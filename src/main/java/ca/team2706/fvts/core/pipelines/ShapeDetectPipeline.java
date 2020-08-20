package ca.team2706.fvts.core.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class ShapeDetectPipeline extends AbstractPipeline {

	public ShapeDetectPipeline() {
		super("shape");
	}
	
	/** The fps timer **/
	public static long fpsTimer = System.nanoTime();

	@Override
	public VisionData process(Mat src, VisionParams visionParams) {
		VisionData ret = new VisionData();
		ret.params = visionParams;
		
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BayerGR2GRAY);
		
		ret.binMask = gray;
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Calculate the image area
		int imgArea = src.height() * src.width();
		for(MatOfPoint c : contours) {
			Rect boundingRect = Imgproc.boundingRect(c);

			// height * width for area (easier and less CPU cycles than contour.area)
			double areaNorm = ((double) boundingRect.width * boundingRect.height) / imgArea;

			if (areaNorm >= visionParams.getByName(getName()+"/"+"minArea").getValueD()) {

				Target target = new Target();
				target.data.put("boundingBox", boundingRect);
				target.data.put("xCentre", boundingRect.x + (boundingRect.width / 2));
				target.data.put("xCentreNorm", Double.valueOf((((Integer)target.data.get("xCentre")) - (src.width() / 2)) / (src.width() / 2)));
				target.data.put("yCentre", boundingRect.y + (boundingRect.height / 2));
				target.data.put("yCentreNorm", Double.valueOf((((Integer)target.data.get("yCentre")) - (src.height() / 2)) / (src.height() / 2)));
				target.data.put("areaNorm", areaNorm);
			
				MatOfPoint2f contour = new MatOfPoint2f();
				c.convertTo(contour, CvType.CV_32F);
				double arcLen = Imgproc.arcLength(contour, true);
				MatOfPoint2f result = new MatOfPoint2f();
				Imgproc.approxPolyDP(contour,result, 0.04*arcLen,true);
				Point[] points = result.toArray();
				int len = points.length;
				MatOfPoint c2 = new MatOfPoint();
				contour.convertTo(c2, CvType.CV_32F);
				target.data.put("contour", c2);
				target.data.put("edges", len);
				ret.targetsFound.add(target);
			}
			// else
			// skip this contour because it's too small
		}
		long now = System.nanoTime();
		ret.fps = ((double) Constants.NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		ret.fps = ((int) (ret.fps * 10)) / 10.0; // round to 1 decimal place
		fpsTimer = now;
		
		return ret;
	}

	// Create Colour Values
	private static final Scalar BACKGROUND_TARGET_COLOUR = new Scalar(237, 19, 75); // Purple (Non-Preffered Target)
	private static final Scalar PREFERRED_TARGET_COLOUR = new Scalar(30, 180, 30); // Green (Preffered Target)

	public void drawPreferredTarget(Mat src, VisionData visionData) {

		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre
		for (Target target : visionData.targetsFound) {
			MatOfPoint contour = (MatOfPoint) target.data.get("contour");
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			contours.add(contour);
			Imgproc.drawContours(src, contours, 0, BACKGROUND_TARGET_COLOUR);
			int x = (int) target.data.get("xCentre");
			int y = (int) target.data.get("yCentre");
			Imgproc.putText(src, String.valueOf((int)target.data.get("edges")), new Point(x,y), Core.FONT_HERSHEY_SIMPLEX,1, BACKGROUND_TARGET_COLOUR);
		}

		// Draw the preferred target over it
		if (visionData.preferredTarget != null) {
			Target target = visionData.preferredTarget;
			MatOfPoint contour = (MatOfPoint) target.data.get("contour");
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			contours.add(contour);
			Imgproc.drawContours(src, contours, 0, PREFERRED_TARGET_COLOUR);
			int x = (int) target.data.get("xCentre");
			int y = (int) target.data.get("yCentre");
			Imgproc.putText(src, String.valueOf((int)target.data.get("edges")), new Point(x,y), Core.FONT_HERSHEY_SIMPLEX,1, BACKGROUND_TARGET_COLOUR);
		
		}
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> options = new ArrayList<AttributeOptions>();
		
		return options;
	}

	@Override
	public void init(MainThread thread) {
		
	}

}
