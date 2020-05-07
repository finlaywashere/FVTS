package ca.team2706.fvts.test.core.pipelines;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.pipelines.BlobDetectPipeline;

public class BlobDetectPipelineTest {
	@Test
	public void blobDetectTest() {
		BlobDetectPipeline pipeline = new BlobDetectPipeline();
		// No need for a pipeline.init() because the blob detect pipeline doesn't require it
		
		// Create testing image
		BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		Color c = new Color(255,0,0);
		float[] hsv = Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), null);
		int[] hsvI = new int[hsv.length];
		for(int i = 0; i < hsv.length; i++) {
			hsvI[i] = (int) hsv[i];
		}
		// Draw testing colour to image
		Graphics g = image.createGraphics();
		g.setColor(c);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
		
		// Create a fake vision parameters
		List<Attribute> attribs = new ArrayList<Attribute>();
		
	}
}
