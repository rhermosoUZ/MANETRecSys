package profiles;

import java.util.HashMap;

public class SixthInterestProfile  extends InterestProfile {

	public SixthInterestProfile(){
        interest = new HashMap<>();
        interest.put(ExhibitType.exhibit1.getType(), 2);
        interest.put(ExhibitType.exhibit2.getType(), 6);
        interest.put(ExhibitType.exhibit3.getType(), 1);
        interest.put(ExhibitType.exhibit4.getType(), 3);
        interest.put(ExhibitType.exhibit5.getType(), 4);
        interest.put(ExhibitType.exhibit6.getType(), 5);
    }

}
