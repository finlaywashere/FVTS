package ca.team2706.fvts.core.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.Utils;

public class ImageDummyInputDevice extends AbstractInputDevice{

	public ImageDummyInputDevice() {
		super("image",true);
	}

	private Map<String,Mat> frames = new HashMap<String,Mat>();
	@Override
	public void init(String identifier) throws Exception{
		if(!frames.containsKey(identifier)) {
			if(!new File(identifier).exists()) {
				throw new FileNotFoundException("Failed to find image file "+identifier);
			}
			Mat frame = Utils.bufferedImageToMat(ImageIO.read(new File(identifier)));
			frames.put(identifier, frame);
		}
	}

	@Override
	public Mat getFrame(String identifier) {
		return frames.get(identifier);
	}
	
}
