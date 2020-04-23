package profiles;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public abstract class InterestProfile {
	protected HashMap<String, Integer> interest;

	public int getMeanValueForInterestType(String name){
        return interest.getOrDefault(name, 0);
    }
	
	public String getHighestInterest() {
		Set<Entry<String, Integer>> entries = interest.entrySet();
		String interest = "";
		int max = 0;
		for(Entry<String, Integer> e : entries) {
			if (e.getValue() > max) {
				max = e.getValue();
				interest = e.getKey();
			}
		}
		return interest;
	}
}
