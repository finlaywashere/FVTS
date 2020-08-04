package ca.team2706.fvts.test.core.math;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.math.PrefferedTargetProcessor;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class PrefferedTargetProcessorTest {

	@Test
	public void prefferedTargetProcessorTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadLibraries();
		PrefferedTargetProcessor processor = new PrefferedTargetProcessor();
		VisionData data = new VisionData();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("prefferedtarget/distToCentreImportance","1"));
		attribs.add(new Attribute("core/interface", "dummy"));
		attribs.add(new Attribute("core/pipeline", "dummy"));
		attribs.add(new Attribute("name", "test"));
		MainThread thread = new MainThread(new VisionParams(attribs, new ArrayList<AttributeOptions>()));
		data.params = thread.visionParams;
		Target t1 = new Target();
		t1.data.put("areaNorm", 0.1);
		t1.data.put("xCentreNorm", 0d);
		Target t2 = new Target();
		t2.data.put("areaNorm", 0.5);
		t2.data.put("xCentreNorm", 0.7);
		data.targetsFound.add(t1);
		data.targetsFound.add(t2);
		processor.process(data, thread);
		assertEquals(data.preferredTarget,t1);
		
	}

}
