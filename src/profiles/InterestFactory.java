package profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InterestFactory {
	static private final List<InterestProfile> interests = fillInterestList();							// list of all interest profiles

	static private List<InterestProfile> fillInterestList() {
		List<InterestProfile> interests = new ArrayList<>();
    	interests.add(new FirstInterestProfile());
    	interests.add(new SecondInterestProfile());
    	interests.add(new ThirdInterestProfile());
    	interests.add(new FourthInterestProfile());
    	interests.add(new FifthInterestProfile());
    	interests.add(new SixthInterestProfile());
    	return interests;
	}
	
	static public InterestProfile getInterest() {
		Random rand = new Random();
		return interests.get(rand.nextInt(interests.size()));
	}
}
