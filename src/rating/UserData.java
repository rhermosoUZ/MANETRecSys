package rating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserData {
    private int userID;
    private Map<Integer, Double> ratings;

    public UserData(int userID, Map<Integer, Double> map){
        this.userID = userID;
        this.ratings = map;
    }

    public double ratingForItem(int itemID){
    	return ratings.getOrDefault(itemID, 0.0);
    }

    public int id(){
        return userID;
    }
    
    public int numberOfRatedItems(){
    	return ratings.size();
    }
    
    public Map<Integer, Double> getRatings(){
    	Map<Integer, Double> tmp = new HashMap<>();
		tmp.putAll(ratings);
		return tmp;
    }

    @Override
    public String toString(){
        String toReturn = "User "+userID;
        toReturn += Arrays.asList(ratings);
        return toReturn;
    }
}
