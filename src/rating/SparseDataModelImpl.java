package rating;

import java.util.Map;
import java.util.Set;

public class SparseDataModelImpl implements DataModel {
    private final Map<Integer, UserData> ratings;
    private final int numberOfItems;

    public SparseDataModelImpl(Map<Integer, UserData> ratings, int numberOfItems){
        this.ratings = ratings;
        this.numberOfItems = numberOfItems;
    }

    @Override
    public double preferenceFor(int userID, int itemID){
//    	System.out.println("Ratings --> " + ratings);
        return ratings.get(userID).ratingForItem(itemID);
    }
    
    

    /**
	 * @return the ratings
	 */
	public Map<Integer, UserData> getRatings() {
		return ratings;
	}

	@Override
    public int numberOfUsers(){
        return ratings.size();
    }

    @Override
    public int numberOfItems(){
        return numberOfItems;
    }

    @Override
    public UserData user(int userID){
        return ratings.get(userID);
    }

    @Override
    public boolean userHasRatedItem(int userID, int itemID) {
    	if(ratings.containsKey(userID)){
    		UserData user = ratings.get(userID);
    		if(user.getRatings().containsKey(itemID)) {
    			return true;
    		}
    	}
        return false;
    }
    
    @Override
    public boolean hasUser(int userID) {
    	return ratings.containsKey(userID);
    }
    
    @Override
    public String toString() {
    	String toReturn = "";
    	for(Integer k: ratings.keySet()) {
    		toReturn += "User " + k + ":: ";
    		for(int exhibit = 1; exhibit <= numberOfItems(); ++exhibit) {
    			if(ratings.containsKey(k)){
    				toReturn += ratings.get(k).ratingForItem(exhibit) + ", ";
    			}
    		}
    		toReturn += "\n";
    	}
		return toReturn;
    }
    
    
 }
