package ca.team2706.fvts.test.core.params;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.team2706.fvts.core.params.AttributeOptions;

public class AttributeOptionsTest {

	@Test
	public void attributeOptionsTest() {
		AttributeOptions o = new AttributeOptions("test", true, 5, -1, 17, 5);
		assertEquals("test",o.getName());
		assertEquals(true,o.isRequired());
		assertEquals(5,o.getType());
		assertEquals(-1,o.getMin());
		assertEquals(17,o.getMax());
		assertEquals(5,o.getMultiplier());
	}

}
