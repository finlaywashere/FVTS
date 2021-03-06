package ca.team2706.fvts.main;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

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
import ca.team2706.fvts.core.data.Target;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.interfaces.DummyInterface;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.VisionParams;

public class DataViewer {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		System.out.println("FVTS DataViewer " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);

		// Must be included!
		// Loads OpenCV
		LibraryLoader.loadLibraries();
		
		Log.silence();

		Options options = new Options();

		Option ip = new Option("ip", true, "The IP address of the NetworkTables server");
		options.addOption(ip);
		Option developmentMode = new Option("dev", "development", false, "Puts Vision2019 in development mode");
		options.addOption(developmentMode);
		Option configFile = new Option("conf", "config", true, "Specifies an alternative config file");
		options.addOption(configFile);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			Log.e(e.getMessage(), true);
			formatter.printHelp("Vision2019", options);
			System.exit(1);
		}

		Main.developmentMode = cmd.hasOption("development");

		Main.visionParamsFile = cmd.getOptionValue("config", "visionParams.properties");

		// read the vision calibration values from file.
		Main.visionParamsList = new Utils().loadVisionParams();

		List<String> lines = ConfigParser.readLines(new FileInputStream("master.cf"));
		Map<String, String> masterConfig = ConfigParser.getPropertiesM(lines, "config");

		Map<String, String> masterEnabled = ConfigParser.getPropertiesM(lines, "enabled");

		// Go through and enable the configs
		for (String s : masterEnabled.keySet()) {
			for (VisionParams params : Main.visionParamsList) {
				if (params.getByName("name").getValue().equals(s)) {
					params.putAttrib(new Attribute("enabled", masterEnabled.get(s)));
				}
			}
		}

		String allowOverride = masterConfig.get("allowOverride");

		if (allowOverride == null || allowOverride.equals("")) {

			allowOverride = "true";
		}
		// Should network tables be started so that the settings can be overridden?
		boolean allowOverrideB = Boolean.valueOf(allowOverride);

		if (allowOverrideB)
			NetworkTablesManager.init();

		ImageDumpScheduler.start();

		VisionCameraServer.startServer();
		DummyInterface overrideInterface = (DummyInterface) AbstractInterface.getByName("dummy");

		for (VisionParams params : Main.visionParamsList) {
			try {

				String s = params.getByName("enabled").getValue();

				if (s == null || s.equals("")) {
					s = "true";
				}
				boolean enabled = Boolean.valueOf(s);

				Log.i(params.getByName("name").getValue() + " enabled: " + enabled, true);

				MainThread thread = new MainThread(params);
				List<AbstractInterface> interfaces = new ArrayList<AbstractInterface>();
				interfaces.add(overrideInterface);
				thread.setOutputInterface(interfaces);
				if (enabled) {
					thread.start();
				}
				Main.threads.add(thread);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}
		} // end main vision startup loop
		Scanner in = new Scanner(System.in);
		System.out.print("Constant or triggered? C/T: ");
		boolean constant = in.nextLine().equalsIgnoreCase("C");
		if (constant) {
			while (true) {
				for(MainThread thread : Main.threads) {
					String name = thread.getVisionParams().getByName("name").getValue();
					Lock l = overrideInterface.locks.get(name);
					VisionData data = overrideInterface.lastFrame.get(name);
					l.unlock();
					System.out.println("Data (" + data.params.getByName("name").getValue() + "):");
					System.out.println("FPS: " + data.fps);
					System.out.println("Number of targets found: " + data.targetsFound.size());
					System.out.println("Preffered target found: " + (data.preferredTarget != null));
					if (data.preferredTarget != null) {
						System.out.println("Preffered target: ");
						System.out.println("Distance: " + data.preferredTarget.data.get("distance"));
						System.out.println("X-Centre: " + data.preferredTarget.data.get("xCentreNorm"));
						System.out.println("Y-Centre: " + data.preferredTarget.data.get("yCentreNorm"));
						System.out.println("Area: " + data.preferredTarget.data.get("areaNorm"));
					}
					for (int i = 0; i < data.targetsFound.size(); i++) {
						Target t = data.targetsFound.get(i);
						System.out.println("Target #" + (i + 1) + ": ");
						System.out.println("Distance: " + t.data.get("distance"));
						System.out.println("X-Centre: " + t.data.get("xCentreNorm"));
						System.out.println("Y-Centre: " + t.data.get("yCentreNorm"));
						System.out.println("Area: " + t.data.get("areaNorm"));
					}
					data = null;
				}
			} // end data collection loop
		}else {
			System.out.println("Hit enter to trigger a capture!");
			while(true) {
				in.nextLine();
				for(MainThread thread : Main.threads) {
					String name = thread.getVisionParams().getByName("name").getValue();
					Lock l = overrideInterface.locks.get(name);
					VisionData data = overrideInterface.lastFrame.get(name);
					l.unlock();
					System.out.println("Data (" + data.params.getByName("name").getValue() + "):");
					System.out.println("FPS: " + data.fps);
					System.out.println("Number of targets found: " + data.targetsFound.size());
					System.out.println("Preffered target found: " + (data.preferredTarget != null));
					if (data.preferredTarget != null) {
						System.out.println("Preffered target: ");
						System.out.println("Distance: " + data.preferredTarget.data.get("distance"));
						System.out.println("X-Centre: " + data.preferredTarget.data.get("xCentreNorm"));
						System.out.println("Y-Centre: " + data.preferredTarget.data.get("yCentreNorm"));
						System.out.println("Area: " + data.preferredTarget.data.get("areaNorm"));
					}
					for (int i = 0; i < data.targetsFound.size(); i++) {
						Target t = data.targetsFound.get(i);
						System.out.println("Target #" + (i + 1) + ": ");
						System.out.println("Distance: " + t.data.get("distance"));
						System.out.println("X-Centre: " + t.data.get("xCentreNorm"));
						System.out.println("Y-Centre: " + t.data.get("yCentreNorm"));
						System.out.println("Area: " + t.data.get("areaNorm"));
					}
					data = null;
				}
			}
		}
	}
}
