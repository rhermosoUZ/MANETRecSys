package recommender;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rating.DataModel;
import rating.UserData;
import result.ItemSimilarityResult;
import result.RecommendationResult;
import simulation.Visitor;

public class ItemBasedRecommender {
	private final static int numberOfNeighbors = 40;
	private final static double threshold = 0.5;

	// returns a list with the best recommendations (related to a user and a model)
	// call recommend(userID, model)
	public static List<RecommendationResult> orderedRecommendations(int userID, DataModel model,
			int numberOfRecommendations) {
//			if (userID == Visitor.traceVisitor) {
//				System.out.println("[LOCAL PREDICTION MODEL] visitor:" + userID);
//				System.out.println(model);
//			}
		RecommendationResult[] allResults = recommend(userID, model);
//	    	if (userID == Visitor.traceVisitor) {
//	    		String toPrint = "";
//	    		for (RecommendationResult r: allResults) {
//	    			toPrint += r + "  ";
//	    		}
//	    		System.out.println("!!!!!!!!! " + toPrint);
//	        }
		List<RecommendationResult> results = new LinkedList<>();

		Arrays.sort(allResults, (x, y) -> Double.compare(y.prediction(), x.prediction()));

//	    	if (userID == Visitor.traceVisitor) {
//	    		String toPrint = "";
//	    		for (RecommendationResult r: allResults) {
//	    			toPrint += r + "  ";
//	    		}
//	    		System.out.println("!!!!!!!!! " + toPrint);
//	        }

		int count = 0;
		for (int i = 0; i < allResults.length; ++i) {
			if (count < numberOfRecommendations && !allResults[i].isEmpty()) {
				results.add(allResults[i]);
				count++;
			}

		}
//	    	if (userID == Visitor.traceVisitor) {
//	    		String toPrint = "";
//	    		for (RecommendationResult r: results) {
//	    			toPrint += r + "  ";
//	    		}
//	    		System.out.println("!!!!!!!!! " + toPrint);
//	        }
//	        
		return results;
	}

	// return for userID  a list of zscores for all items
	// calls value(serID, itemID, model)
	public static RecommendationResult[] recommend(int userID, DataModel model) {

		RecommendationResult[] results = new RecommendationResult[model.numberOfItems()];

		for (int itemID = 1; itemID <= model.numberOfItems(); ++itemID) {
			if (!model.userHasRatedItem(userID, itemID)) {
				double pred = value(userID, itemID, model);
				results[itemID - 1] = new RecommendationResult(itemID, pred, 0, 0);
			} else {
				results[itemID - 1] = new RecommendationResult(itemID, 0, 0, 0);
			}
		}
//		if (userID == Visitor.traceVisitor) {
//			String toPrint = "";
//			for (int i = 0; i < results.length; i++) {
//				toPrint += results[i] + " - ";
//			}
//			System.out.println("[RETURNED BY RECOMMEND] " + toPrint);
//		}		
		return results;
	}

	public static double recommendationFor(int userID, int itemID, DataModel model) {
		return value(userID, itemID, model);
	}

	// yields prediction according to z_u_e' in the paper
	private static double value(int userID, int itemID, DataModel model) {
		double numerator = 0;
		double denominator = 0;
		double value = 0;
		int count = 0;
		// iterate the list of the items best correlated to itemID:
		for (ItemSimilarityResult isr : nearestNeighborsFor(itemID, model)) {
			int otherItemId = isr.correlatedItemId();
			double rawRating = 0.0;

			if (model.userHasRatedItem(userID, otherItemId)) {  
				rawRating = model.preferenceFor(userID, otherItemId);  //z_u_e in formula 
				double sim = isr.correlation();		//w_e_e'
				numerator += sim * rawRating;
				denominator += sim;
				count++;
			}

			if (count == numberOfNeighbors) {
				break;
			}

		}
		if (denominator != 0.0) {
			value = numerator / denominator;
		}
		return value;
	}

	// return a list with correlated items with a correlation to itemID of at least 0.5
	private static List<ItemSimilarityResult> nearestNeighborsFor(int itemID, DataModel model) {
		// for itemID: get all correlatiosn to others 
		ItemSimilarityResult[] results = allItemSimilarities(itemID, model);

		Arrays.sort(results, (x, y) -> Double.compare(y.correlation(), x.correlation()));

		List<ItemSimilarityResult> result = new LinkedList<>();

		for (ItemSimilarityResult isr : results) {
        	//System.out.println("####### nearestNeighbors for --> " + isr);
			if (itemID != isr.correlatedItemId() && isr.correlation() >= threshold) {
				result.add(isr);
			}
		}
		return result;
	}
	
	// returns for a certain item (itemID) the list the correlations to all other items
	// array of ItemSimilarityResults (= triple: itemID, correlatedItemID, correlation)
	private static ItemSimilarityResult[] allItemSimilarities(int itemID, DataModel model) {
		ItemSimilarityResult[] results = new ItemSimilarityResult[model.numberOfItems()];

		for (int itemToCompare = 1; itemToCompare <= model.numberOfItems(); ++itemToCompare) {
			double correlation = itemCosineSimilarity(itemID, itemToCompare, model);
	    	//System.out.println("Correlation [" + itemID + "," + itemToCompare + "]" + " = " + correlation);
			results[itemToCompare - 1] = new ItemSimilarityResult(itemID, itemToCompare, correlation);
		}

		return results;
	}
	// returns one value: adjusted cosine similarity (w_e_e´in the paper) between item e and item e'
	// seems to be correct 
	private static double itemCosineSimilarity(int itemID1, int itemID2, DataModel model) {
		double numerator = 0.0;
		double denominator1 = 0.0;
		double denominator2 = 0.0;

//	        System.out.println("+++++++++ Number of users --> " + model.numberOfUsers());
//	        for(int user = 1; user <= model.numberOfUsers(); ++user){
		if (model.getRatings() != null) {
			for (int user : model.getRatings().keySet()) {  // iterate over all users in model:
				
				// itemID1 and itemID2 are both visited by the user
				if (model.userHasRatedItem(user, itemID1) && model.userHasRatedItem(user, itemID2)) {
					double itemValue = model.preferenceFor(user, itemID1);  // rating of 1. item
					double itemToCompareValue = model.preferenceFor(user, itemID2); // rating of 2. item
					numerator += itemToCompareValue * itemValue;
					denominator1 += itemValue * itemValue;
					denominator2 += itemToCompareValue * itemToCompareValue;
//	        			System.out.println("_________ Does HAVE both items for visitor " + user);
				}

			}
		}

		return numerator / (Math.sqrt(denominator1 * denominator2));
	}
}
