package ca.team2706.fvts.core.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.NetworkTablesManager;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.main.Main;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
@SuppressWarnings("deprecation")
public class NetworkTablesInterface extends AbstractInterface {

	public NetworkTablesInterface() {
		super("networktables");
	}

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo rio
	 * :]
	 *
	 * @param data The vision data to publish
	 */
	@Override
	public void publishData(VisionData data, MainThread thread) {
		NetworkTable visionTable = NetworkTablesManager.tables.get(data.params.getByName("name").getValue());
		// Sends the data
		// Puts the fps into the vision table
		visionTable.putNumber("fps", data.fps);
		// Puts the number of targets found into the vision table
		visionTable.putNumber("numTargetsFound", data.targetsFound.size());

		if (Main.pubAll) {
			for(Target t : data.targetsFound) {
				// Put all the data into the vision table
				for (String key : t.data.keySet()) {
					Object o = t.data.get(key);
					pubObject(key, o, visionTable);
				}
			}
		}

		// If there is a target
		if (data.preferredTarget != null) {
			// Put all the data into the vision table
			for (String key : data.preferredTarget.data.keySet()) {
				Object o = data.preferredTarget.data.get(key);
				pubObject(key, o, visionTable);
			}
		}
	}
	private void pubObject(String key, Object o, NetworkTable visionTable) {
		if(o instanceof Integer) {
			visionTable.putNumber(key, (Integer) o);
		}else if(o instanceof Double) {
			visionTable.putNumber(key, (Double) o);
		}else if(o instanceof Float) {
			visionTable.putNumber(key, (Float) o);
		}else if(o instanceof String) {
			visionTable.putString(key, (String) o);
		}else if(o instanceof Boolean) {
			visionTable.putBoolean(key, (Boolean) o);
		}
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		ret.add(new AttributeOptions(getName()+"/teamNumber",true));
		
		return ret;
	}

	private boolean setup = false;

	private Lock initLock = new ReentrantLock();

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	public void init(MainThread thread) {
		initLock.lock();

		if (!setup) {
			setup = true;
			// Tells the NetworkTable class that this is a client
			NetworkTable.setClientMode();
			// Sets the interval for updating NetworkTables
			NetworkTable.setUpdateRate(0.02);

			boolean use_GUI = true;

			// If on Linux don't use guis
			if (System.getProperty("os.arch").toLowerCase().indexOf("arm") != -1) {
				use_GUI = false;
			}

			if (!use_GUI && Main.serverIp.equals("")) {

				// Sets the team number
				NetworkTable.setTeam(thread.getVisionParams().getByName(getName()+"/teamNumber").getValueI());

			} else {

				if (Main.serverIp.equals("")) {
					Main.serverIp = "localhost";
				}
				String ip = Main.serverIp;

				// Sets the IP address to connect to
				NetworkTable.setIPAddress(ip);

			}
			// Enables DSClient
			NetworkTable.setDSClientEnabled(true);

			// Initilizes NetworkTables
			NetworkTable.initialize();
		}
		String name = thread.getVisionParams().getByName("name").getValue();
		NetworkTable visionTable = NetworkTable.getTable("vision-" + name + "/");
		NetworkTablesManager.tables.put(name, visionTable);
		Utils.sendVisionParams(thread.getVisionParams());

		// Sets the vision table to the "vision" table that is in NetworkTables
		Main.loggingTable = NetworkTable.getTable("logging-level");
		initLock.unlock();
	}

}
