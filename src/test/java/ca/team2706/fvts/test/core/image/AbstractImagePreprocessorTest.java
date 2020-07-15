package ca.team2706.fvts.test.core.image;

import static org.junit.Assert.fail;

import org.junit.Test;

import ca.team2706.fvts.core.image.AbstractImagePreprocessor;
import ca.team2706.fvts.core.image.ImageCropPreprocessor;
import ca.team2706.fvts.core.image.ImageResizingPreprocessor;

public class AbstractImagePreprocessorTest {

	@Test
	public void abstractImagePreprocessorTest() {
		AbstractImagePreprocessor processor1 = AbstractImagePreprocessor.getByName("crop");
		AbstractImagePreprocessor processor2 = AbstractImagePreprocessor.getByName("resize");
		if(!(processor1 instanceof ImageCropPreprocessor))
			fail("AbstractImagePreprocessor did not return a crop preprocessor!");
		if(!(processor2 instanceof ImageResizingPreprocessor))
			fail("AbstractImagePreprocessor did not return a resize preprocessor!");
		
	}

}
