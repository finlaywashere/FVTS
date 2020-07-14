package ca.team2706.fvts.test.core.pipelines;

import static org.junit.Assert.fail;

import org.junit.Test;

import ca.team2706.fvts.core.pipelines.AbstractPipeline;
import ca.team2706.fvts.core.pipelines.BlobDetectPipeline;
import ca.team2706.fvts.core.pipelines.DummyPipeline;

public class AbstractPipelineTest {

	@Test
	public void abstractPipelineTest() {
		AbstractPipeline blob = AbstractPipeline.getByName("blobdetect");
		AbstractPipeline dummy = AbstractPipeline.getByName("dummy");
		if(!(blob instanceof BlobDetectPipeline))
			fail("Abstract pipeline did not return blob detect pipeline!");
		if(!(dummy instanceof DummyPipeline))
			fail("Abstract pipeline did not return dummy pipeline!");
		
	}

}
