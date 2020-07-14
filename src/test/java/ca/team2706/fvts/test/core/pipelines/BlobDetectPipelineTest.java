package ca.team2706.fvts.test.core.pipelines;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.core.pipelines.BlobDetectPipeline;

public class BlobDetectPipelineTest {
	@Test
	public void blobDetectTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		System.load(Constants.OPENCV());
		BlobDetectPipeline pipeline = new BlobDetectPipeline();
		// No need for a pipeline.init() because the blob detect pipeline doesn't require it
		
		// Create testing image
		BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		Color c = new Color(255,0,0);
		// Draw testing colour to image
		Graphics g = image.createGraphics();
		g.setColor(c);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
		BufferedImage image2 = new BufferedImage(640,480,BufferedImage.TYPE_3BYTE_BGR);
		g = image2.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image2.getWidth(), image2.getHeight());
		g.dispose();
		// Create a fake vision parameters
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("blobdetect/minHue", "0"));
		attribs.add(new Attribute("blobdetect/maxHue", "20"));
		attribs.add(new Attribute("blobdetect/minSaturation", "0"));
		attribs.add(new Attribute("blobdetect/maxSaturation", "255"));
		attribs.add(new Attribute("blobdetect/minValue", "200"));
		attribs.add(new Attribute("blobdetect/maxValue", "255"));
		
		attribs.add(new Attribute("blobdetect/minArea","0"));
		attribs.add(new Attribute("blobdetect/erodeDilateIterations","0"));
		
		VisionParams params = new VisionParams(attribs, new ArrayList<AttributeOptions>());
		Mat img1 = Utils.bufferedImageToMat(image);
		Mat img2 = Utils.bufferedImageToMat(image2);
		VisionData data1 = pipeline.process(img1, params);
		assertEquals(1,data1.targetsFound.size());
		img1.release();
		VisionData data2 = pipeline.process(img2, params);
		assertEquals(0,data2.targetsFound.size());
		img2.release();
	}
}
