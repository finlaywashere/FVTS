package ca.team2706.fvts.core.interfaces;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.data.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;

public abstract class AbstractInterface {
	public abstract void publishData(VisionData data, MainThread thread);
	public abstract void init(MainThread thread);
	public abstract List<AttributeOptions> getOptions();
	
	private String name;
	
	public String getName() {
		return name;
	}
	public AbstractInterface(String name) {
		this.name = name;
	}
	
	static {
		init();
	}
	public static void init() {
		interfaces = new ArrayList<AbstractInterface>();
		interfaces.add(new NetworkTablesInterface());
		interfaces.add(new DummyInterface());
		interfaces.add(new ImageDumpInterface());
		interfaces.add(new CSVLogInterface());
	}

	public static List<AbstractInterface> interfaces = null;
	public static AbstractInterface getByName(String name) {
		if(interfaces == null) {
			// Init
			init();
		}
		for(AbstractInterface i : interfaces) {
			if(i.getName().equalsIgnoreCase(name))
				return i;
		}
		Log.e("Failed to find pipeline by the name of "+name, true);
		return null;
	}
}
