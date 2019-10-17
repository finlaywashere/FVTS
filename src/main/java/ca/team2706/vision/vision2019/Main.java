package ca.team2706.vision.vision2019;

import java.awt.Color;
import java.awt.Graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import org.opencv.core.MatOfPoint;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	public static String filename = "";
	public static ParamsSelector selector;
	public static int timestamp = 0;
	public static File timestampfile;
	public static BufferedImage currentImage;
	public static VideoCapture camera;
	public static VisionData lastData;
	public static boolean process = true;
	public static boolean showMiddle = false;
	public static boolean useCamera = true;
	public static NetworkTable loggingTable;
	public static Mat frame;

	public static void setFrame(Mat f) {
		frame = f;
	}

	public static List<MainThread> threads = new ArrayList<MainThread>();

	public static void reloadConfig() {
		visionParamsList.clear();
		loadVisionParams();
		for (MainThread thread : threads) {
			String name = thread.visionParams.name;
			boolean found = false;
			for (VisionParams params : visionParamsList) {
				if (!found) {
					if (params.name.equals(name)) {
						thread.updateParams(params);
						found = true;
					}
				}

			}

		}
	}

	// Camera Type (set in visionParams.properties)
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/
	/** The vision parameters, this is used by the vision pipeline **/
	public static List<VisionParams> visionParamsList = new ArrayList<VisionParams>();

	/**
	 * A class to hold calibration parameters for the image processing algorithm
	 */
	public static class VisionParams {
		int group;

		/** This is the minimum hue that the pipeline will recognize **/
		int minHue;
		/** This is the maximum hue that the pipeline will recognize **/
		int maxHue;
		/** This is the minimum saturation that the pipeline will recognize **/
		int minSaturation;
		/** This is the maximum saturation that the pipeline will recognize **/
		int maxSaturation;
		/** This is the minimum value that the pipeline will recognize **/
		int minValue;
		/** This is the maximum value that the pipeline will recognize **/
		int maxValue;
		/** This is the id of the camera that will be used to get images **/
		int cameraSelect;
		/**
		 * The threshold to detect one large cube as 2 cubes, this is a value between 0
		 * and 1 This is how many times the pipeline will erode dilate the camera image
		 **/
		int erodeDilateIterations;

		double slope, yIntercept;

		/**
		 * The threshold to detect one large cube as 2 cubes, this is a value between 0
		 * and 1
		 **/
		double aspectRatioThresh;
		/** The minimum area that a target can have and still be recognized **/
		double minArea;
		/**
		 * How important it is for a target to be close to the center of the image, this
		 * will change depending on how well we can turn
		 * 
		 **/
		double distToCentreImportance;
		/** The width to resize the image from the camera to **/
		int width;
		/** The height to resize the image from the camera to **/
		int height;
		/**
		 * The size to resize the image from the camera to, this is just the width and
		 * the height values
		 **/
		Size sz;
		/** This is the image to be processed if the selected camera is -1 **/
		String imageFile;

		public NetworkTable table;

		public String name;

		public String outputPath;

		public double secondsBetweenImageDumps;

		public boolean enabled;

		public String type, identifier;
	}

	/**
	 * A class to hold any visionTable data returned by process() :) :) :} :] :]
	 */

	public static class VisionData {

		public static class Target {
			double distance;

			MatOfPoint contour;

			/** The x center of the target in the image **/
			int xCentre;
			/**
			 * The normalized x center of the target that is between 0 and 1
			 **/
			double xCentreNorm;
			/** The y center of the target in the image **/
			int yCentre;
			/**
			 * The normalized y center of the target that is between 0 and 1
			 **/
			double yCentreNorm;
			/**
			 * A value between 0 and 1 representing the percentage of the image the target
			 * takes up
			 **/
			double areaNorm; // [0,1] representing how much of the screen it
								// occupies
			/** The rectangle made from x and y centers **/
			Rect boundingBox;
		}

		/** The List of all the targets in the image **/

		ArrayList<Target> targetsFound = new ArrayList<Target>();
		/**
		 * The target that is the most appealing, how it is chosen depends on the
		 * distToCenterImportance value in the vision parameters
		 **/
		Target preferredTarget;
		/** The image that contains the targets **/
		public Mat outputImg = new Mat();
		/** The frames per second **/
		public Mat binMask = new Mat();
		public double fps;
		public VisionParams params;
	}

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	public static void initNetworkTables(String ip) {

		// Tells the NetworkTable class that this is a client
		NetworkTable.setClientMode();
		// Sets the interval for updating NetworkTables
		NetworkTable.setUpdateRate(0.02);
		// Sets the vision table to the "vision" table that is in NetworkTables
		loggingTable = NetworkTable.getTable("logging-level");

		boolean use_GUI = true;
		
		// If on Linux don't use guis
		if (System.getProperty("os.arch").toLowerCase().indexOf("arm") != -1) {
			use_GUI = false;
		}
		
		if (!use_GUI && ip.equals("")) {

			// Sets the team number
			NetworkTable.setTeam(2706); // Use this for the robit
			// Enables DSClient
			NetworkTable.setDSClientEnabled(true); // and this for the robit

		} else {

			if(ip.equals("")) {
				ip = "localhost";
			}
			
			// Sets the IP adress to connect to
			NetworkTable.setIPAddress(ip); // Use this for testing

		}

		// Initilizes NetworkTables
		NetworkTable.initialize();
	}

	/**
	 * Loads the visionTable params! :]
	 **/

	public static void loadVisionParams() {
		try
	{

		File configFile = new File("visionParams.properties");

		List<String> lists = ConfigParser.listLists(configFile);

		for (String s : lists) {

			VisionParams visionParams = new VisionParams();

			Map<String, String> data = ConfigParser.getProperties(configFile, s);

			visionParams.name = s;

			visionParams.minHue = Integer.valueOf(data.get("minHue"));
			visionParams.maxHue = Integer.valueOf(data.get("maxHue"));
			visionParams.minSaturation = Integer.valueOf(data.get("minSaturation"));
			visionParams.maxSaturation = Integer.valueOf(data.get("maxSaturation"));
			visionParams.minValue = Integer.valueOf(data.get("minValue"));
			visionParams.maxValue = Integer.valueOf(data.get("maxValue"));

			visionParams.aspectRatioThresh = Double.valueOf(data.get("aspectRatioThresh"));

			visionParams.distToCentreImportance = Double.valueOf(data.get("distToCenterImportance"));

			visionParams.imageFile = data.get("imageFile");

			visionParams.minArea = Double.valueOf(data.get("minArea"));

			visionParams.erodeDilateIterations = Integer.valueOf(data.get("erodeDilateIterations"));

			String resolution = data.get("resolution");

			visionParams.width = Integer.valueOf(resolution.split("x")[0]);
			visionParams.height = Integer.valueOf(resolution.split("x")[1]);

			// Set the vision parameters size
			visionParams.sz = new Size(visionParams.width, visionParams.height);

			visionParams.table = NetworkTable.getTable("vision-" + s);

			visionParams.outputPath = data.get("imgDumpPath");

			visionParams.secondsBetweenImageDumps = Double.valueOf(data.get("imgDumpTime"));

			visionParams.slope = Double.valueOf(data.get("slope"));

			visionParams.yIntercept = Double.valueOf(data.get("yIntercept"));

			visionParams.group = Integer.valueOf(data.get("group"));

			visionParams.type = data.get("type");

			visionParams.identifier = data.get("identifier");

			visionParamsList.add(visionParams);

		}

		sendVisionParams();

	}catch(
	Exception e1)
	{
		e1.printStackTrace();
		System.err.println("\n\nError reading the params file, check if the file is corrupt?");
		System.exit(1);
	}
	}

	private static void sendVisionParams() {
		
		for(VisionParams params : visionParamsList) {
			
			NetworkTable visionTable = NetworkTable.getTable("vision-" + params.name+"/params");
			
			visionTable.putNumber("group", params.group);
			visionTable.putNumber("yIntercept", params.yIntercept);
			visionTable.putNumber("slope", params.slope);
			visionTable.putNumber("secondsBetweenImageDumps", params.secondsBetweenImageDumps);
			visionTable.putNumber("height", params.height);
			visionTable.putNumber("width", params.width);
			visionTable.putNumber("erodeDilateIterations", params.erodeDilateIterations);
			visionTable.putNumber("minArea", params.minArea);
			visionTable.putString("imageFile", params.imageFile);
			visionTable.putNumber("distToCenterImportance", params.distToCentreImportance);
			visionTable.putNumber("aspectRatioThresh", params.aspectRatioThresh);
			visionTable.putString("type", params.type);
			visionTable.putString("identifier", params.identifier);
			visionTable.putNumber("minHue", params.minHue);
			visionTable.putNumber("maxHue", params.maxHue);
			visionTable.putNumber("minSaturation", params.minSaturation);
			visionTable.putNumber("maxSaturation", params.maxSaturation);
			visionTable.putNumber("minValue", params.minValue);
			visionTable.putNumber("maxValue", params.maxValue);
			
		}
		
	}>>>>>>>master

	/**
	 * Saves the vision parameters to a file
	 * 
	 **/
	public static void saveVisionParams() {
<<<<<<< HEAD
		// Initilizes the properties object
		Properties properties = new Properties();
		try {
			// Sets the camera select property in the file to the camera select
			// value
			properties.setProperty("CameraSelect", String.valueOf(visionParams.cameraSelect));
			// Sets the minimum hue property in the file to the minimum hue
			// value
			properties.setProperty("minHue", String.valueOf(visionParams.minHue));
			// Sets the maximum hue property in the file to the maximum hue
			// value
			properties.setProperty("maxHue", String.valueOf(visionParams.maxHue));
			// Sets the minimum saturation property in the file to the minimum
			// saturation value
			properties.setProperty("minSaturation", String.valueOf(visionParams.minSaturation));
			// Sets the maximum saturation property in the file to the maximum
			// saturation value
			properties.setProperty("maxSaturation", String.valueOf(visionParams.maxSaturation));
			// Sets the minimum value property in the file to the minimum value
			// value
			properties.setProperty("minValue", String.valueOf(visionParams.minValue));
			// Sets the maximum value property in the file to the maximum value
			// value
			properties.setProperty("maxValue", String.valueOf(visionParams.maxValue));
			// Sets the erode dilate iterations property in the file to the
			// erode dilate iterations value
			properties.setProperty("erodeDilateIterations", String.valueOf(visionParams.erodeDilateIterations));
			// Sets the minimum area property in the file to the minimum area
			// value
			properties.setProperty("minArea", String.valueOf(visionParams.minArea));
			// Sets the aspect ratio threshold property in the file to the
			// aspect ratio threshold value
			properties.setProperty("aspectRatioThresh", String.valueOf(visionParams.aspectRatioThresh));
			// Sets the distance to center importance property in the file to
			// the distance to center importance value
			properties.setProperty("distToCentreImportance", String.valueOf(visionParams.distToCentreImportance));
			// Sets the image file property in the file to the image file value
			properties.setProperty("imageFile", visionParams.imageFile);
			// Sets the resolution property in the file to the resolution value
			properties.setProperty("resolution", visionParams.width + "x" + visionParams.height);
			// Sets the image dumping interval property in the file to the image
			// dumping interval value
			properties.setProperty("imgDumpWait", String.valueOf(seconds_between_img_dumps));
			// Sets the image dumping path property in the file to the image
			// dumping path value
			properties.setProperty("imgDumpPath", outputPath);
			// Initilizes the output stream to the vision parameters file
			FileOutputStream out = new FileOutputStream("visionParams.properties");
			// Dumps the properties to the output stream
			properties.store(out, "");
		} catch (Exception e1) {
			Log.e(e1.getMessage(), true);
			Log.e("Error saving properties file", true);
=======

		try {

			for (VisionParams params : visionParamsList) {

				saveVisionParams(params);

			}

		} catch (Exception e1) {
			e1.printStackTrace();
>>>>>>> master
			System.exit(1);
		}
	}

<<<<<<< HEAD

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo
	 * rio :]
