package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;

public class GroupProcessor extends AbstractMathProcessor {

	public GroupProcessor() {
		super("group");
	}

	@Override
	public void process(VisionData visionData, MainThread main) {
		if(visionData.targetsFound.size() == 0)
			return;
		
		int groupAngle = visionData.params.getByName(getName()+"/"+"groupAngle").getValueI();
		ArrayList<Target> newTargets = new ArrayList<Target>();

		for (Target target : visionData.targetsFound) {

			MatOfPoint2f contour = new MatOfPoint2f(((MatOfPoint) target.data.get("contour")).toArray());

			RotatedRect rect = Imgproc.minAreaRect(contour);

			rect.angle = rect.angle + groupAngle;

			if (rect.angle > 0) {
				continue;
			}

			Target minTarget = null;
			double minDist = Double.MAX_VALUE;

			for (Target target2 : visionData.targetsFound) {

				if (target2 != target) {

					if ((Integer) target2.data.get("xCentre") < (Integer) target.data.get("xCentre")) {
						continue;
					}

					MatOfPoint2f contour2 = new MatOfPoint2f(((MatOfPoint) target2.data.get("contour")).toArray());

					RotatedRect rect2 = Imgproc.minAreaRect(contour2);
					rect2.angle = rect2.angle + groupAngle;

					if (rect2.angle < 0) {
						continue;
					}

					double w = Math.abs((Integer) target.data.get("xCentre") - (Integer) target2.data.get("xCentre"));
					double h = Math.abs((Integer) target.data.get("yCentre") - (Integer) target2.data.get("yCentre"));

					double dist = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));

					if (dist < minDist) {

						minDist = dist;
						minTarget = target2;

					}

				}

			}

			boolean missing = false;

			for (Target target2 : visionData.targetsFound) {

				if (target2 != target) {

					if ((Integer) target2.data.get("xCentre") < (Integer) target.data.get("xCentre")) {
						continue;
					}

					MatOfPoint2f contour2 = new MatOfPoint2f(((MatOfPoint) target.data.get("contour")).toArray());

					RotatedRect rect2 = Imgproc.minAreaRect(contour2);
					rect2.angle = rect2.angle + 40;

					if (rect2.angle > 0) {
						continue;
					}

					double w = Math.abs((Integer) target.data.get("xCentre") - (Integer) target2.data.get("xCentre"));
					double h = Math.abs((Integer) target.data.get("yCentre") - (Integer) target2.data.get("yCentre"));

					double dist = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));

					if (dist < minDist) {

						missing = true;

					}

				}

			}

			if (minTarget == null || missing) {
				continue;
			}

			Target target3 = new Target();
			
			Rect boundingBox = (Rect) target.data.get("boundingBox");
			Rect minBoundingBox = (Rect) minTarget.data.get("boundingBox");

			double x = boundingBox.x < minBoundingBox.x ? boundingBox.x : minBoundingBox.x;
			double y = boundingBox.y < minBoundingBox.y ? boundingBox.y : minBoundingBox.y;

			double width = Math.abs(boundingBox.x - minBoundingBox.x) + minBoundingBox.width;
			double height = Math.abs(boundingBox.y - minBoundingBox.y) + minBoundingBox.height;

			target3.data.put("boundingBox", new Rect((int) x, (int) y, (int) width, (int) height));
			target3.data.put("xCentre", (int) (x + (width / 2)));
			target3.data.put("xCentreNorm", Double.valueOf(((Integer) target3.data.get("xCentre") - (visionData.binMask.width() / 2))
					/ (visionData.binMask.width() / 2)));
			target3.data.put("yCentre", (int) (y + (height / 2)));
			target3.data.put("yCentreNorm", Double.valueOf(((Integer) target3.data.get("yCentre") - (visionData.binMask.height() / 2))
					/ (visionData.binMask.height() / 2)));
			Rect t3Box = (Rect) target3.data.get("boundingBox");
			target3.data.put("areaNorm", (t3Box.height * t3Box.width)
					/ ((double) visionData.binMask.width() * visionData.binMask.height()));

			newTargets.add(target3);

		}

		visionData.targetsFound = newTargets;
	}

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();

		AttributeOptions angle = new AttributeOptions(getName()+"/"+"groupAngle", true);

		ret.add(angle);

		return ret;
	}

}
