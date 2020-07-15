package ca.team2706.fvts.core;

import java.io.File;

public class Constants {
	public static final String OPENCV_LIBRARY = "opencv_java347";
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 2;
	public static final String AUTHOR = "Merge Robotics";
	public static final String NAME = "FVTS";
	
	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;
	
	public static final String VERSION_STRING = "v"+MAJOR_VERSION+"."+MINOR_VERSION;
	
	public static File MODULES_FOLDER = new File("modules/");
	
	public static final String OPENCV() {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String lpath = System.getProperty("java.library.path");
		String path = "";
		if(os.toLowerCase().contains("windows")) {
			if(arch.contains("64")) {
				path = "windows/x86_64/"+OPENCV_LIBRARY+".dll";
			}else {
				path = "windows/x86/"+OPENCV_LIBRARY+".dll";
			}
		}
		if(os.toLowerCase().contains("linux")) {
			if(arch.toLowerCase().contains("arm")) {
				path = "linux/raspbian/lib"+OPENCV_LIBRARY+".so";
			}else if(arch.contains("x86")) {
				path = "linux/x86/lib"+OPENCV_LIBRARY+".so";
			}else {
				path = "linux/x86-64/lib"+OPENCV_LIBRARY+".so";
			}
		}
		if(path.isEmpty())
			throw new RuntimeException("Could not find OpenCV!!!");
		String[] paths = lpath.split(":");
		for(String s : paths) {
			File f = new File(s,path);
			if(f.exists())
				return f.getPath();
		}
		throw new RuntimeException("Could not find OpenCV!!!");
	}
}
