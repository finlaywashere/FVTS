package ca.team2706.fvts.test.core.image;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.image.ImageCropPreprocessor;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class ImageCropPreprocessorTest {

	@Test
	public void imageCropPreprocessorTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		System.load(Constants.OPENCV());
		ImageCropPreprocessor crop = new ImageCropPreprocessor();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("crop/cX1","0.25"));
		attribs.add(new Attribute("crop/cY1","0.25"));
		attribs.add(new Attribute("crop/cX2","0.75"));
		attribs.add(new Attribute("crop/cY2","0.75"));
		attribs.add(new Attribute("core/interface","dummy"));
		attribs.add(new Attribute("core/pipeline","dummy"));
		attribs.add(new Attribute("name","test"));
		BufferedImage src = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		Mat srcMat = Utils.bufferedImageToMat(src);
		MainThread main = new MainThread(new VisionParams(attribs, new ArrayList<AttributeOptions>()));
		Mat result = crop.process(srcMat, main);
		assertEquals(320, result.cols());
		assertEquals(240, result.rows());
	}

}
