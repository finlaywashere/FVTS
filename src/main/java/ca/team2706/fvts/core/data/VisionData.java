package ca.team2706.fvts.core.data;

import java.util.ArrayList;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.params.VisionParams;

/**
 * A class to hold any visionTable data returned by process() :) :) :} :] :]
 */

public class VisionData {

	/** The List of all the targets in the image **/

	public ArrayList<Target> targetsFound = new ArrayList<Target>();
	/**
	 * The target that is the most appealing, how it is chosen depends on the
	 * distToCenterImportance value in the vision parameters
	 **/
	public Target preferredTarget;
	/** The image that contains the targets **/
	public Mat outputImg = new Mat();
	/** The frames per second **/
	public Mat binMask = new Mat();
	public double fps;
	public VisionParams params;
}