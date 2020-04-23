package profiles;

import java.util.HashMap;

public class FastBehavior extends Behavior {
	
	public FastBehavior() {
		sojournTimes = new HashMap<>();
		sojournTimes.put(6,10);
		sojournTimes.put(5,20);
		sojournTimes.put(4,30);
        sojournTimes.put(3,40);
        sojournTimes.put(2,50);
        sojournTimes.put(1,60);
        velocity = 3;
    }
}
