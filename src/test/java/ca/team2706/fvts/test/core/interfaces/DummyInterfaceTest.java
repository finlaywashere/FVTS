package ca.team2706.fvts.test.core.interfaces;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.interfaces.DummyInterface;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class DummyInterfaceTest {

	@Test
	public void dummyInterfaceTest() throws Exception {
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadOpenCV();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("core/interface", "dummy"));
		attribs.add(new Attribute("core/pipeline", "dummy"));
		attribs.add(new Attribute("name", "test"));
		DummyInterface dummy = (DummyInterface) AbstractInterface.getByName("dummy");
		MainThread thread = new MainThread(new VisionParams(attribs, new ArrayList<AttributeOptions>()));
		VisionData data = new VisionData();
		data.params = thread.getVisionParams();
		dummy.publishData(data, thread);
		assertEquals(dummy.lastFrame.get("test"), data);
	}

}
