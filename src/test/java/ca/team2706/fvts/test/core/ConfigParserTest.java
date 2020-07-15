package ca.team2706.fvts.test.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ca.team2706.fvts.core.ConfigParser;

public class ConfigParserTest {
	
	@Test
	public void getPropertiesTest() {
		File tmpConfig = null;
		try {
			tmpConfig = File.createTempFile("test", "config");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		try {
			tmpConfig.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(tmpConfig));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
		out.println("test:");
		out.println("  test2:");
		out.println("    test3=hello");
		out.println();
		out.close();
		
		Map<String,Map<String,String>> props = null;
		try {
			props = ConfigParser.getProperties(tmpConfig, "test");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals("hello", props.get("test2").get("test3"));
	}
	@Test
	public void saveListTest() throws Exception{
		Map<String,Map<String,String>> properties = new HashMap<String,Map<String,String>>();
		Map<String,String> test = new HashMap<String,String>();
		test.put("test1", "test2");
		properties.put("name", test);
		File f = File.createTempFile("test", "config");
		f.delete();
		f.createNewFile();
		ConfigParser.saveList(f, "test5", properties);
		Map<String,Map<String,String>> read = ConfigParser.getProperties(f, "test5");
		assertEquals(properties, read);
	}
	@Test
	public void getPropertiesMTest() throws Exception{
		File f = File.createTempFile("test", "config");
		f.delete();
		f.createNewFile();
		PrintWriter out = new PrintWriter(new FileWriter(f,true));
		out.println("test:");
		out.println("  test2=true");
		out.close();
	}
	
}
