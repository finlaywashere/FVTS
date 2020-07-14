package ca.team2706.fvts.test.core.pipelines;

import java.util.ArrayList;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class DummyPipelineTest {

	@Test
	public void dummyPipelineTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		System.load(Constants.OPENCV());
		Mat src = new Mat();
		VisionParams params = new VisionParams(new ArrayList<Attribute>(), new ArrayList<AttributeOptions>());

	}

}