=======
	public static void saveVisionParams(VisionParams params) throws Exception {
		Map<String, String> data = new HashMap<String, String>();

		data.put("type", params.type);
		data.put("identifier", params.identifier);
		data.put("minHue", String.valueOf(params.minHue));
		data.put("maxHue", String.valueOf(params.maxHue));
		data.put("minSaturation", String.valueOf(params.minSaturation));
		data.put("maxSaturation", String.valueOf(params.maxSaturation));
		data.put("minValue", String.valueOf(params.minValue));
		data.put("maxValue", String.valueOf(params.maxValue));

		data.put("aspectRatioThresh", String.valueOf(params.aspectRatioThresh));

		data.put("distToCenterImportance", String.valueOf(params.distToCentreImportance));

		data.put("imageFile", params.imageFile);

		data.put("minArea", String.valueOf(params.minArea));

		data.put("erodeDilateIterations", String.valueOf(params.erodeDilateIterations));

		data.put("resolution", params.width + "x" + params.height);

		data.put("imgDumpPath", params.outputPath);

		data.put("imgDumpTime", String.valueOf(params.secondsBetweenImageDumps));

		data.put("slope", String.valueOf(params.slope));

		data.put("yIntercept", String.valueOf(params.yIntercept));

		data.put("group", String.valueOf(params.group));

		ConfigParser.saveList(new File("visionParams.properties"), params.name, data);
	}

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo rio
	 * :]
