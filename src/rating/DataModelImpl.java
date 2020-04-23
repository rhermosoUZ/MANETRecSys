package rating;

import java.util.HashMap;
import java.util.Map;

public class DataModelImpl implements DataModel {
    //Holding the ratings of each user(rows) for each item(column)
    private final double[][] ratings;

    public DataModelImpl(double[][] matrix){
        this.ratings = matrix;
    }

    @Override
    public double preferenceFor(int userID, int itemID){
        return ratings[userID - 1][itemID - 1];
    }

    @Override
    public int numberOfUsers(){
        return ratings.length;
    }

    @Override
    public int numberOfItems(){
        return ratings[0].length;
    }

    @Override
    public UserData user(int userID){
        HashMap<Integer, Double> userRatings = new HashMap<>();
        for(int column = 0; column < numberOfItems(); ++column){
            userRatings.put(column+1, ratings[userID-1][column]);
        }
        return new UserData(userID, userRatings);
    }

    @Override
    public boolean userHasRatedItem(int userID, int itemID) {
        return preferenceFor(userID, itemID) != Double.NaN;
    }

	@Override
	public boolean hasUser(int userID) {
		return userID < ratings.length;
	}

	@Override
	public Map<Integer, UserData> getRatings() {
		// TODO Auto-generated method stub
		return null;
	}
	public String toString() {
		String matrix = "";
		int uid = 1;
		for (int i = 0; i < ratings.length; i++) {
			matrix += "User " + uid + ":  ";
			for (int j =0; j < ratings[0].length; j++) {
				matrix += ratings[i][j] + "  ";
			}
			matrix += "\n";
			uid++;
			
		}
		return  matrix;
	}

}
