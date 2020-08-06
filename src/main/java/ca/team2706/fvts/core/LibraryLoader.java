package ca.team2706.fvts.core;

import java.io.File;

public class LibraryLoader {
	public static final String OPENCV_LIBRARY = "opencv_java347";
	
	public static final void findAndLoadOpenCV(String overrideOpenCV) {
		if(overrideOpenCV != null) {
			Log.d("Loading OpenCV from "+new File(overrideOpenCV).getAbsolutePath(), true);
			System.load(new File(overrideOpenCV).getAbsolutePath());
			return;
		}
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String lpath = System.getProperty("java.library.path");
		String path = "";
		String path2 = "";
		if(os.toLowerCase().contains("windows")) {
			if(arch.contains("64")) {
				path = "windows/x86_64/"+OPENCV_LIBRARY+".dll";
			}else {
				path = "windows/x86/"+OPENCV_LIBRARY+".dll";
			}
			path2 = OPENCV_LIBRARY+".dll";
		}
		if(os.toLowerCase().contains("linux")) {
			if(arch.toLowerCase().contains("arm")) {
				path = "linux/raspbian/lib"+OPENCV_LIBRARY+".so";
			}else if(arch.contains("x86")) {
				path = "linux/x86/lib"+OPENCV_LIBRARY+".so";
			}else {
				path = "linux/x86-64/lib"+OPENCV_LIBRARY+".so";
			}
			path2 = "lib"+OPENCV_LIBRARY+".so";
		}
		if(path.isEmpty())
			throw new RuntimeException("Could not find OpenCV!!!");
		String[] paths = lpath.split(":");
		for(String s : paths) {
			File f = new File(s,path);
			if(f.exists()) {
				Log.d("Loading OpenCV from "+f.getAbsolutePath(), true);
				System.load(f.getAbsolutePath());
				return;
			}
			File f2 = new File(s,path2);
			if(f2.exists()) {
				Log.d("Loading OpenCV from "+f.getAbsolutePath(), true);
				System.load(f2.getAbsolutePath());
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
