package ca.team2706.fvts.test.core.params;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.team2706.fvts.core.params.Attribute;

public class AttributeTest {

	@Test
	public void attributeTest() {
		Attribute a1 = new Attribute("test","test123",0,0,0,1);
		Attribute a2 = new Attribute("test1","3",1,2,4,1);
		Attribute a3 = new Attribute("test2","7.5",1,-1,100,4);
		Attribute a4 = new Attribute("test3","true",2,0,0,1);
		assertEquals("test", a1.getName());
		assertEquals("test123",a1.getValue());
		assertEquals(0,a1.getType());
		assertEquals(0,a1.getMin());
		assertEquals(0,a1.getMax());
		assertEquals(1,a1.getMultiplier());
		
		assertEquals("test1", a2.getName());
		assertEquals(3,a2.getValueI());
		assertEquals(1,a2.getType());
		assertEquals(2,a2.getMin());
		assertEquals(4,a2.getMax());
		assertEquals(1,a2.getMultiplier());
		
		assertEquals("test2", a3.getName());
		assertEquals(7.5,a3.getValueD(),0.1);
		assertEquals(1,a3.getType());
		assertEquals(-1,a3.getMin());
		assertEquals(100,a3.getMax());
		assertEquals(4,a3.getMultiplier());
		
		assertEquals("test3", a4.getName());
		assertEquals(true,a4.getValueB());
		assertEquals(2,a4.getType());
		assertEquals(0,a4.getMin());
		assertEquals(0,a4.getMax());
		assertEquals(1,a4.getMultiplier());
		
	}

}
