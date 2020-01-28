package ca.team2706.mergevision.core;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.first.cameraserver.CameraServer;

public class VisionCameraServer extends Thread {

	public static Map<Integer, VideoCapture> cameras = new HashMap<Integer, VideoCapture>();

	public static Map<String, CvSink> servers = new HashMap<String, CvSink>();
	
	public static void startServer() {
		new VisionCameraServer().start();
	}

	public static void initCamera(String type, String identifier) throws Exception {

		if (type.equals("usb")) {
			
			int id = Integer.valueOf(identifier);

			if(cameras.containsKey(id) || id == -1) {
				return;
			}
			
			VideoCapture capture = new VideoCapture(id);
			
			Log.i("Waiting for camera to respond!",true);
			
			while(!capture.isOpened()) {
				Thread.sleep(10);
			}
			
			Log.i("Camera successfully connected",true);

			if (frame1 == null) {
				frame1 = new Mat();
			}

			if(!capture.read(frame1)) {
				
				Log.e("Failed to connect to camera #"+identifier,true);
				
				System.exit(1);
				
			}

			cameras.put(id, capture);

			frames.put(id, frame1);

		}else if(type.equals("mjpegserver")) {
			
			String[] data = identifier.split(":");
			
			MjpegServer server;
			
			if(data.length > 1) {
				
				server = CameraServer.getInstance().addServer(data[0], Integer.valueOf(data[1]));
				
			}else {
				
				server = CameraServer.getInstance().addServer(identifier);
				
			}
			
			CvSink sink1 = new CvSink(server.getName());
			
			sink1.setSource(server.getSource());
			
			servers.put(identifier, sink1);
			
		}

	}

	private static Mat frame1;
	private static Map<Integer, Mat> frames = new HashMap<Integer, Mat>();

	private static Map<String, Mat> framess = new HashMap<String,Mat>();
	
	public static Mat getFrame(String type, String identifier) {
		
		if(type.equals("usb")) {
			return frames.get(Integer.valueOf(identifier));
		}else if(type.equals("mjpegserver")) {
			return framess.get(identifier);
		}
		
		return null;
		
	}

	private Mat frame;

	@Override
	public void run() {

		while (true) {

			for (int i : cameras.keySet()) {

				VideoCapture capture = cameras.get(i);

				if (frame == null) {
					frame = new Mat();
				}

				if (capture.read(frame)) {

					frames.put(i, frame);

				} else {
					Log.e("Failed to read from camera " + i,true);
				}
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Log.e(e.getMessage(), true);
			}
		}

	}

	public static void update() {

		Mat frame = null;

		for (int i : cameras.keySet()) {

			VideoCapture capture = cameras.get(i);

			if (frame == null) {
				frame = new Mat();
			}

			if (capture.read(frame)) {

				frames.put(i, frame);

			} else {
				Log.e("Failed to read from camera " + i,true);
			}

		}
		
		for(String s : servers.keySet()) {
			
			CvSink sink = servers.get(s);
			
			long l = sink.grabFrame(frame);
			
			if(l != 0) {
				
				framess.put(s,frame);
				
			}
			
		}

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Log.e(e.getMessage(), true);
		}
	}

}