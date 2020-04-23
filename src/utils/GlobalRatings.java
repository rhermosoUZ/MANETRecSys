package utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import configuration.Configuration;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import result.RecommendationResult;

public class GlobalRatings {
	
	private static final class InstanceHolder {
		static final GlobalRatings INSTANCE = new GlobalRatings();
	}
	 
	//private final Map<Integer, Map<Integer, Integer>> recommendationMatrix;
	private final Map<Integer, Map<Integer, Integer>> sojournTimeMatrix;
	
	private GlobalRatings() {
	//	recommendationMatrix = new HashMap<>();
		sojournTimeMatrix = new HashMap<>();
	}
		  
	public static GlobalRatings getInstance () {
		return InstanceHolder.INSTANCE;
	}
	 
//	public void addRecommendation(int user, RecommendationResult recommendation) {
//		if(recommendationMatrix.containsKey(user)) {
//			 recommendationMatrix.get(user).put(recommendation.itemID(), recommendation.predictionAsInt());
//		} else {
//			 Map<Integer, Integer> results = new HashMap<>();
//			 results.put(recommendation.itemID(), recommendation.predictionAsInt());
//			 recommendationMatrix.put(user, results);
//		}
//	}
	
	public void addSojournTime(int uid, int exhibitID, int sojournTime){
		if(sojournTimeMatrix.containsKey(uid)) {
			sojournTimeMatrix.get(uid).put(exhibitID, sojournTime);
		} else {
			 Map<Integer, Integer> results = new HashMap<>();
			 results.put(exhibitID, sojournTime);
			 sojournTimeMatrix.put(uid, results);
		}
	}
	 
	public double[][] getMatrix() {
		Set<Integer> visitorsId = sojournTimeMatrix.keySet();
			
		int numberOfVisitors = visitorsId.stream()
								.max(Comparator.naturalOrder())
								.orElse(0);
			
		int numberOfExhibits = Configuration.numberOfExhibits;
			
		double[][] sojournTimesMatrix = new double[numberOfVisitors][numberOfExhibits];
			
		for(Integer visitorId: sojournTimeMatrix.keySet()) {
			Map<Integer, Integer> recommendations = sojournTimeMatrix.get(visitorId);
			for(Integer exhibitId: recommendations.keySet()) {
				sojournTimesMatrix[visitorId-1][exhibitId-1] = recommendations.get(exhibitId); 
			}
		}
			
		return sojournTimesMatrix;
	}
	
}
