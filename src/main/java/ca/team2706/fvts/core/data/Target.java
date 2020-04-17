package ca.team2706.fvts.core.data;

import java.util.HashMap;
import java.util.Map;

public class Target {
	public Map<String,Object> data = new HashMap<String,Object>();
	/* The data in this object that is actually used by the core FVTS stuff:
	 * Double distance // The distance to the target (in whatever unit it was callibrated in)
	 * MatOfPoint contour // The actual object
	 * Integer xCentre // The x center of the target in the image
	 * Double xCentreNorm // The normalized (-1 to 1) x center of the target in the image
	 * Integer yCentre // The y center of the target in the image
	 * Double yCentreNorm // The normalized (-1 to 1) y center of the target in the image
	 * Double areaNorm  // The normalized (0 to 1) area of the target in image
	 * Rect boundingBox // The bounding box around the object
	 * 
	 */
}
