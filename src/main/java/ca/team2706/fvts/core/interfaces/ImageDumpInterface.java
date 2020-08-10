package ca.team2706.fvts.core.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.team2706.fvts.core.Bundle;
import ca.team2706.fvts.core.ImageDumpScheduler;
import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.main.Main;

public class ImageDumpInterface extends AbstractInterface {

	public ImageDumpInterface() {
		super("imgdump");
	}

	@Override
	public void publishData(VisionData data, MainThread thread) {
		long lastTimeL = lastTime.get(thread.getParamName());
		long elapsedTime = System.currentTimeMillis()-lastTimeL;
		if(thread.useCamera && elapsedTime > thread.getVisionParams().getByName(getName()+"/imgDumpTime").getValueD()) {
			lastTime.put(thread.getParamName(), System.currentTimeMillis());
			try {
				Bundle b = new Bundle(Utils.matToBufferedImage(data.outputImg),
						Utils.matToBufferedImage(data.binMask),
						timestamp.get(thread.getParamName()), thread.visionParams,
						thread.getParamName(),dumpDirs.get(thread.getParamName()));
				ImageDumpScheduler.schedule(b);
				timestamp.put(thread.getParamName(), timestamp.get(thread.getParamName()+1));
			} catch (IOException e) {
				Log.e(e.getMessage(), true);
				return;
			}
		}
	}

	private Map<String,Long> lastTime = new HashMap<String,Long>();
	private Map<String,Integer> timestamp = new HashMap<String,Integer>();
	private Map<String,File> dumpDirs = new HashMap<String,File>();
	@Override
	public void init(MainThread thread) {
		lastTime.put(thread.getParamName(), 0L);
		timestamp.put(thread.getParamName(), 0);
		dumpDirs.put(thread.getParamName(), new File(thread.getVisionParams().getByName(getName()+"/imgDumpPath").getValue().replaceAll("\\$1", "" + Main.runID)));
	}
	
	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		ret.add(new AttributeOptions(getName()+"/imgDumpPath",true));
		ret.add(new AttributeOptions(getName()+"/imgDumpTime",true));
		
		return ret;
	}

}
