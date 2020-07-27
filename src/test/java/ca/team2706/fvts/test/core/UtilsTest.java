package ca.team2706.fvts.test.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class UtilsTest {

	@Test
	public void loadVisionParamsTest() throws Exception{
		File f = File.createTempFile("fvts", "params");
		f.delete();
		f.createNewFile();
		List<Attribute> attribs = new ArrayList<Attribute>();
		attribs.add(new Attribute("core/interface","dummy"));
		attribs.add(new Attribute("core/pipeline","dummy"));
		attribs.add(new Attribute("core/type","0"));
		attribs.add(new Attribute("core/identifier","0"));
		attribs.add(new Attribute("enabled","0"));
		attribs.add(new Attribute("core/csvLog","0"));
		attribs.add(new Attribute("core/imgDumpPath","0"));
		attribs.add(new Attribute("core/imgDumpTime","0"));
		attribs.add(new Attribute("test/test1","test5"));
		attribs.add(new Attribute("name","test7"));
		VisionParams params1 = new VisionParams(attribs,new ArrayList<AttributeOptions>());
		Utils.saveVisionParams(params1, f);
		attribs.remove(attribs.size()-1);
		attribs.remove(attribs.size()-1);
		attribs.add(new Attribute("test2/test1","test7"));
		attribs.add(new Attribute("name","test4"));
		VisionParams params2 = new VisionParams(attribs,new ArrayList<AttributeOptions>());
		Utils.saveVisionParams(params2, f);
		
		List<VisionParams> params = new Utils().loadVisionParams(new FileInputStream(f));
		
		assertEquals("test7",params.get(0).getByName("name").getValue());
		assertEquals("test4",params.get(1).getByName("name").getValue());
		assertEquals("test5",params.get(0).getByName("test/test1").getValue());
		assertEquals("test7",params.get(1).getByName("test2/test1").getValue());
		
		
		f.delete();
	}

}
