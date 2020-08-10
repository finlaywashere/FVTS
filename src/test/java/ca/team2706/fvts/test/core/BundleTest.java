package ca.team2706.fvts.test.core;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import ca.team2706.fvts.core.Bundle;

public class BundleTest {

	@Test
	public void getRaw() {
		try {
			BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(image, null, 0,null,null,null);
			if (!(bundle.getRaw() == image)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getBinMask() {
		try {
			BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(null, image, 0,null,null,null);
			if (!(bundle.getBinMask() == image)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getTimeStamp() {
		try {
			int i = 99;
			Bundle bundle = new Bundle(null, null, i,null,null,null);
			if (!(bundle.getTimeStamp() == i)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void Bundle() {
		try {
			@SuppressWarnings("unused")
			Bundle bundle = new Bundle(null, null, 0,null,null,null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
