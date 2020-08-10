package ca.team2706.fvts.core.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.main.Main;

public class CSVLogInterface extends AbstractInterface {

	public CSVLogInterface() {
		super("csv");
	}

	@Override
	public void publishData(VisionData visionData, MainThread thread) {
		if (((double)System.currentTimeMillis() - elapsedTime.get(thread.getParamName()))/1000 > thread.getVisionParams()
				.getByName(getName() + "/csvLogTime").getValueD()) {
			List<String> data = new ArrayList<String>();
			File logFile = csvFiles.get(thread.getParamName());
			if (!logFile.exists()) {
				data.add("Elapsed Time");
				data.add("FPS");
				data.add("Number of Targets");
				data.add("Preffered Target X");
				data.add("Preffered Target Y");
				data.add("Preffered Target Area");
				data.add("Preffered Target Distance");
				try {
					Log.logData(logFile, data);
				} catch (Exception e) {
					Log.e("Error while logging vision data to csv file!", true);
					Log.e(e.getMessage(), true);
				}
				data.clear();
			}
			data.add("" + (System.currentTimeMillis() - lastTime.get(thread.getParamName())));
			data.add("" + visionData.fps);
			data.add("" + visionData.targetsFound.size());
			if (visionData.preferredTarget != null) {
				data.add((Double) visionData.preferredTarget.data.get("xCentreNorm") + "");
				data.add((Double) visionData.preferredTarget.data.get("yCentreNorm") + "");
				data.add((Double) visionData.preferredTarget.data.get("areaNorm") + "");
				data.add((Double) visionData.preferredTarget.data.get("distance") + "");
			}
			try {
				Log.logData(logFile, data);
			} catch (Exception e) {
				Log.e("Error while logging vision data to csv file!", true);
				Log.e(e.getMessage(), true);
			}
			elapsedTime.put(thread.getParamName(), System.currentTimeMillis());
		}
		lastTime.put(thread.getParamName(), System.currentTimeMillis());
	}

	private Map<String, Long> lastTime = new HashMap<String, Long>();
	private Map<String, Long> elapsedTime = new HashMap<String, Long>();
	private Map<String, Integer> timestamp = new HashMap<String, Integer>();
	private Map<String, File> csvFiles = new HashMap<String, File>();

	@Override
	public void init(MainThread thread) {
		lastTime.put(thread.getParamName(), 0L);
		timestamp.put(thread.getParamName(), 0);
		csvFiles.put(thread.getParamName(), new File(thread.getVisionParams().getByName(getName() + "/csvLogPath")
				.getValue().replaceAll("\\$1", "" + Main.runID)));
		elapsedTime.put(thread.getParamName(), 0L);
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		ret.add(new AttributeOptions(getName() + "/csvLogPath", true));
		ret.add(new AttributeOptions(getName() + "/csvLogTime", true));

		return ret;
	}

}
