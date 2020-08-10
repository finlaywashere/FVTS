package ca.team2706.fvts.core;

import java.awt.image.BufferedImage;
import java.io.File;

import ca.team2706.fvts.core.params.VisionParams;

public class Bundle {
	private BufferedImage raw,binMask;
	private int timestamp;
	private VisionParams params;
	private String suffix;
	private File outputDir;

	public Bundle(BufferedImage raw, BufferedImage binMask,int timestamp, VisionParams params, String suffix, File outputDir) {
		this.raw = raw;
		this.binMask = binMask;
		this.timestamp = timestamp;
		this.params = params;
		this.suffix = suffix;
		this.outputDir = outputDir;
	}

	public BufferedImage getBinMask() {
		return binMask;
	}

	public BufferedImage getRaw() {
		return raw;
	}

	public int getTimeStamp(){
		return timestamp;
	}

	public VisionParams getParams() {
		return params;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public String getSuffix() {
		return suffix;
	}

	public File getOutputDir() {
		return outputDir;
	}
	
}
