package ca.team2706.fvts.test.core.image;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.image.ImageResizingPreprocessor;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class ImageResizingPreprocessorTest {

	@Test
	public void imageResizingPreprocessorTest() throws Exception{
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadLibraries();
		ImageResizingPreprocessor processor = new ImageResizingPreprocessor();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("resize/width", "200"));
		attribs.add(new Attribute("resize/height", "150"));
		attribs.add(new Attribute("core/interface", "dummy"));
		attribs.add(new Attribute("core/pipeline", "dummy"));
		attribs.add(new Attribute("name", "test"));
		BufferedImage src = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		Mat srcMat = Utils.bufferedImageToMat(src);
		MainThread main = new MainThread(new VisionParams(attribs, new ArrayList<AttributeOptions>()));
		Mat result = processor.process(srcMat, main);
		assertEquals(200, result.cols());
		assertEquals(150, result.rows());
	}

}
