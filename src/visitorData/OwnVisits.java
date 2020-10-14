package visitorData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.SojournTimeData;

public class OwnVisits {
	private int uid;
	
	// key = exhibitID
	// SojournTime = sojourntime + timestamp
	private final Map<Integer, SojournTimeData> sojournTimesAtExhibits = new HashMap<>();
	private final List<Integer> visitingOrder = new ArrayList<>();

	public OwnVisits(int uid) {
		this.uid = uid;
	}
	
	public void addSojournTimeOfVisit(int exhibitId, double sojournTime) {
		sojournTimesAtExhibits.putIfAbsent(exhibitId, new SojournTimeData(sojournTime, LocalDateTime.now()));
		visitingOrder.add(exhibitId);
	}
	
	public int numberOfVisitedExhibits() {
		return sojournTimesAtExhibits.size();
	}
	
	public double getSojournTime(int exhibitId) {
		return sojournTimesAtExhibits.get(exhibitId).getSojournTime();
	}
	
	public boolean itemhasBeenVisited(int itemID) {
		return sojournTimesAtExhibits.containsKey(itemID);
	}
	
	public Map<Integer, SojournTimeData> getSojournTimesMap() {
		return sojournTimesAtExhibits;
	}
	public void printSojournTimesOfVisitors() {
		String output = "visitor " + this.uid + " number of visits:" + visitingOrder.size();
		for (int exhibitId: sojournTimesAtExhibits.keySet()) {
			output += " " + exhibitId +":" + sojournTimesAtExhibits.get(exhibitId).getSojournTimeAsInt();
		}		
		System.out.println(output);
	}
}
