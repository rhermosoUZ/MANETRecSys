package profiles;

public enum ExhibitType {
	exhibit1("elephant"), exhibit2("ice bear"),exhibit3("monkey"), 
	exhibit4("tiger"),exhibit5("zebra"), exhibit6("eagle");
	
	private String type;
	
	public String getType() {
		return this.type;
	}
	
	ExhibitType(String type){
		this.type = type;
	}

}
