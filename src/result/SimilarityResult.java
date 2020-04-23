package result;


public abstract class SimilarityResult {
    private final double correlation;

    protected SimilarityResult(double correlation){
        this.correlation = correlation;
    }

    public double correlation(){
        return correlation;
    }

    public boolean isEmpty(){
        return correlation == 0.0;
    }

    @Override
    public String toString(){
        return "Similarity: "+correlation();
    }
}
