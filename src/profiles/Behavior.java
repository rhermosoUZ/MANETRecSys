package profiles;

import java.util.HashMap;

import utils.GaussianRandomGenerator;

public abstract class Behavior {
	// contains list with mean value of sojourn time for each interest profile:
	protected HashMap<Integer, Integer> sojournTimes;
	protected int velocity;
	
	public int drawGaussianFromInterestRank(int interestRank){
		int mean = sojournTimes.getOrDefault(interestRank, 0);
        //if(mean == 0) return 10; 
//        double standardDeviation = mean/10;
        double standardDeviation = mean/50;
        return GaussianRandomGenerator.nextPositivInt(mean, standardDeviation);
    }
	
	public int velocity() {
		return velocity;
	}
}

