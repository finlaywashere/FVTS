package ca.team2706.fvts.core;

/**
 * A class to hold a x and y position
 */
public class NormalisedPoint {
	
	/**
	 * The X
	 */
	private int x;
	/**
	 * The Y
	 */
	private int y;
	
	/**
	 * Creates a normalised Point
	 * @param x The X value of the point
	 * @param y The Y value of the point
	 */
	public NormalisedPoint(int x, int y) {
		//Sets the x to the x
		this.x = x;
		//Sets the y to the y
		this.y = y;
	}
	/**
	 * Gets the X 
	 * @return The X value of this point
	 */
	public int getX() {
		return x;
	}
	/**
	 * Gets the Y
	 * @return The Y value of this point
	 */
	public int getY() {
		return y;
	}
	
}
