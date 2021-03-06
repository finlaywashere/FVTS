package ca.team2706.fvts.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ca.team2706.fvts.core.ConfigParser;
import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.ImageDumpScheduler;
import ca.team2706.fvts.core.LibraryLoader;
import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.NetworkTablesManager;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.VisionCameraServer;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.VisionParams;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

@SuppressWarnings("deprecation")
public class Main {

	public static String MASTER_CONFIG_FILE = "master.cf";

	public static String filename = "";
	public static int timestamp = 0;
	public static File timestampfile;
	public static NetworkTable loggingTable;
	public static String visionParamsFile = "visionParams.properties";
	public static boolean developmentMode = false;
	public static boolean pubAll = false;
	public static int runID;
	public static String serverIp = "";
	public static CommandLine cmd = null;

	public static List<MainThread> threads = new ArrayList<MainThread>();

	public static void reloadConfig() {
		visionParamsList.clear();
		new Utils().loadVisionParams();
		for (MainThread thread : threads) {
			String name = thread.visionParams.getByName("name").getValue();
			boolean found = false;
			for (VisionParams params : visionParamsList) {
				if (!found) {
					if (params.getByName("name").getValue().equals(name)) {
						thread.updateParams(params);
						found = true;
					}
				}

			}

		}
	}

	// Camera Type (set in visionParams.properties)
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/
	/** The vision parameters, this is used by the vision pipeline **/
	public static List<VisionParams> visionParamsList = new ArrayList<VisionParams>();

	public static boolean b = true;
	public Main(String[] args) throws Exception{
		System.out.println("FVTS Main " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);

		Options options = new Options();

		Option ip = new Option("ip", true, "The IP address of the NetworkTables server");
		options.addOption(ip);
		Option developmentMode = new Option("dev", "development", false, "Puts FVTS in development mode");
		options.addOption(developmentMode);
		Option configFile = new Option("conf", "config", true, "Specifies an alternative config file");
		options.addOption(configFile);
		Option masterFile = new Option("mast", "master", true, "Specifies an alternative master config file");
		options.addOption(masterFile);
		Option openCVOverride = new Option("cv","opencv", true, "Specifies an alternative OpenCV binary file");
		options.addOption(openCVOverride);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			Log.e(e.getMessage(), true);
			formatter.printHelp("FVTS", options);
			System.exit(1);
		}
		Main.developmentMode = cmd.hasOption("development");
		
		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadLibraries(cmd.getOptionValue("opencv", null));
		
		if(cmd.hasOption("master")) {
			MASTER_CONFIG_FILE = cmd.getOptionValue("master",null);
		}

		// Connect NetworkTables, and get access to the publishing table
		serverIp = cmd.getOptionValue("ip", "");

		visionParamsFile = cmd.getOptionValue("config", "visionParams.properties");

		// read the vision calibration values from file.
		visionParamsList = new Utils().loadVisionParams();

		Map<String, String> masterConfig;
		Map<String, String> masterEnabled;
		if(MASTER_CONFIG_FILE.equalsIgnoreCase("fallback")) {
			List<String> lines = ConfigParser.readLines(getClass().getResourceAsStream("master.cf"));
			masterConfig = ConfigParser.getPropertiesM(lines, "config");
			masterEnabled = ConfigParser.getPropertiesM(lines, "enabled");
		}else {
			List<String> lines = ConfigParser.readLines(new FileInputStream(MASTER_CONFIG_FILE));
			masterConfig = ConfigParser.getPropertiesM(lines, "config");
			masterEnabled = ConfigParser.getPropertiesM(lines, "enabled");
		}
		if(masterConfig.containsKey("pubAll")) {
			pubAll = Boolean.valueOf(masterConfig.get("pubAll"));
		}

		

		// Go through and enable the configs
		for (String s : masterEnabled.keySet()) {
			for (VisionParams params : visionParamsList) {
				if (params.getByName("name").getValue().equals(s)) {
					params.putAttrib(new Attribute("enabled", masterEnabled.get(s)));
				}
			}
		}
		runID = Utils.findFirstAvailable(masterConfig.get("logFile"));
		Log.i("Started initialization of FVTS with run id "+runID, true);
		CLI.logFile = new File(masterConfig.get("logFile").replaceAll("\\$1", "" + runID));

		String allowOverride = masterConfig.get("allowOverride");

		if (allowOverride == null || allowOverride.equals("")) {

			allowOverride = "true";
		}

		ImageDumpScheduler.start();

		VisionCameraServer.startServer();

		for (VisionParams params : visionParamsList) {
			try {

				String s = params.getByName("enabled").getValue();

				if (s == null || s.equals("")) {
					s = "true";
				}
				boolean enabled = Boolean.valueOf(s);

				Log.i(params.getByName("name").getValue() + " enabled: " + enabled, true);

				MainThread thread = new MainThread(params);
				if (enabled) {
					thread.start();
				}
				threads.add(thread);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(e.getMessage(), true);
			}

		} // end main vision startup loop
			// Should network tables be started so that the settings can be overridden?
		boolean allowOverrideB = Boolean.valueOf(allowOverride);

		if (allowOverrideB)
			NetworkTablesManager.init();
	}

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * @param args The command line arguments
	 * @throws Exception If the configs fail to load
	 */

	public static void main(String[] args) throws Exception {
		new Main(args);
	}
}
