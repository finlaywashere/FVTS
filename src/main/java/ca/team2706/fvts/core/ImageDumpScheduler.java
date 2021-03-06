package ca.team2706.fvts.core;

import java.util.ArrayList;
import java.util.List;

public class ImageDumpScheduler implements Runnable{
	public static final int QUEUE_LIMIT = 10;
	
	public static List<Bundle> bundles = new ArrayList<Bundle>();
	public static Thread thread;
	public static boolean b = true;
	public static boolean stop = false;
	@Override
	public void run() {
		try {
		while(b){
			if(bundles.size() > 0){
				Bundle b = bundles.get(0);
				bundles.remove(0);
				while(bundles.size() > QUEUE_LIMIT){
					bundles.remove(0);
				}
				
				try {
					Utils.imgDump(b.getRaw(), "raw-"+b.getSuffix(),b.getTimeStamp(),b.getOutputDir());
					Utils.imgDump(b.getBinMask(), "binMask-"+b.getSuffix(), b.getTimeStamp(),b.getOutputDir());
				} catch (Exception e) {
					//Non fatal error
				}
				if(stop) {
					ImageDumpScheduler.b = false;
				}
			}
		}
		}catch(Exception e) {
			Log.e(e.getMessage(),true);
		}
	}
	public static void schedule(Bundle b){
		bundles.add(b);
	}
	public static void start(){
		b = true;
		stop = false;
		thread = new Thread(new ImageDumpScheduler());
		thread.start();
	}

}
