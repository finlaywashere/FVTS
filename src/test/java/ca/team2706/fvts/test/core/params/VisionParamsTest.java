package ca.team2706.fvts.test.core.params;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class VisionParamsTest {

	@Test
	public void visionParamsTest() {
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("test","3"));
		attribs.add(new Attribute("test1","2"));
		List<AttributeOptions> options = new ArrayList<AttributeOptions>();
		options.add(new AttributeOptions("test", true));
		options.add(new AttributeOptions("test1", true));
		options.add(new AttributeOptions("test2", false));
		try {
			VisionParams vp = new VisionParams(attribs, options);
			Attribute a = vp.getByName("test");
			assertEquals("test",a.getName());
			assertEquals(3,a.getValueI());
		}catch(Exception e) {
			e.printStackTrace();
			fail();
		}
		options.add(new AttributeOptions("test3", true));
		Log.silence();
		try {
			new VisionParams(attribs, options);
			fail();
		}catch(Exception e) {
		}
		Log.unsilence();
	}

}
