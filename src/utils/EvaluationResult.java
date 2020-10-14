package utils;

import java.time.LocalDateTime;

public class EvaluationResult {
	private final int userID;
	private final int itemID;
	private int measuredSojournTime;
	private final int localPrediction;
	private final int globalPrediction;
	private final int randomPrediction;
	private boolean isRandomRecommendation = true;
	private final double tick;
	private final LocalDateTime recommendationTimestamp;
	

	public EvaluationResult(int userID, int itemID, int prediction, int randomPrediction, int globalPrediction, double tick, LocalDateTime timestamp, boolean _isRandom) {
		this.userID = userID;
		this.itemID = itemID;
		this.localPrediction = prediction;
		this.randomPrediction = randomPrediction;
		this.globalPrediction = globalPrediction;
		this.tick = tick;
		this.recommendationTimestamp = timestamp;
		this.measuredSojournTime = 0;
		this.isRandomRecommendation = _isRandom;
	}
	
	public void setMeasuredSojournTime(int measuredSojournTime) {
		this.measuredSojournTime = measuredSojournTime;
	}
	
	public int getRandomPrediction() {
		return randomPrediction;
	}
	
	public int getError() {
		return localPrediction - measuredSojournTime; 
	}

	public int getMeasuredSojournTime() {
		return measuredSojournTime;
	}

	public int getUserID() {
		return userID;
	}

	public int getItemID() {
		return itemID;
	}

	public int getLocalPrediction() {
		return localPrediction;
	}

	public LocalDateTime getRecommendationTimestamp() {
		return recommendationTimestamp;
	}
	
	public int getGlobalPrediction() {
		return globalPrediction;
	}
	
	public double getTick() {
		return tick;
	}

	public boolean isRandomRecommendation() {
		return isRandomRecommendation;
	}

	public void setRandomRecommendation(boolean _isRandomRec) {
		this.isRandomRecommendation = isRandomRecommendation;
	}
	
	@Override
	public String toString() {
		return "EvaluationResult [userID=" + userID + ", itemID=" + itemID 
				+ ", tick=" + tick 
				+ ", measuredSojournTime=" + measuredSojournTime 
				+ ", localPrediction=" + localPrediction
				+ ", globalPrediction=" + globalPrediction 
				+ ", randomPrediction=" + randomPrediction
				//+ ", recommendationTimestamp=" + recommendationTimestamp 
				//+ ", is RandomRec  " + this.isRandomRecommendation() + "]"
				;
	}

	
}
