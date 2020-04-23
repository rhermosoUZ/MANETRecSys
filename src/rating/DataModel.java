package rating;

import java.util.Map;

public interface DataModel {

    double preferenceFor(int userID, int itemID);
    boolean userHasRatedItem(int userID, int itemID);
    int numberOfUsers();
    int numberOfItems();
    public Map<Integer, UserData> getRatings();
    UserData user(int userID);
    boolean hasUser(int userID);
   
}
