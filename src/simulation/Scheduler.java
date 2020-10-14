package simulation;

import java.util.ArrayList;
import profiles.BehaviorFactory;
import profiles.InterestFactory;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import utils.CSVWriter;
import utils.GlobalRatings;
import configuration.Configuration;

public class Scheduler {

	private boolean simulationIsTerminated = false;

	private final ArrayList<Visitor> allVisitorsInSpace = new ArrayList<>(); // list of all visitors currently in zoo

	private final ContinuousSpace<Object> space;
	final Grid<Object> grid;
	private final GridPoint entry;

	public Scheduler(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		this.entry = new GridPoint(3, 3); // entry to zoo in the grid
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void run() {

		createNewVisitorAndLetHerIn();
		unregisterLeftVisitors();

		// for each tick: move all visitors
		for (Visitor v : allVisitorsInSpace) {
			v.move(grid);
		}

		// store all sojourn times in the system so far  --> global_sojourn_time_matrix<tick>.csv
		if (ticksHasPassed(Configuration.store_steps)) {
			CSVWriter.saveGlobalSojournTimeMatrix(GlobalRatings.getInstance().getMatrix());
		}

		// terminating simulation:
		if (this.simulationIsTerminated) {   				// is set in this.unregisterLeftVisitors()
			for (Visitor v : allVisitorsInSpace) {
				CSVWriter.saveEvaluatedRecommendations(v);  // -> evaluated_recommendations_of_visitor_<id>.csv  (with prediction errors)
			}
			RunEnvironment.getInstance().endRun();			// stop simulation
		}
	}

	private void createNewVisitorAndLetHerIn() {
		// create new visitors if system isn't occupied due to capacity
		if (allVisitorsInSpace.size() < Configuration.visitorCapacity) {
			if (Math.random() < 0.2) {
				Visitor newVisitor = new Visitor(space, grid, BehaviorFactory.getBehavior(), InterestFactory.getInterest());;
				this.addVisitorToContextAndSpace(newVisitor);
				allVisitorsInSpace.add(newVisitor);
				// System.out.println("[ENTER] Visitor " + newVisitor.id());
			}
		}
	}

	private void addVisitorToContextAndSpace(Visitor v) {
		Context<Object> context = ContextUtils.getContext(this);
		context.add(v);
		space.moveTo(v, (int) entry.getX(), (int) entry.getY());
		NdPoint pt = space.getLocation(v);
		grid.moveTo(v, (int) pt.getX(), (int) pt.getY());
	}

	private void unregisterLeftVisitors() {
		// unregister visitors that has just left
		Context<Object> context = ContextUtils.getContext(this);
		for (int i = 0; i < allVisitorsInSpace.size(); ++i) {
			Visitor v = allVisitorsInSpace.get(i);
			if (v.toRemoveFromSpace()) {
				context.remove(v);
				allVisitorsInSpace.remove(i);
				if (Configuration.printLeave)
					System.out.println("[LEAVE] Visitor " + v.id());
				if (v.id() == Configuration.traceVisitor) {
					v.getMobileDevice().getOwnVisits().printSojournTimesOfVisitors();  // print data of all visits of traceVisitor
				}
				// !!!! check if simulation should terminate:
				if (v.id() == Configuration.visitorTerminatingSimulation)
					this.simulationIsTerminated = true;
			}
		}
	}

	private void printTicks() {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (tick % Configuration.tick_steps == 0.0) {
			System.out.println("Tick: " + tick);
		}
	}

	private boolean ticksHasPassed(int steps) {
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount() % steps == 0.0;
	}

}
