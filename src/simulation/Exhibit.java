package simulation;

public class Exhibit {
	
	static int idCounter = 0;
	
	private final String type;
	private final int id;
	
	public Exhibit() {
		this.type = "none";
		this.id = 0;
	}
	
	public Exhibit(String type) {
		this.type = type;
		this.id = ++idCounter;
	}
	
	public String getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return ""+id+": "+type;
	}
}
