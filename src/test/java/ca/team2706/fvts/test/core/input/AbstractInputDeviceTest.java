package ca.team2706.fvts.test.core.input;

import static org.junit.Assert.fail;

import org.junit.Test;

import ca.team2706.fvts.core.input.AbstractInputDevice;
import ca.team2706.fvts.core.input.DummyInputDevice;
import ca.team2706.fvts.core.input.USBCameraInputDevice;

public class AbstractInputDeviceTest {

	@Test
	public void abstractInputDeviceTest() {
		AbstractInputDevice device1 = AbstractInputDevice.getByName("dummy");
		AbstractInputDevice device2 = AbstractInputDevice.getByName("usb");
		if(!(device1 instanceof DummyInputDevice))
			fail("AbstractInputDevice returned a non dummy device");
		if(!(device2 instanceof USBCameraInputDevice))
			fail("AbstractInputDevice returned a non usb device");
		
	}

}
