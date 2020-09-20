package ca.team2706.fvts.core.interfaces;

import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;

public class NetworkPushInterface extends AbstractInterface {

	public NetworkPushInterface() {
		super("push");
	}

	@Override
	public void publishData(VisionData data, MainThread thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(MainThread thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AttributeOptions> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
