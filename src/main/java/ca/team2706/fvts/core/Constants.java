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
		if(os.toLowerCase().contains("windows")) {
			if(arch.contains("64")) {
				return new File(lpath,"windows/x86_64/"+OPENCV_LIBRARY+".dll").getPath();
			}else {
				return new File(lpath,"windows/x86/"+OPENCV_LIBRARY+".dll").getPath();
			}
		}
		if(os.toLowerCase().contains("linux")) {
			if(arch.toLowerCase().contains("arm")) {
				return new File(lpath,"linux/raspbian/lib"+OPENCV_LIBRARY+".so").getPath();
			}else if(arch.contains("x86")) {
				return new File(lpath,"linux/x86/lib"+OPENCV_LIBRARY+".so").getPath();
			}else {
				return new File(lpath,"linux/x86-64/lib"+OPENCV_LIBRARY+".so").getPath();
			}
		}
		return null;
	}
}
