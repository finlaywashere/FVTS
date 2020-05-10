package ca.team2706.fvts.test.core;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.main.Main;

public class MainThreadTest {
	
	@Test
	public void mainThreadTest() throws Exception {
		// Set up testing environment
		
		// Load OpenCV
		System.loadLibrary(Constants.OPENCV_LIBRARY);
		
		// Disable all GUI stuff
		Main.developmentMode = false;
		// Configure run identifier
		Main.runID = 0;
		// Locate temp folder
		File tmpFolder = File.createTempFile("test", "tmp").getParentFile();
		// Create a mock vision parameters by setting all handling to dummy classes and by setting the bare minimum parameters
		// Make all the parameters
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("core/interface", "dummy"));
		attribs.add(new Attribute("core/pipeline", "dummy"));
		attribs.add(new Attribute("core/type", "dummy"));
		attribs.add(new Attribute("core/identifier", ""));
		// Pointing the csv log to a folder that should NEVER exist
		attribs.add(new Attribute("core/csvLog", tmpFolder.getAbsolutePath()+"/asdhasdukahsduilashdlkjashduaisdhgasjdgaysdgajksd/$1.csv"));
		attribs.add(new Attribute("enabled","true"));
		attribs.add(new Attribute("name", "test"));
		
		// Create the vision parameters object
		VisionParams params = new VisionParams(attribs, new ArrayList<AttributeOptions>());
		
		// Create a MainThread object to run the test on
		MainThread thread = new MainThread(params);
		
		// Silence log so that the annoying pipeline time info will stop
		Log.silence();
		
		thread.start();
		
		// Run for 10s and then check for errors
		Thread.sleep(10*1000);
		
		boolean error = thread.error;
		
		thread.stop = true;
		
		thread.join();
		
		// Un silence the log
		Log.unsilence();
		
		assertFalse(error);
	}
}
