package profiles;

import java.util.HashMap;

public class MediumBehavior extends Behavior {
	
	public MediumBehavior() {
		sojournTimes = new HashMap<>();
		sojournTimes.put(6,20);
		sojournTimes.put(5,40);
		sojournTimes.put(4,60);
        sojournTimes.put(3,80);
        sojournTimes.put(2,100);
        sojournTimes.put(1,120);
        velocity = 2;
	}
}
