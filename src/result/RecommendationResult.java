package result;

public class RecommendationResult {
    private final int itemID;
    private final double prediction;
    private final double random;
    private final double global;
    
    public RecommendationResult(int itemID, double prediction, double random, double global){
        this.itemID = itemID;
        this.prediction = prediction;
        this.random = random;
        this.global = global;
    }

    public int itemID(){
        return itemID;
    }
    
    public double prediction() {
    	return prediction;
    }

    public int predictionAsInt(){
        return (int) Math.round(prediction);
    }
    
    public int randomResult() {
    	return (int) Math.round(random); 
    }
    
    public int globalResult() {
    	return (int) Math.round(global); 
    }
    
    public boolean isEmpty(){return prediction == 0;}

    @Override
    public String toString(){
        return "Item " + itemID + "; prediction: " + prediction;
    }
}
