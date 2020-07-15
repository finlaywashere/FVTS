package ca.team2706.fvts.test.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencv.core.Point;

import ca.team2706.fvts.core.OrderedPoint;

public class OrderedPointTest {
	@Test
	public void testOrderedPoint() {
		Point p = new Point(10,15);
		OrderedPoint point = new OrderedPoint(p, 1);
		assertEquals(p,point.getP());
		assertEquals(1, point.getType(),0.1);
	}
}
