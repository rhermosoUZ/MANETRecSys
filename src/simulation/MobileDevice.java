package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rating.DataModel;
import rating.DataModelImpl;
import rating.SparseDataModelImpl;
import rating.UserData;
import recommender.ItemBasedRecommender;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import result.RecommendationResult;
import utils.Datagram;
import utils.EvaluationResult;
import utils.GaussianRandomGenerator;
import utils.MapUtils;
import utils.Normalisation;
import utils.GlobalRatings;
import utils.SojournTimeData;
import visitorData.OwnVisits;
import configuration.Configuration;

public class MobileDevice {

	private int visitorID;
	private final int ttl = Configuration.ttl;
	private final List<Integer> whomImet = new ArrayList<>();			// for control purposes only 
	
	private final Map<Integer, EvaluationResult> evaluationResults = new HashMap<>();  // evaluationResults
	
	private OwnVisits ownVisits;  // !!new:  class instead of Map!! 
	
	private final ConcurrentMap<Integer, ConcurrentMap<Integer, Datagram>> collectedSojournTimes = new ConcurrentHashMap<>();
	
	private Normalisation norm = new Normalisation();
	private final Grid<Object> grid;

	public MobileDevice(Grid<Object> grid, int visitorID) {
		this.grid = grid;
		this.visitorID = visitorID;
		ownVisits = new OwnVisits(this.visitorID);  
	}

	public void exchangeDatagrams(GridPoint location) {

		GridCellNgh<Visitor> nghCreator = new GridCellNgh<Visitor>(grid, location, Visitor.class, 1, 1);
		List<GridCell<Visitor>> neigborCellsContainingVisitors = nghCreator.getNeighborhood(true); // list of neighbor cells with visitor
		
		for (GridCell<Visitor> cell : neigborCellsContainingVisitors) {
			Iterator<Visitor> iter = cell.items().iterator();
			// for each cell get all visitors inside
			while (iter.hasNext()) {
				Visitor v = iter.next();
				MobileDevice mobile = v.getMobileDevice(); // get data from neighbor

				if (!this.equals(mobile)) {
					if ( ! whomImet.contains(v.id()) ) {
						this.whomImet.add(v.id());
					}
					//Map<Integer, SojournTimeData> normalized = norm.zscoreForDataTransfer(visitedExhibitsMap);
					Map<Integer, SojournTimeData> normalized = norm.zscoreForDataTransfer(ownVisits.getSojournTimesMap());
					normalized.forEach((itemId, time) -> mobile
							.receiveData(new Datagram(visitorID, itemId, time.getSojournTime(), time.timestamp(), ttl)));

					ConcurrentMap<Integer, ConcurrentMap<Integer, Datagram>> cstTmp = MapUtils
							.copyMap(collectedSojournTimes);

					for (Map<Integer, Datagram> user : cstTmp.values()) {
						for (Datagram dg : user.values()) {
							if (!dg.ttlIsExpired()) {
								mobile.receiveData(dg);
							}
						}
					}
				}
			}
		}
	}

	public void receiveData(Datagram dg) {
		dg.reduceTimeToLive();

		if (collectedSojournTimes.containsKey(dg.getVisitorId())) {
			collectedSojournTimes.get(dg.getVisitorId()).put(dg.getItemId(), dg);

		} else {
			if (dg.getVisitorId() != visitorID) {
				ConcurrentHashMap<Integer, Datagram> entry = new ConcurrentHashMap<>();
				entry.put(dg.getItemId(), dg);
				collectedSojournTimes.put(dg.getVisitorId(), entry);
			}
		}
	}

	public void registerSojournTime(int exhibitId, int sojournTime) {
		if (this.visitorID == Configuration.traceVisitor) {
			//System.out.println("####### add sojourn time: ++ vsitorID:" + this.visitorID + " exhibit:" + exhibitId + " sojourmtijme:" + sojournTime);
		}
		ownVisits.addSojournTimeOfVisit(exhibitId, sojournTime);
		GlobalRatings.getInstance().addSojournTime(visitorID, exhibitId, sojournTime);
	}

	public boolean hasVisited(int exhibitId) {
		//return visitedExhibitsMap.containsKey(exhibitId);
		return ownVisits.itemhasBeenVisited(exhibitId);
	}

	public int numberOfVisitedExhibits() {
		//return visitedExhibitsMap.size();
		return ownVisits.numberOfVisitedExhibits();
	}

