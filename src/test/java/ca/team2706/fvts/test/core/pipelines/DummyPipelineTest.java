package ca.team2706.fvts.test.core.pipelines;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.opencv.core.Mat;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.core.pipelines.DummyPipeline;

public class DummyPipelineTest {

	@Test
	public void dummyPipelineTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		System.load(Constants.OPENCV());
		Mat src = new Mat();
		VisionParams params = new VisionParams(new ArrayList<Attribute>(), new ArrayList<AttributeOptions>());
		DummyPipeline pipeline = new DummyPipeline();
		VisionData data = pipeline.process(src, params);
		assertEquals(src,data.binMask);
		assertEquals(src,data.outputImg);
		assertEquals(params,data.params);
	}

}
