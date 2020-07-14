package ca.team2706.fvts.test.core.params;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParamsOptions;

public class VisionParamsOptionsTest {

	@Test
	public void visionParamsOptionsTest() {
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("test","3"));
		attribs.add(new Attribute("test1","2"));
		List<AttributeOptions> options = new ArrayList<AttributeOptions>();
		options.add(new AttributeOptions("test", true));
		options.add(new AttributeOptions("test1", true));
		options.add(new AttributeOptions("test2", false));
		VisionParamsOptions vpOptions = new VisionParamsOptions(options);
		vpOptions.setAttribs(attribs);
		assertTrue(vpOptions.isValid());
		options.add(new AttributeOptions("test3", true));
		vpOptions = new VisionParamsOptions(options);
		vpOptions.setAttribs(attribs);
		Log.silence();
		assertTrue(!vpOptions.isValid());
		Log.unsilence();
	}

}
