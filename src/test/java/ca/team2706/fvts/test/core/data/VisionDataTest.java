package ca.team2706.fvts.test.core.data;

import org.junit.Test;

import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.data.VisionData;

public class VisionDataTest {

	@Test
	public void visionDataTest() {
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadOpenCV();
		new VisionData();
	}

}
