package simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import profiles.InterestProfile;
import profiles.Behavior;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import result.RecommendationResult;
import utils.CSVWriter;
import utils.Counter;
import utils.EvaluationResult;
import configuration.Configuration;

// class Visitor represents a human that moves in the space
// all data aspects are considered in the class MobileDevice
public class Visitor {

	public static int idCounter = 0;
	private final int uid;
	private Counter counter;

	public enum State {
		goToNextExhibit, ongoingToExhibit, arrivedAtNewExhibit, stayingInFrontOfExhibit, leaveSpace, removeFromSpace
	}

	private State state;
	private Exhibit targetExhibit = null;
	public static int traceVisitor = Configuration.traceVisitor;

	private MobileDevice md;
	private final Behavior behavior;
	private final InterestProfile interestProfile;

	private final ContinuousSpace<Object> space;
	private final Grid<Object> grid;
	private GridPoint targetPoint = null;
	private GridPoint exit = new GridPoint(3, 10);

	public Visitor(ContinuousSpace<Object> space, Grid<Object> grid, Behavior behavior, InterestProfile profil) {
		this.state = State.goToNextExhibit; // visitor starts by going to next exhibit
		this.grid = grid;
		this.space = space;
		this.behavior = behavior;
		this.interestProfile = profil;
		this.uid = ++idCounter;
		this.md = new MobileDevice(grid, uid);
		if (Configuration.printEnter)
			System.out.println(
					"[ENTER] Visitor " + uid + "  " + behavior.getClass() + " profile: " + interestProfile.getClass());
	}

	// control loop:
	void move(Grid<Object> grid) {

		moveTowardsOnSpace(targetPoint); // visualization: move on grid
		GridPoint pt = grid.getLocation(this);
		this.getMobileDevice().exchangeDatagrams(pt); // get Data from neighbors

		processStates();

		// save local knowledge of visitor (= all data (zscores) he collected from
		// neighbors)
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (tick % Configuration.store_steps == 0) {
			CSVWriter.saveCollectedRatingsOfVisitor(this); // saves collected_ratings_of_visitor<id>_>tick>.csv
		}
	}

	// start state: state = State.goToNextExhibit;
	public void processStates() {
		if ((this.uid == Visitor.traceVisitor) && Configuration.showState)
			System.out.println("Visitor:" + this.uid + "  State --> " + this.state + " Target Point -> "
					+ this.targetPoint + " - Current location " + grid.getLocation(this));
		
		if (state == State.goToNextExhibit) {		
			if (hasVisitedAllExhibits()) { // special case: visitor has visited everything: goto exit
				targetPoint = exit;
				targetExhibit = null;
				state = State.leaveSpace;
			} else {
				targetExhibit = recommendMeNewExhibit(); // get new exhibit
				targetPoint = grid.getLocation(targetExhibit);
				state = State.ongoingToExhibit;
//				state = State.arrivedAtNewExhibit;
//				if (this.uid == Visitor.traceVisitor && Configuration.followVisits) {
//					 System.out.println("[go to new Exhibit - visitor:] " + this.uid + " target: "
//					 + targetExhibit);
//				}
			}
		}
		
		if (state == State.ongoingToExhibit) {
			if (targetExhibit != null) {
				if (targetPoint.equals(grid.getLocation(this)))
					state = State.arrivedAtNewExhibit;
			} else
				System.out.println("[ERROR] No target point in state onGoing");
		}
		
		if (state == State.arrivedAtNewExhibit) { // the visitor has just arrived at a new exhibit => draw sojourn time
			int steps = drawSojournTimeAndSaveResult() / 10;
//			int steps = drawSojournTimeAndSaveResult();
			state = State.stayingInFrontOfExhibit;
			counter = new Counter(steps);
			targetPoint = null;
			return;
		}

		if (state == State.stayingInFrontOfExhibit) {
			if (!counter.isExpired()) // stay => count down sojourn time
				counter.decrease();
			else {
				if (Math.random() > Configuration.exitProbability) // move to next exhibit with probability
					state = State.goToNextExhibit;
				else { // leave space
					state = State.leaveSpace;
					targetPoint = exit;
					targetExhibit = null;
				}
			}
		}

		if (state == State.leaveSpace) {
			state = State.removeFromSpace; // in next tick: visitor is removed from space (in class Scheduler)
			if (!md.getEvaluationResults().isEmpty()) {
				CSVWriter.saveEvaluatedRecommendations(this); // saves evaluated_recommendations_of_visitor<id>.csv
			}
			if (this.uid == Visitor.traceVisitor && Configuration.printWhomImet) {
				this.getMobileDevice().printWhomImetWith();
			}
			return;
		}
	}

