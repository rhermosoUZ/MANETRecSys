package profiles;

import java.util.HashMap;

public class SlowBehavior extends Behavior {
	
	public SlowBehavior() {
		sojournTimes = new HashMap<>();
		sojournTimes.put(6,40);
		sojournTimes.put(5,80);
		sojournTimes.put(4,120);
        sojournTimes.put(3,160);
        sojournTimes.put(2,200);
        sojournTimes.put(1,240);
        velocity = 1;
	}
}
