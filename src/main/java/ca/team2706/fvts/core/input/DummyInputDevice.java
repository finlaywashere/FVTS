package ca.team2706.fvts.core.input;

import org.opencv.core.Mat;

public class DummyInputDevice extends AbstractInputDevice {

	public DummyInputDevice() {
		super("dummy",true);
	}

	@Override
	public void init(String identifier) throws Exception {
		
	}

	@Override
	public Mat getFrame(String identifier) {
		Mat mat = new Mat();
		return mat;
	}

}
