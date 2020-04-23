package profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BehaviorFactory {
	static private final List<Behavior> behaviors = fillBehaviourList(); 
	
	private static List<Behavior> fillBehaviourList() {
		List<Behavior> behaviours = new ArrayList<>();
    	behaviours.add(new FastBehavior());
    	behaviours.add(new MediumBehavior());
    	behaviours.add(new SlowBehavior());
    	return behaviours;
	}
	static public Behavior getBehavior() {
		Random rand = new Random();
		return behaviors.get(rand.nextInt(behaviors.size()));
	}
	

}
