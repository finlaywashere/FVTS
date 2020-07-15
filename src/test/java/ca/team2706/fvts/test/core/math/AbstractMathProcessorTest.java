package ca.team2706.fvts.test.core.math;

import static org.junit.Assert.fail;

import org.junit.Test;

import ca.team2706.fvts.core.math.AbstractMathProcessor;
import ca.team2706.fvts.core.math.AngleOffsetProcessor;
import ca.team2706.fvts.core.math.GroupProcessor;

public class AbstractMathProcessorTest {

	@Test
	public void abstractMathProcessorTest() {
		AbstractMathProcessor processor1 = AbstractMathProcessor.getByName("group");
		AbstractMathProcessor processor2 = AbstractMathProcessor.getByName("angleoffset");
		if(!(processor1 instanceof GroupProcessor))
			fail("AbstractMathProcessor returned a non group processor!");
		if(!(processor2 instanceof AngleOffsetProcessor))
			fail("AbstractMathProcessor returned a non angle offset processor!");
		
	}

}
