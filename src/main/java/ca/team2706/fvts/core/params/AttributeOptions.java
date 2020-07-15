package ca.team2706.fvts.core.params;

public class AttributeOptions {
	
	public static final int DEFAULT = 0;
	public static final int SLIDER = 1;
	public static final int SLIDER_DOUBLE = 2;
	
	private String name;
	private boolean required;
	
	private int type,min,max,multiplier;
	
	public AttributeOptions(String name, boolean required, int type, int min, int max, int scale) {
		this.name = name;
		this.required = required;
		this.type = type;
		this.min = min;
		this.max = max;
		this.multiplier = scale;
	}
	public int getMultiplier() {
		return multiplier;
	}
	public AttributeOptions(String name, boolean required, int type, int min, int max) {
		this(name, required,type,min,max,1);
	}
	public AttributeOptions(String name, boolean required) {
		this(name,required,DEFAULT,0,0,1);
	}
	public String getName() {
		return name;
	}
	public boolean isRequired() {
		return required;
	}
	public int getType() {
		return type;
	}
	public int getMin() {
		return min;
	}
	public int getMax() {
		return max;
	}
}