	public RecommendationResult calculateRecommendation() {

		RecommendationResult result = null;
		Map<Integer, UserData> ratingMap = getRatings();  // collected ratings from other visitors met
		if (this.visitorID == Configuration.traceVisitor)
			// System.out.println("!!!!!!!! size rating map " + ratingMap.size());
		if (hasVisitedEnoughExhibits()) { // see below more than x% items visited
			DataModel model = new SparseDataModelImpl(ratingMap, Configuration.numberOfExhibits);
			// getRatings() returns the same data as stored in visitor<id>_<ticks>.csv (=
			// data of visitors so far met)
//			if (this.visitorID == Visitor.traceVisitor) {
//				System.out.println("[LOCAL PREDICTION MODEL] ");
//				System.out.println(model);
//			}
			List<RecommendationResult> results = ItemBasedRecommender.orderedRecommendations(visitorID, model, 10);

			// print recommendations
//			if (this.visitorID == Visitor.traceVisitor) {
//				System.out.println();
//				String toPrint = "ordered recommendations):";
//				for(RecommendationResult r: results) {
//					System.out.println(r);
//				}	
//				System.out.println();
//			}
			if (results.size() > 0) {
				for (RecommendationResult rr : results) {
					//if (!visitedExhibitsMap.containsKey(rr.itemID()) && rr.prediction() > -0.3) {
					if (! ownVisits.itemhasBeenVisited(rr.itemID()) && rr.prediction() > -0.3) {
						result = rr;
						break;
					}
				}
			}
		}

		if (result != null) {
			int localPrediction = norm.denormalise(result.prediction()); // calculates sojourntime from zscore
			int randomPrediction = calculateRandomRecommendation();
			int globalPrediction = norm.denormalise(calculateGlobalPrediction(result.itemID()));

			result = new RecommendationResult(result.itemID(), localPrediction, randomPrediction, globalPrediction);
//			if (userID == Visitor.traceVisitor) {
//				System.out.println("!!!!!!!!!!!  result:" + result);
//			}
		}

		return result;
	}

	private boolean hasVisitedEnoughExhibits() {
		int numberOfExhibits = Configuration.numberOfExhibits;
		int minNumberOfVisitsForRecommendation = (int) Math.round(numberOfExhibits * 0.0);
		//return this.visitingOrder.size() > minNumberOfVisitsForRecommendation;
		return this.ownVisits.numberOfVisitedExhibits() > minNumberOfVisitsForRecommendation;
	}

	// returns the global prediction of the sojourn time of itemID for the user
	private double calculateGlobalPrediction(int itemID) {
		// calculates zscores
		// ResultMatrix should contain all sojourn times

//		if (userID == Visitor.traceVisitor) {
//			DataModel model1 = new DataModelImpl(ResultMatrix.getInstance().getMatrix());
//			System.out.println("[GLOBAL PREDICTION MODEL] for visitor nr:" + this.userID);
//			System.out.println(model1);   // seems to take the correct data
//		} 
		
		DataModel model = new DataModelImpl(norm.zscore(GlobalRatings.getInstance().getMatrix(), visitorID));
		
		if (this.visitorID == Visitor.traceVisitor) {
			//System.out.println("[GLOBAL PREDICTION ZSCORE MODEL] for visitor nr:" + this.visitorID);
			//System.out.println(model);   // seems to take the correct data
		} 		
		return ItemBasedRecommender.recommendationFor(visitorID, itemID, model);
	}

	private int calculateRandomRecommendation() {
		int count = ownVisits.numberOfVisitedExhibits();
		Collection<Integer> times = new ArrayList<>();
		ownVisits.getSojournTimesMap().forEach((x, y) -> times.add(y.getSojournTimeAsInt()));

		double mean = times.stream().mapToInt(Integer::intValue).average().orElse(0.0);

		double variance = times.stream().map(x -> (x - mean) * (x - mean)).reduce((x, y) -> x + y).orElse(0.0) / count;

		double standardDeviation = Math.sqrt(variance);

		int value = GaussianRandomGenerator.nextPositivInt((int) Math.round(mean), standardDeviation);
		return value;
	}

	private Map<Integer, UserData> getRatings() {
		Map<Integer, UserData> ratings = new HashMap<>();

		// put in ratings:
		// getUserRatings() returns Map with:
		// key = userID value = map of recommendations: (key: exhibitID, value:zscore)
		// i.e. all ratings of the user with this userID
		UserData temp = new UserData(visitorID, getUserRatings());
		// put the own ratings of this user into the ratings-map:
		ratings.put(visitorID, temp);
//		if (userID == Visitor.traceVisitor) {
//			System.out.println(" :::::::::::: getRatings " +temp);
//		} 

		// collectedSojournTimes contains all ratings of other visitors met (filled in
		// receiveData(Datagram ))
		// format: key= visitorId, value = Map (visitorID + datagram(=userID, itemID,
		// sojourntime))
		// run over all visitors i:
		for (Integer i : collectedSojournTimes.keySet()) {
			// rating = Map with key = visitorID value = datagram
			// fetch Map for visitor i
			Map<Integer, Datagram> rating = collectedSojournTimes.get(i);

			Map<Integer, Double> userRatings = new HashMap<>();

			// put in userRatings for each visitor (=her map of itemID, sojourn time)
			rating.forEach((itemID, dg) -> userRatings.put(itemID, dg.getSojournTime()));

			// ratings is the Matrix over all visitors (Map of itemId + sojournTime)
			ratings.put(i, new UserData(i, userRatings));
		}

//		if (userID == Visitor.traceVisitor) {
//			System.out.println();
//			String toPrint = "returned form  getRatings():";
//			for(Integer vId: ratings.keySet()) {
//				toPrint += Arrays.asList( vId + " ::: " + ratings.get(vId) + "\n");
//			}
//			System.out.println(toPrint);	
//			System.out.println();
//		}
		// returns the data stored in file Visitor_<vid>_<tick>.csv
		return ratings;
	}

