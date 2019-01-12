package ca.team2706.vision.trackerboxreloaded;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {

	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;

	/** The fps timer **/
	public static long fpsTimer = System.nanoTime();

	/**
	 * The visionTable pipeline!
	 *
	 * @param src          Raw source image to process
	 * @param visionParams Parameters for visionTable
	 * @return All the data!
	 */
	@SuppressWarnings("unused")
	public static VisionData process(Mat src, VisionParams visionParams, boolean use_GUI) {

		// As a memory footprint optimization, when running on a Pi, re-use one working
		// image in memory
		Mat dilated, erodeOne, erodeTwo, workingImg;
		// If using the guis
		if (use_GUI) {
			// Make new Mats
			dilated = new Mat();
			erodeOne = new Mat();
			erodeTwo = new Mat();
		} else {
			// Else re use them
			dilated = new Mat();
			erodeOne = dilated;
			erodeTwo = dilated;
		}
		// Calculate the image area
		int imgArea = src.height() * src.width();

		// If there's any data or intermediate images that you want to return, add them
		// to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();

		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Erode - Dilate*2 - Erode
		Imgproc.erode(hsvThreshold, erodeOne, new Mat(), new Point(), visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erodeOne, dilated, new Mat(), new Point(), 2 * visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilated, erodeTwo, new Mat(), new Point(), visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));

		visionData.binMask = erodeTwo.clone();

		// Find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(dilated, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Make Bounding Box
		for (MatOfPoint contour : contours) {

			Rect boundingRect = Imgproc.boundingRect(contour);

			List<Point> points = contour.toList();

			// height * width for area (easier and less CPU cycles than contour.area)
			double areaNorm = ((double) boundingRect.width * boundingRect.height) / imgArea;

			if (areaNorm >= visionParams.minArea) {

				double a = Double.POSITIVE_INFINITY, b = Double.POSITIVE_INFINITY, c = Double.POSITIVE_INFINITY,
						d = Double.POSITIVE_INFINITY;

				double x1, x2, x3, x4, y1, y2, y3, y4;

				x1 = boundingRect.x;
				y1 = boundingRect.y;
				x2 = boundingRect.width+boundingRect.x;
				y2 = boundingRect.y;
				x3 = boundingRect.width+boundingRect.x;
				y3 = boundingRect.height+boundingRect.y;
				x4 = boundingRect.x;
				y4 = boundingRect.height+boundingRect.y;

				for (Point point : points) {

					double a1 = Math.sqrt(Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2));
					double b1 = Math.sqrt(Math.pow(point.x - x2, 2) + Math.pow(point.y - y2, 2));
					double c1 = Math.sqrt(Math.pow(point.x - x3, 2) + Math.pow(point.y - y3, 2));
					double d1 = Math.sqrt(Math.pow(point.x - x4, 2) + Math.pow(point.y - y4, 2));
					
					if (a1 < a) {
						a = a1;
						continue;
					}
					if (b1 < b) {
						b = b1;
						continue;
					}
					if (c1 < c) {
						c = c1;
						continue;
					}
					if (d1 < d) {
						d = d1;
						continue;
					}

				}
				List<Point> orderedPoints = new ArrayList<Point>();
				for (Point point : points) {

					double a1 = Math.sqrt(Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2));
					double b1 = Math.sqrt(Math.pow(point.x - x2, 2) + Math.pow(point.y - y2, 2));
					double c1 = Math.sqrt(Math.pow(point.x - x3, 2) + Math.pow(point.y - y3, 2));
					double d1 = Math.sqrt(Math.pow(point.x - x4, 2) + Math.pow(point.y - y4, 2));

					if (a1 == a) {
						orderedPoints.add(point);
					}
					if (b1 == b) {
						orderedPoints.add(point);
					}
					if (c1 == c) {
						orderedPoints.add(point);
					}
					if (d1 == d) {
						orderedPoints.add(point);
					}
				}

				double angle = 90;

				try {

					Point A = orderedPoints.get(0);
					Point D = orderedPoints.get(orderedPoints.size() - 1);
					
					double height = D.y - A.y;
					double width = Math.abs(D.x - A.x);
					
					if(width == 0) {
						width = 1;
					}
					if(height == 0) {
						height = 1;
					}

					angle = Math.toDegrees(Math.atan(height / width));
					
					if(D.x < A.x) {
						//Angle is negative
						angle = -angle;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				VisionData.Target target = new VisionData.Target();
				target.boundingBox = boundingRect;
				target.xCentre = target.boundingBox.x + (target.boundingBox.width / 2);
				target.xCentreNorm = ((double) target.xCentre - (src.width() / 2)) / (src.width() / 2);
				target.yCentre = target.boundingBox.y + (target.boundingBox.height / 2);
				target.yCentreNorm = ((double) target.yCentre - (src.height() / 2)) / (src.height() / 2);
				target.areaNorm = (target.boundingBox.height * target.boundingBox.width) / ((double) imgArea);
				target.angle = angle;
				visionData.targetsFound.add(target);
			}
			// else
			// skip this contour because it's too small
		}

		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		visionData.fps = ((int) (visionData.fps * 10)) / 10.0; // round to 1 decimal place
		fpsTimer = now;

		return visionData;
	}

	/**
	 * From all the targets found in visionData.targetsFound, select the one that
	 * we're going to send to the roboRIO.
	 *
	 * @param visionData
	 */
	public static void selectPreferredTarget(VisionData visionData, VisionParams visionParams) {

		if (visionData.targetsFound.size() == 0) {
			return;
		}

		// loop over the targets to find the largest area of any target found.
		// this is so we can give the largest a score of 1.0, and each other target a
		// score that is a
		// percentage of the area of the largest.
		double largestAreaNorm = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {
			if (target.areaNorm > largestAreaNorm)
				largestAreaNorm = target.areaNorm;
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {

			// Give each target a score, and select the one with the highest score.

			double areaScore = target.areaNorm / largestAreaNorm;
			double distFromCentrePenalty = Math.abs(target.xCentreNorm);

			double score = (1 - visionParams.distToCentreImportance) * areaScore
					- visionParams.distToCentreImportance * distFromCentrePenalty;

			if (bestScore < score) {
				visionData.preferredTarget = target;
				bestScore = score;
			}
		}
	}

	// Create Colour Values
	private static final Scalar BACKGROUND_TARGET_COLOUR = new Scalar(237, 19, 75); // Purple (Non-Preffered Target)
	private static final Scalar PREFERRED_TARGET_COLOUR = new Scalar(30, 180, 30); // Green (Preffered Target)

	public static void drawPreferredTarget(Mat src, VisionData visionData) {

		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre

		for (VisionData.Target target : visionData.targetsFound) {
			Point centerTarget = new Point(target.xCentre, target.yCentre);
			Imgproc.circle(src, centerTarget, 6, BACKGROUND_TARGET_COLOUR, -1);
			Imgproc.rectangle(src, new Point(target.boundingBox.x, target.boundingBox.y),
					new Point(target.boundingBox.x + target.boundingBox.width,
							target.boundingBox.y + target.boundingBox.height),
					BACKGROUND_TARGET_COLOUR, 3);
		}

		// Draw the preferred target over it
		if (visionData.preferredTarget != null) {

			Point centerTarget = new Point(visionData.preferredTarget.xCentre, visionData.preferredTarget.yCentre);
			Imgproc.circle(src, centerTarget, 10, PREFERRED_TARGET_COLOUR, -1);
			Imgproc.rectangle(src,
					new Point(visionData.preferredTarget.boundingBox.x, visionData.preferredTarget.boundingBox.y),
					new Point(visionData.preferredTarget.boundingBox.x + visionData.preferredTarget.boundingBox.width,
							visionData.preferredTarget.boundingBox.y + visionData.preferredTarget.boundingBox.height),
					PREFERRED_TARGET_COLOUR, 7);
		}
	}

}
