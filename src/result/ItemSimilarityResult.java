package result;

public class ItemSimilarityResult extends SimilarityResult {
    private final int itemID;
    private final int correlatedItemId;

    public ItemSimilarityResult(int itemId, int correlatedItemId, double correlation){
        super(correlation);
        this.itemID = itemId;
        this.correlatedItemId = correlatedItemId;
    }

    public int itemID(){
        return itemID;
    }

    public int correlatedItemId(){ return correlatedItemId;}

    @Override
    public String toString(){
        return "Item "+itemID+"; CorrelatedItem "+correlatedItemId+"; "+super.toString();

    }
}
