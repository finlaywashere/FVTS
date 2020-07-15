package ca.team2706.fvts.test.core.input;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.input.DummyInputDevice;

public class DummyInputDeviceTest {

	@Test
	public void dummyInputDeviceTest() {
		DummyInputDevice device = new DummyInputDevice();
		Mat frame = device.getFrame("test");
		assertTrue(frame.empty());
	}

}