	private int drawSojournTimeAndSaveResult() { // invoked when arriving at new exhibit
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		// draw sojourn time for target exhibit:
		int exhibitId = targetExhibit.getId();
		String exhibitType = targetExhibit.getType();
		int rankInInterest = interestProfile.getMeanValueForInterestType(exhibitType);
		int sojournTime = behavior.drawGaussianFromInterestRank(rankInInterest); // !!!! draw sojourn time !!!!!

		// set sojourn time in evaluation results
		if (md.getEvaluationResults().containsKey(exhibitId)) {
			System.out.println("!!!!!! exhibitId:" + exhibitId + " prec:"
					+ md.getEvaluationResults().get(exhibitId).getLocalPrediction() + " sojourn:" + sojournTime);
			md.getEvaluationResults().get(exhibitId).setMeasuredSojournTime(sojournTime); // set sojourn time in
																							// Evaluation results

			if (this.uid == Visitor.traceVisitor && Configuration.followRecommendedVisits) {
				System.out.println("[ARRIVED --- SOJOURNTIME] for visitor " + this.id() + "  tick:" + tick
				// + tick "type " + exhibitType
				// + "; interest level:" + rankInInterest
						+ "   EXHIBIT:" + exhibitId + "   SOJOURN TIME: " + sojournTime);
			}
		} else {
			if (this.uid == Visitor.traceVisitor && Configuration.followRandomVisits) {
				System.out.println("ARRIVED --- [RANDOMVISIT] for visitor " + this.id() + "  tick:" + tick
				// + " of type " + exhibitType
				// + " interest level:" +
				// interestProfile.getMeanValueForInterestType(exhibitType)
						+ "   EXHIBIT:" + exhibitId + "  SOJOURN TIME:" + sojournTime);
			}
		}

		md.registerSojournTime(exhibitId, sojournTime); // in ownVisits and globalRating
		return sojournTime;
	}

	private Exhibit recommendMeNewExhibit() {
		int recommendatedItem = 0;
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		RecommendationResult result = md.calculateRecommendation();  // calculate recommendation with predictions

		this.getMobileDevice().printWhomImetWith();

		if (result != null) {
			recommendatedItem = result.itemID();
			// store result in EvaluationResults or item with itemID: 
			md.getEvaluationResults().put(result.itemID(),
					new EvaluationResult(uid, result.itemID(), result.predictionAsInt(), result.randomResult(),
							result.globalResult(), tick, LocalDateTime.now(), false));
			if (this.uid == Visitor.traceVisitor && Configuration.printRecommendationResults) {
				System.out.println("[NEW RECOMMENDATION] for visitor " + this.id() + "  tick:" + tick
						+ " GoTo EXHIBIT: " + result.itemID());
				System.out.println("           ---  prediction: " + result.predictionAsInt() + "  global: "
						+ result.globalResult() + "  random: " + result.randomResult());
			}
		} else if (this.uid == Visitor.traceVisitor) {
			 System.out.println("[Visitor " + this.id() + "] --- NOT Found Exhibit to Recommend " + " tick:" + tick);
		}

		// return only a yet unvisited item:
		Exhibit exhibit = null;
		List<Exhibit> targets = getAllExhibits();
		if (recommendatedItem != 0) {
			for (Exhibit e : targets) {
				if (e.getId() == recommendatedItem && !md.hasVisited(e.getId())) {
					exhibit = e;
					break;
				}
			}
		} else {  // return random exhibit
			for (Exhibit e : targets) {
				if (!md.hasVisited(e.getId())) {
					exhibit = e;
//					if (this.uid == this.traceVisitor) {
					if (this.uid == Visitor.traceVisitor && Configuration.printRecommendationResults) {
						// System.out.println("[RANDOM VISIT] Visitor " + this.id() + "] item:" +
						// e.getId() + " of type: " + e.getType());

						// System.out.println("[RANDOM VISIT] Visitor " + this.id() + "] item:" +
						// e.getId() + " of type:" + e.getType());
					}
					break;
				}
			}
		}

		return exhibit;
	}

	private List<Exhibit> getAllExhibits() {
		Iterable<Object> objects = grid.getObjects();
		List<Exhibit> targets = new ArrayList<>();
		objects.forEach(x -> {
			if (x.getClass() == Exhibit.class)
				targets.add((Exhibit) x);
		});
		Collections.shuffle(targets);
		return targets;
	}

	private double distance(GridPoint p1, GridPoint p2) {
		double ycoord = Math.abs(p1.getY() - p2.getY());
		double xcoord = Math.abs(p1.getX() - p2.getX());
		double distance = Math.sqrt(ycoord * ycoord + xcoord * xcoord);

		return distance;

	}

	private void moveTowardsOnSpace(GridPoint pt) {
		if (pt == null) {
			return;
		}

		int steplength = behavior.velocity();

		if (!pt.equals(grid.getLocation(this))) {
//			if (this.uid == Visitor.traceVisitor)
//				System.out.println("[DISTANCE] -> " + pt + " and " + grid.getLocation(this)  + distance(pt, grid.getLocation(this)));

			if (distance(pt, grid.getLocation(this)) <= this.behavior.velocity()) {
				grid.moveTo(this, pt.getX(), pt.getY());
			} else {
				NdPoint myPoint = space.getLocation(this);
				NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
				space.moveByVector(this, steplength, angle, 0);
				myPoint = space.getLocation(this);
				grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
				if (this.uid == Visitor.traceVisitor && Configuration.printRecommendationResults) {
					// System.out.println("... " + this.uid + " new location is [" + (int)
					// myPoint.getX() + ", " + (int) myPoint.getY() + "]");
				}
			}
		}
	}

	public boolean toRemoveFromSpace() {
		return state == State.removeFromSpace;
	}

	private boolean hasVisitedAllExhibits() {
		return md.numberOfVisitedExhibits() == Configuration.numberOfExhibits;
	}

	public MobileDevice getMobileDevice() {
		return md;
	}

	public int id() {
		return uid;
	}

	public InterestProfile getInterestProfile() {
		return interestProfile;
	}

	public String toString() {
		String toReturn = new String();
		toReturn = "Visitor " + uid;
//		toReturn += "STATE -> " + this.state + "\n";
//		toReturn += "TARGET_EXHIBIT -> " + this.targetExhibit + "\n";

		return toReturn;
	}

}
