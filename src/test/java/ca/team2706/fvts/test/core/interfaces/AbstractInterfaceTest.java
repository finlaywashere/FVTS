package ca.team2706.fvts.test.core.interfaces;

import static org.junit.Assert.fail;

import org.junit.Test;

import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.interfaces.DummyInterface;
import ca.team2706.fvts.core.interfaces.NetworkTablesInterface;

public class AbstractInterfaceTest {

	@Test
	public void abstractInterfaceTest() {
		AbstractInterface interface1 = AbstractInterface.getByName("dummy");
		AbstractInterface interface2 = AbstractInterface.getByName("networktables");
		if(!(interface1 instanceof DummyInterface))
			fail("AbstractInterface returned a non dummy interface");
		if(!(interface2 instanceof NetworkTablesInterface))
			fail("AbstractInterface returned a non networktables interface");
		
	}

}
