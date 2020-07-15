package ca.team2706.fvts.core.params;

public class Attribute {
	
	private String value;
	private String name;
	
	private int type,min,max,multiplier;
	
	public Attribute(String name, String value, int type, int min, int max, int multiplier) {
		this.value = value;
		this.name = name;
		this.type = type;
		this.min = min;
		this.max = max;
		this.multiplier = multiplier;
	}
	public Attribute(String name, String value) {
		this(name,value,AttributeOptions.DEFAULT,0,0,1);
	}
	
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public int getValueI() throws NumberFormatException{
		return Integer.valueOf(value);
	}
	public double getValueD() throws NumberFormatException{
		return Double.valueOf(value);
	}
	public boolean getValueB(){
		return Boolean.valueOf(value);
	}
	public void setValue(String value) {
		this.value = value;
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
	public int getMultiplier() {
		return multiplier;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
}
