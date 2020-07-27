package ca.team2706.fvts.test.core;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class MainThreadTest {

	@Test
	public void mainThreadTest() throws Exception{
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadOpenCV();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("name","test"));
		attribs.add(new Attribute("core/interface","dummy"));
		attribs.add(new Attribute("core/pipeline","dummy"));
		attribs.add(new Attribute("core/type","dummy"));
		attribs.add(new Attribute("core/identifier","0"));
		File csvFile = File.createTempFile("fvts-test", "csv");
		csvFile.createNewFile();
		attribs.add(new Attribute("core/csvLog",csvFile.getAbsolutePath()));
		attribs.add(new Attribute("enabled","true"));
		attribs.add(new Attribute("core/imgDumpTime","-1"));
		attribs.add(new Attribute("core/maths","distance"));
		attribs.add(new Attribute("distance/distSlope","1"));
		attribs.add(new Attribute("distance/distYIntercept","0"));
		
		MainThread thread = new MainThread(new VisionParams(attribs, new ArrayList<AttributeOptions>()));
		Log.silence();
		thread.start();
		Thread.sleep(2000);
		thread.stop = true;
		thread.join(500);
		Log.unsilence();
		if(thread.error)
			fail();
	}

}
