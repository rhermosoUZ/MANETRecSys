package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Normalisation {
	private double mean;
	public double getStandardDeviation() {
		return standardDeviation;
	}

	public double getMean() {
		return mean;
	}

	private double variance;
	private double standardDeviation;
	
	public Map<Integer, SojournTimeData> zscoreForRecommendation(Map<Integer, SojournTimeData> allData) {
		int count = allData.size();
		Collection<Integer> times = new ArrayList<>();
		allData.forEach((x,y) -> times.add(y.getSojournTimeAsInt()));
        
        mean = times.stream()
                			.mapToInt(Integer::intValue)
                			.average()
                			.orElse(0.0);
        
        variance = times.stream().parallel()
                			.map(x -> (x - mean) * (x - mean))
                			.reduce((x, y) -> x + y)
                			.orElse(0.0);
        
        variance /= count;
        
        standardDeviation = Math.sqrt(variance);
        
        return normalizeMap(allData, mean, standardDeviation);
	}
	
	public double[][] zscore(double[][] matrix, int user){
		
		for(int row = 0; row < matrix.length; ++row) {
			int count = 0;
	        double sum = 0;
			for(int column = 0; column < matrix[0].length; ++column){
	            if(matrix[row][column] != 0.0){
	                sum += matrix[row][column];
	                count++;
	            }
	        }
			
			double mean = sum/count;
		    double variance = 0.0;

		    for(int column = 0; column < matrix[0].length; ++column){
		    	if(matrix[row][column] != 0.0) {
		    		double value = matrix[row][column] - mean;
		    		variance += value*value;
		        }
		    }

		    variance /= count;
		    double standardDeviation = Math.sqrt(variance);
		    
		    for(int column = 0; column < matrix[0].length; ++column)
		    	if(matrix[row][column] != 0.0) {
		    		matrix[row][column] = (matrix[row][column] - mean)/standardDeviation;
		    	} else {
		    		matrix[row][column] = Double.NaN;
		    	}
		}
		
		return matrix;
	}
	
	public Map<Integer, SojournTimeData> zscoreForDataTransfer(Map<Integer, SojournTimeData> allData) {
		
		int count = allData.size();
		
		ArrayList<Integer> times = new ArrayList<>();
		allData.forEach((x,y) -> times.add(y.getSojournTimeAsInt()));
        
		double mean = times.stream()
							.mapToInt(Integer::intValue)
							.average()
							.orElse(0.0);
		
        double variance = times.stream()
                			.map(x -> (x - mean) * (x - mean))
                			.reduce((x, y) -> x + y)
                			.orElse(0.0) / count;
        
        double standardDeviation = Math.sqrt(variance);
        
        return normalizeMap(allData, mean, standardDeviation);
	}
	
	private Map<Integer, SojournTimeData> normalizeMap(Map<Integer, SojournTimeData> allData, double mean, double standardDeviation){
		 Map<Integer, SojournTimeData> normalizedData = new HashMap<>();
	     for(Integer i : allData.keySet()) {
	    	 SojournTimeData entry = allData.get(i);
	    	 double newEntry = 0.0;
	    	 if(standardDeviation != 0.0) {
	        	newEntry = (entry.getSojournTimeAsInt() - mean)/standardDeviation;
	        }
	        normalizedData.put(i, new SojournTimeData(newEntry, entry.timestamp()));
	     }
	     return normalizedData;
	}
	
	public int denormalise(double value) {
		return (int)Math.round(value * standardDeviation + mean);
	}
}