	// seems to work correct: calculates the zscore for the sojourn times that this
	// user has visited so far
	private Map<Integer, Double> getUserRatings() {
		Map<Integer, Double> userRatings = new HashMap<>();
		// SojournTimeData is class containing sojourn time + time stamp
		// visitedExhibits = Map with exhibitId, SojournTimeData (filled in
		// addVisitedExhibit() - called in Vistor.step()
		// normalized is Map with exhibitID and normalized sojourn time
		//Map<Integer, SojournTimeData> normalized = norm.zscoreForRecommendation(visitedExhibitsMap);
		Map<Integer, SojournTimeData> normalized = norm.zscoreForRecommendation(ownVisits.getSojournTimesMap());
//		if (userID == Visitor.traceVisitor) {
//			String toPrint = "User "+ userID;
//			toPrint += Arrays.asList(visitedExhibits);
//			System.out.println("!!!!!!!!!!!!in getUserRatings():  visited exhibits:" + toPrint);
//		}
//		if (userID == Visitor.traceVisitor) {
//			String toPrint = "User "+ userID;
//			toPrint += Arrays.asList(normalized);
//			System.out.println("!!!!!!!!!!!!in getUserRatings():  normalized ratings:" + toPrint);
//		}

		// copy normalized into userRatings =(Map with exhibitID and sojourntime)
		normalized.forEach((itemID, sojournTime) -> userRatings.put(itemID, sojournTime.getSojournTime()));
//		if (userID == Visitor.traceVisitor) {
//			String toPrint = "User "+ userID;
//			toPrint += Arrays.asList(userRatings);
//			System.out.println("!!!!!!!!!!!!in getUserRAtings:  userRatings:" + toPrint);
//		}
		return userRatings;
	}

	public double[][] getSojournTimeMatrix() {
		Set<Integer> visitorsIds = collectedSojournTimes.keySet();

		int highestVisitorId = visitorsIds.stream().max(Comparator.naturalOrder()).orElse(0);

		if (visitorID > highestVisitorId) {
			highestVisitorId = visitorID;
		}

		int numberOfExhibits = Configuration.numberOfExhibits;

		double[][] sojournTimeMatrix = new double[highestVisitorId][numberOfExhibits];
		Arrays.stream(sojournTimeMatrix).forEach(a -> Arrays.fill(a, Double.NaN));

		for (Integer visitorId : collectedSojournTimes.keySet()) {
			Map<Integer, Datagram> times = collectedSojournTimes.get(visitorId);
			for (Integer exhibitId : times.keySet()) {
				sojournTimeMatrix[visitorId - 1][exhibitId - 1] = times.get(exhibitId).getSojournTime();
			}
		}

		//Map<Integer, SojournTimeData> normalized = norm.zscoreForDataTransfer(visitedExhibitsMap);
		// XXX Check this
		Map<Integer, SojournTimeData> normalized = norm.zscoreForDataTransfer(ownVisits.getSojournTimesMap());
		for (Integer exhibitId : normalized.keySet()) {
			sojournTimeMatrix[visitorID - 1][exhibitId - 1] = normalized.get(exhibitId).getSojournTime();
		}

		return sojournTimeMatrix;
	}

	public void printWhomImetWith() {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String metWith = "";
		whomImet.sort(null);
		for (int v: whomImet) {
			metWith += " - " + v;
		}
		if (this.visitorID == Visitor.traceVisitor && Configuration.printRecommendationResults)
			//System.out.println( "[#ALL VISITORS]:" + Visitor.idCounter + "   [MET " + this.visitorID  + " with " + whomImet.size() + " has met at tick:" + tick + " with " + whomImet.size() + " visitors:" + metWith);
			System.out.println( "[#ALL VISITORS]:" + Visitor.idCounter + "   [MET with "  + whomImet.size() + " visitors:" + metWith + "   --- at tick: " + tick);
	}
	
	public Map<Integer, EvaluationResult> getEvaluationResults() {
		return evaluationResults;
	}
	
	public OwnVisits getOwnVisits() {
		return ownVisits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + visitorID;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MobileDevice other = (MobileDevice) obj;
		if (visitorID != other.visitorID)
			return false;
		return true;
	}
}
