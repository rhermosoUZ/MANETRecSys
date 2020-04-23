package utils;

import java.time.LocalDateTime;

public final class Datagram {
	private final int visitorId;
	private final int itemId;
	private final double sojournTime;	
	private final LocalDateTime timestamp;
	private int ttl;
	
	public Datagram(int visitorId, int itemId, double sojournTime, LocalDateTime timestamp, int ttl) {
		this.visitorId = visitorId;
		this.itemId = itemId;
		this.sojournTime = sojournTime;
		this.timestamp = timestamp;
		this.ttl = ttl;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public synchronized void reduceTimeToLive() {
		if (ttl > 0) {
			ttl--;
		}
	}
	
	public int getVisitorId() {
		return visitorId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public double getSojournTime() {
		return sojournTime;
	}
	
	public synchronized boolean ttlIsExpired() {
		return ttl == 0;
	}
	
	public String toString() {
		return visitorId + "; " + itemId + "; "+sojournTime + "; " + timestamp + "; " + ttl;
	}

}
