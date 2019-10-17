package ca.team2706.vision.vision2019;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	public static ParamsSelector selector;
	public static File timestampfile;
	private static File visionParamsFile;
	public static boolean developmentMode = false;

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
		/**
		 * This is how many times the pipeline will erode dilate the camera image
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
		
		public String type,identifier;
		
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
		List<Target> targetsFound = new ArrayList<Target>();
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
		try {
			List<String> lists = ConfigParser.listLists(visionParamsFile);

			for (String s : lists) {

				VisionParams visionParams = new VisionParams();

				Map<String, String> data = ConfigParser.getProperties(visionParamsFile, s);

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

		} catch (Exception e1) {
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
		
	}

	/**
	 * Saves the vision parameters to a file
	 * 
	 **/
	public static void saveVisionParams() {

		try {

			for (VisionParams params : visionParamsList) {

				saveVisionParams(params);

			}

		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

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

		ConfigParser.saveList(visionParamsFile, params.name, data);
	}

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo rio
	 * :]
	 *
	 * @param visionData
	 */
	public static void sendVisionDataOverNetworkTables(VisionData visionData) {

		NetworkTable visionTable = visionData.params.table;

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
			
			visionTable.putNumber("angle", visionData.preferredTarget.xCentreNorm*45);
		}
	}

	/**
	 * Converts a OpenCV Matrix to a BufferedImage :)
	 *
	 * @param matrix Matrix to be converted
	 * @return Generated from the matrix
	 * @throws IOException
	 * @throws Exception
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		matrix.release();
		return bi;
	}

	/**
	 * Converts a Buffered Image to a OpenCV Matrix
	 * 
	 * @param Buffered Image to convert to matrix
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

	}

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * 
	 * @param The command line arguments
	 */
	public static void main(String[] args) throws Exception{

		// Must be included!
		// Loads OpenCV
		System.loadLibrary("opencv_java310");
		
		Options options = new Options();
		
		Option ip = new Option("ip",true,"The IP address of the NetworkTables server");
		options.addOption(ip);
		Option developmentMode = new Option("dev","development",false,"Puts Vision2019 in development mode");
		options.addOption(developmentMode);
		Option configFile = new Option("conf","config",true,"Specifies an alternative config file");
		options.addOption(configFile);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		}catch(Exception e) {
			e.printStackTrace();
			formatter.printHelp("Vision2019", options);
			System.exit(1);
		}
		Main.developmentMode = cmd.hasOption("development");
		
		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables(cmd.getOptionValue("ip",""));

		visionParamsFile = new File(cmd.getOptionValue("config","visionParams.properties"));
		
		// read the vision calibration values from file.
		loadVisionParams();
		
		Map<String,String> masterConfig = ConfigParser.getProperties(new File("master.cf"), "config");
		
		Map<String,String> masterEnabled = ConfigParser.getProperties(new File("master.cf"), "enabled");
		
		String allowOverride = masterConfig.get("allowOverride");
		
		if(allowOverride == null || allowOverride.equals("")) {
			
			allowOverride = "true";
			
		}
		
		boolean allowOverrideB = Boolean.valueOf(allowOverride);
		
		if(allowOverrideB)
			NetworkTablesManager.init();

		ImageDumpScheduler.start();

		VisionCameraServer.startServer();

		for (VisionParams params : visionParamsList) {
			try {
				
				String s = masterEnabled.get(params.name);
				
				if(s == null || s.equals("")) {
					s = "true";
				}
				
				boolean enabled = Boolean.valueOf(s);
				
				params.enabled = enabled;
				
				VisionCameraServer.initCamera(params.type,params.identifier);
				MainThread thread = new MainThread(params);
				if(enabled) {
					thread.start();
				}
				threads.add(thread);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	} // end main video processing loop
}
