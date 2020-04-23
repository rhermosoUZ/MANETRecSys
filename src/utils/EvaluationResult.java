package utils;

import java.time.LocalDateTime;

public class EvaluationResult {
	private final int userID;
	private final int itemID;
	private final int localPrediction;
	private final LocalDateTime recommendationTimestamp;
	private int measuredSojournTime;
	private final double tick;
	private final int randomPrediction;
	private final int globalPrediction;
	private boolean isRandomRecommendation = true;
	

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
		return "EvaluationResult [userID=" + userID + ", itemID=" + itemID + ", localPrediction=" + localPrediction
				+ ", recommendationTimestamp=" + recommendationTimestamp + ", measuredSojournTime="
				+ measuredSojournTime + ", tick=" + tick + ", randomPrediction=" + randomPrediction
				+ ", globalPrediction=" + globalPrediction + ", is RandomRec  " + this.isRandomRecommendation() + "]";
	}

	
}
