package utils;

import java.time.LocalDateTime;

public final class SojournTimeData {
	private final double sojournTime;
	private final LocalDateTime timestamp;
	
	public SojournTimeData(double sojournTime, LocalDateTime timestamp) {
		this.sojournTime = sojournTime;
		this.timestamp = timestamp;
	}
	
	public double getSojournTime() {
		return sojournTime;
	}
	
	public int getSojournTimeAsInt() {
		return (int) Math.round(sojournTime);
	}
	
	public LocalDateTime timestamp() {
		return timestamp;
	}
	
	public String toString() {
		return " " + this.sojournTime;
	}

}
