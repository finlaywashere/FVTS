package ca.team2706.fvts.core;

import java.io.File;

public class Constants {
	public static final String OPENCV_LIBRARY = "opencv_java310";
	public static final int MAJOR_VERSION = 2;
	public static final int MINOR_VERSION = 0;
	public static final String AUTHOR = "Merge Robotics";
	public static final String NAME = "FVTS";
	
	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;
	
	public static final String VERSION_STRING = "v"+MAJOR_VERSION+"."+MINOR_VERSION;
	
	public static File MODULES_FOLDER = new File("modules/");
}
