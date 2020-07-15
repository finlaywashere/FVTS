package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class AngleOffsetProcessor extends AbstractMathProcessor {

	public AngleOffsetProcessor() {
		super("angleoffset");
	}

	@Override
	public void process(VisionData visionData, MainThread main) {
		if(visionData.targetsFound.size() == 0)
			return;
		VisionParams visionParams = visionData.params;
		/*
		 * 
		 * Calculates a magical distance offset based on the angle of an object using quadratics and fine tuning
		 * 
		 * 
		 */

		for (Target t : visionData.targetsFound) {
			// Do the offset math which is using quadratics and please let this work, ive been trying this for 3 hours and its 00:00, i am very tired but this code keeps me up at night
			double aoA = visionParams.getByName(getName()+"/"+"aoA").getValueD();
			double aoB = visionParams.getByName(getName()+"/"+"aoB").getValueD();
			double aoC = visionParams.getByName(getName()+"/"+"aoC").getValueD();
			double magic = Math.abs((Double) t.data.get("xCentreNorm")) / ((Double) t.data.get("areaNorm") / (visionData.binMask.rows() * visionData.binMask.cols()));
			double xo = Math.pow(magic,2) * aoA + magic * aoB + aoC;
			t.data.put("distance", xo);
		}
	}

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions aoA = new AttributeOptions(getName()+"/"+"aoA", true);
		AttributeOptions aoB = new AttributeOptions(getName()+"/"+"aoB", true);
		AttributeOptions aoC = new AttributeOptions(getName()+"/"+"aoC", true);
		
		ret.add(aoA);
		ret.add(aoB);
		ret.add(aoC);
		
		return ret;
	}

}
