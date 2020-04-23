package utils;

public class Counter {
	private int amount;
	
	public Counter(int amount) {
		this.amount = amount;
	}
	
	public void decrease() {
		if (amount > 0) {
			amount--;
		}
	}
	
	public boolean isExpired() {
		return amount == 0;
	}
}
