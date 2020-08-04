package ca.team2706.fvts.core;

import java.io.File;

public class LibraryLoader {
	public static final String OPENCV_LIBRARY = "opencv_java347";
	public static final String NTCORE_LIBRARY = "ntcorejni";
	public static final String CSCORE_LIBRARY = "cscorejni";
	public static final String WPIUTIL_LIBRARY = "wpiutiljni";
	
	public static final void findAndLoadOpenCV(String overrideOpenCV) {
		if(overrideOpenCV != null) {
			System.load(new File(overrideOpenCV).getAbsolutePath());
			return;
		}
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
			if(f.exists()) {
				System.load(f.getAbsolutePath());
				System.out.println(f.getParentFile().getAbsolutePath());
				System.setProperty("java.library.path", f.getParentFile().getAbsolutePath()+":"+lpath);
				return;
			}
		}
		throw new RuntimeException("Could not find OpenCV!!!");
	}
	public static final void loadLibraries(String cvOverride) {
		findAndLoadOpenCV(cvOverride);
	}
	public static final void loadLibraries() {
		loadLibraries(null);
	}
}
