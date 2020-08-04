package ca.team2706.fvts.core;

import java.io.File;

public class LibraryLoader {
	public static final String OPENCV_LIBRARY = "opencv_java347";
	public static final String NTCORE_LIBRARY = "ntcorejni";
	public static final String CSCORE_LIBRARY = "cscorejni";
	
	public static final void loadOpenCV() {
		loadLibraries(null, null, null);
	}
	public static final void findAndLoadOpenCV(String overrideOpenCV) {
		if(overrideOpenCV != null) {
			System.load(overrideOpenCV);
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
				return;
			}
		}
		throw new RuntimeException("Could not find OpenCV!!!");
	}
	public static final void findAndLoadNTCore(String ntOverride) {
		if(ntOverride != null) {
			System.load(ntOverride);
			return;
		}
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String lpath = System.getProperty("java.library.path");
		String path = "";
		if(os.toLowerCase().contains("windows")) {
			if(arch.contains("64")) {
				path = "windows/x86_64/"+NTCORE_LIBRARY+".dll";
			}else {
				path = "windows/x86/"+NTCORE_LIBRARY+".dll";
			}
		}
		if(os.toLowerCase().contains("linux")) {
			if(arch.toLowerCase().contains("arm")) {
				path = "linux/raspbian/lib"+NTCORE_LIBRARY+".so";
			}else if(arch.contains("x86")) {
				path = "linux/x86/lib"+NTCORE_LIBRARY+".so";
			}else {
				path = "linux/x86-64/lib"+NTCORE_LIBRARY+".so";
			}
		}
		if(path.isEmpty())
			throw new RuntimeException("Could not find NTCore!!!");
		String[] paths = lpath.split(":");
		for(String s : paths) {
			File f = new File(s,path);
			if(f.exists()) {
				System.load(f.getAbsolutePath());
				return;
			}
		}
		throw new RuntimeException("Could not find NTCore!!!");
	}
	public static final void findAndLoadCSCore(String csOverride) {
		if(csOverride != null) {
			System.load(csOverride);
			return;
		}
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String lpath = System.getProperty("java.library.path");
		String path = "";
		if(os.toLowerCase().contains("windows")) {
			if(arch.contains("64")) {
				path = "windows/x86_64/"+CSCORE_LIBRARY+".dll";
			}else {
				path = "windows/x86/"+CSCORE_LIBRARY+".dll";
			}
		}
		if(os.toLowerCase().contains("linux")) {
			if(arch.toLowerCase().contains("arm")) {
				path = "linux/raspbian/lib"+CSCORE_LIBRARY+".so";
			}else if(arch.contains("x86")) {
				path = "linux/x86/lib"+CSCORE_LIBRARY+".so";
			}else {
				path = "linux/x86-64/lib"+CSCORE_LIBRARY+".so";
			}
		}
		if(path.isEmpty())
			throw new RuntimeException("Could not find CSCore!!!");
		String[] paths = lpath.split(":");
		for(String s : paths) {
			File f = new File(s,path);
			if(f.exists()) {
				System.load(f.getAbsolutePath());
				return;
			}
		}
		throw new RuntimeException("Could not find CSCore!!!");
	}
	public static final void loadLibraries(String cvOverride, String ntOverride, String csOverride) {
		findAndLoadOpenCV(cvOverride);
		findAndLoadCSCore(csOverride);
		findAndLoadNTCore(ntOverride);
	}
}