>>>>>>> master
	 *
	 * @param visionData
	 */
	public static void sendVisionDataOverNetworkTables(VisionData visionData) {

<<<<<<< HEAD
=======
		NetworkTable visionTable = visionData.params.table;

>>>>>>> master
		// Sends the data
		// Puts the fps into the vision table
		visionTable.putNumber("fps", visionData.fps);
		// Puts the number of targets found into the vision table
		visionTable.putNumber("numTargetsFound", visionData.targetsFound.size());

		// If there is a target
		if (visionData.preferredTarget != null) {
			// Put the normalized x into the vision table
			visionTable.putNumber("ctrX", visionData.preferredTarget.xCentreNorm);
			// Puts the normalized area into the vision table
			visionTable.putNumber("area", visionData.preferredTarget.areaNorm);
<<<<<<< HEAD
=======
			
			visionTable.putNumber("angle", visionData.preferredTarget.xCentreNorm*45);
>>>>>>> master
		}
	}

	/**
	 * Converts a OpenCV Matrix to a BufferedImage :)
	 *
	 * <<<<<<< HEAD
	 * 
	 * @param matrix Matrix to be converted =======
	 * @param matrix Matrix to be converted >>>>>>> master
	 * @return Generated from the matrix
	 * @throws IOException
	 * @throws Exception
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	/**
	 * Converts a Buffered Image to a OpenCV Matrix
	 * 
	 * <<<<<<< HEAD
	 * 
	 * @param Buffered Image to convert to matrix =======
	 * @param Buffered Image to convert to matrix >>>>>>> master
	 * @return The matrix from the buffered image
	 */

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	/**
	 * 
<<<<<<< HEAD
	 * @param The
	 *            image to dump to a file
	 * @param image
	 *            the image to be dumped
	 * @param suffix
	 *            the suffix to put on the file name
	 * @throws IOException
	 */

	public static void imgDump(BufferedImage image, String suffix, int timestamp) throws IOException {
		// prepend the file name with the tamestamp integer, left-padded with
		// zeros so it sorts properly
		@SuppressWarnings("deprecation")
		String match = loggingTable.getString("match");
		if(match.equals("")){
			match = "practice";
		}
		
		File output = new File(outputPath +match+"-"+String.format("%05d", timestamp) + "_" + suffix + ".png");
=======
	 * @param The    image to dump to a file
	 * @param image  the image to be dumped
	 * @param suffix the suffix to put on the file name
	 * @throws IOException
	 */

	public static void imgDump(BufferedImage image, String suffix, int timestamp, VisionParams params)
			throws IOException {
		// prepend the file name with the tamestamp integer, left-padded with
		// zeros so it sorts properly
		File output = new File(params.outputPath + String.format("%05d", timestamp) + "_" + suffix + ".png");
>>>>>>> master
		try {
			ImageIO.write(image, "PNG", output);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		timestampfile.delete();
		timestampfile.createNewFile();
		PrintWriter out = new PrintWriter(timestampfile);
		out.println(timestamp);
		out.close();
<<<<<<< HEAD
		
	}

	public static boolean b = true;

	=======

	}

	>>>>>>>master

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * 
<<<<<<< HEAD
	 * @param The
	 *            command line arguments
	 */
	public static void main(String[] args) {
=======
	 * @param The command line arguments
	 */

	public static void main(String[] args) throws Exception{

>>>>>>> master
		// Must be included!
		// Loads OpenCV
		System.loadLibrary("opencv_java310");

<<<<<<< HEAD
		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables();

		// read the vision calibration values from file.
		loadVisionParams();

		try {
			// Copys the vision parameters to a usb flash drive
			Files.copy(Paths.get("visionParams.properties"),
					Paths.get(outputPath + "/visionParams-" + timestamp + ".properties"),
					StandardCopyOption.REPLACE_EXISTING);
			Log.i("Saved visionparams.properties!", true);
		} catch (IOException e2) {
			Log.e(e2.getMessage(), true);
		}
		// Initilizes a Matrix to hold the frame

		frame = new Mat();

		// Open a connection to the camera
		VideoCapture camera = null;
		CLI.startServer();
		// Whether to use a camera, or load an image file from disk.
		if (visionParams.cameraSelect == -1) {
			useCamera = false;
		}

		if (useCamera) {
			// Initilizes the camera
			camera = new VideoCapture(visionParams.cameraSelect);

			// Sets camera parameters
			int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
			camera.set(Videoio.CAP_PROP_FOURCC, fourcc);
			camera.set(Videoio.CAP_PROP_FRAME_WIDTH, visionParams.width);
			camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, visionParams.height);

			camera.read(frame);

			if (!camera.isOpened()) {
				// If the camera didn't open throw an error
				System.err.println("Error: Can not connect to camera");
				CLI.log("Error: Can not connect to camera");
				// Exit
				System.exit(1);
			}

			// Set up the camera feed
			camera.read(frame);
		} else {
			// load the image from file.
			try {
				frame = bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
			} catch (IOException e) {
				Log.e(e.getMessage(), true);
				frame = new Mat();
			}
		}
		
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = true;
		// If on Linux don't use guis
		if (System.getProperty("os.name").toLowerCase().indexOf("raspbian") != -1) {
			use_GUI = false;
		}
		// Set the vision parameters size
		visionParams.sz = new Size(visionParams.width, visionParams.height);
		if (use_GUI) {
			// Resizes the frame to the vision parameters size
			Imgproc.resize(frame, frame, visionParams.sz);
		}
		// Set up the GUI display windows
		if (use_GUI) {
			try {
				// Initilizes the window to display the raw image
				guiRawImg = new DisplayGui(matToBufferedImage(frame), "Raw Camera Image",true);
				// Initilizes the window to display the processed image
				guiProcessedImg = new DisplayGui(matToBufferedImage(frame), "Processed Image",true);
				// Initilizes the parameters selector
				cli = new CLI();
				ParamsSelector selector = new ParamsSelector(true,true);
				guiRawImg.addKeyListener(selector);
				guiProcessedImg.addKeyListener(selector);
			} catch (IOException e) {
				Log.e("Mat2BufferedImage broke! Non-fatal error", true);
				Log.e(e.getMessage(), true);
				// means mat2BufferedImage broke
				// non-fatal error, let the program continue
			}
		}
		ImageDumpScheduler.start();
		// Main video processing loop
		while (b) {
			if (useCamera) {
				// Read the frame from the camera, if it fails try again
				if (!camera.read(frame)) {
					Log.e("Error: Failed to get a frame from the camera",true);
					continue;
				}
			} // else use the image from disk that we loaded above
			else{
				// load the image from file.
	            try {
        	        frame = bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
                } catch (IOException e) {
                	e.printStackTrace();
                    frame = new Mat();
                }

			}
			if (use_GUI) {
				// Resize the frame
				Imgproc.resize(frame, frame, visionParams.sz);
			}
			// Process the frame!
			// Log when the pipeline starts
			long pipelineStart = System.nanoTime();
			// Process the frame
			VisionData visionData = Pipeline.process(frame, visionParams, use_GUI);
			// Log when the pipeline stops
			long pipelineEnd = System.nanoTime();
			// Selects the prefered target
			Pipeline.selectPreferredTarget(visionData, visionParams);
			// Creates the raw output image object
			Mat rawOutputImg;
			if (use_GUI) {
				// If use gui then draw the prefered target
				// Sets the raw image to the frame
				rawOutputImg = frame.clone();
				// Draws the preffered target
				Pipeline.drawPreferredTarget(rawOutputImg, visionData);
			} else {
				// Sets the raw image to the frame
				rawOutputImg = frame;
			}
			// Sends the data to the vision table
			sendVisionDataOverNetworkTables(visionData);
			lastData = visionData;
			// display the processed frame in the GUI
			if (use_GUI) {
				try {
					// May throw a NullPointerException if initializing
					// the window failed
					BufferedImage raw = matToBufferedImage(rawOutputImg);
					currentImage = raw;
					if (showMiddle) {
						Graphics g = raw.getGraphics();
						g.setColor(Color.RED);
						g.fillOval(raw.getWidth() / 2 - 8, raw.getHeight() / 2 - 8, 8, 8);
						g.dispose();
					}
					guiRawImg.updateImage(raw);
					guiProcessedImg.updateImage(matToBufferedImage(visionData.binMask));
				} catch (IOException e) {
					Log.e("Mat2BufferedImage broke! Non-fatal error", true);
					Log.e(e.getMessage(), true);
					// means mat2BufferedImage broke
					// non-fatal error, let the program continue
					continue;
				} catch (NullPointerException e) {
					e.printStackTrace();
					Log.i("Window closed",false);
					Runtime.getRuntime().halt(0);
				} catch (Exception e) {
					// just in case
					Log.e(e.getMessage(), true);
					continue;
				}
			}
			if (useCamera) {
				// log images to file once every seconds_between_img_dumps
				double elapsedTime = ((double) System.currentTimeMillis() / 1000) - current_time_seconds;
				// If the elapsed time is more that the seconds between image
				// dumps
				// then dump images asynchronously
				if (elapsedTime >= seconds_between_img_dumps) {
					// Sets the current number of seconds
					current_time_seconds = (((double) System.currentTimeMillis()) / 1000);
					// Clones the frame
					Mat finalFrame = frame.clone();
					try {
						Mat draw = finalFrame.clone();
						Pipeline.drawPreferredTarget(draw, visionData);
						Bundle b = new Bundle(matToBufferedImage(finalFrame), matToBufferedImage(visionData.binMask),
								matToBufferedImage(draw), timestamp);
						ImageDumpScheduler.schedule(b);
						timestamp++;
					} catch (IOException e) {
						Log.e(e.getMessage(), true);
						return;
					}
				}
			}
			// Display the frame rate onto the console
			double pipelineTime = (((double) (pipelineEnd - pipelineStart)) / Pipeline.NANOSECONDS_PER_SECOND) * 1000;
			Log.i("Vision FPS: "+visionData.fps+", pipeline took: "+pipelineTime+" ms",false);
		}
	} // end main video processing loop

	public static void hideMiddle() {
		showMiddle = false;
	}

	public static void showMiddle() {
		showMiddle = true;
	}

	public static VisionData forceProcess() {
		Mat frame = new Mat();
		camera.read(frame);
		Imgproc.resize(frame, frame, visionParams.sz);

		VisionData visionData = Pipeline.process(frame, visionParams, false);

		Pipeline.selectPreferredTarget(visionData, visionParams);

		return visionData;
	}

	public static VisionData forceProcess(Mat frame) {
		Imgproc.resize(frame, frame, visionParams.sz);

		VisionData visionData = Pipeline.process(frame, visionParams, false);

		Pipeline.selectPreferredTarget(visionData, visionParams);

		return visionData;
	}

	public static Mat getFrame() {
		Mat frame = new Mat();
		camera.read(frame);
		return frame;
	}=======

	String ip = "";

	if(args.length>0){ip=args[0];}

	// Connect NetworkTables, and get access to the publishing table
	initNetworkTables(ip);

	// read the vision calibration values from file.
	loadVisionParams();

	Map<String, String> masterConfig = ConfigParser.getProperties(new File("master.cf"), "config");

	Map<String, String> masterEnabled = ConfigParser.getProperties(new File("master.cf"), "enabled");

	String allowOverride = masterConfig.get("allowOverride");

	if(allowOverride==null||allowOverride.equals("")){

	allowOverride="true";

	}

	boolean allowOverrideB = Boolean.valueOf(allowOverride);

	if(allowOverrideB)NetworkTablesManager.init();

	ImageDumpScheduler.start();

	VisionCameraServer.startServer();

	for(
	VisionParams params:visionParamsList){try
	{

		String s = masterEnabled.get(params.name);

		if (s == null || s.equals("")) {
			s = "true";
		}

		boolean enabled = Boolean.valueOf(s);

		params.enabled = enabled;

		VisionCameraServer.initCamera(params.type, params.identifier);
		MainThread thread = new MainThread(params);
		if (enabled) {
			thread.start();
		}
		threads.add(thread);
	}catch(
	Exception e)
	{
		e.printStackTrace();
	}
}

} // end main video processing loop
>>>>>>>master}
