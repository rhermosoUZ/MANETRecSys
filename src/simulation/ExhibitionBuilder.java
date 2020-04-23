package simulation;

import java.util.HashMap;
import java.util.Random;

import configuration.Configuration;
import profiles.ExhibitType;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;


public class ExhibitionBuilder implements ContextBuilder<Object> {
	private final String[] exhibitTypes = {ExhibitType.exhibit1.getType(), ExhibitType.exhibit2.getType(), 
			ExhibitType.exhibit3.getType(), ExhibitType.exhibit4.getType(), ExhibitType.exhibit5.getType(), ExhibitType.exhibit6.getType()};
	
	@Override
	public Context<Object> build(Context<Object> context) {
		
		int height = Configuration.gridHeight;
		int width = Configuration.gridWidth;
		
		context.setId("MANETsimulation");
		 
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new repast.simphony.space.continuous.StrictBorders(), width, height);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true, width, height));
		
		setUpExhibits(space, grid, context);
		
		Scheduler flowSource = new Scheduler(space, grid);
	    context.add(flowSource);
		
		return context;
	}
	
	private void setUpExhibits(ContinuousSpace<Object> space, Grid<Object> grid, Context<Object> context) {
		
		Random rand = new Random();
		int numberOfExhibits = Configuration.numberOfExhibits;
		int numberOfTypes = exhibitTypes.length;
		
		HashMap<Integer, Integer> ranges = getRanges(numberOfExhibits);
		
		for(int i = 0; i < numberOfExhibits; ++i) {
			String type = "";
			for(int j = 0; j < numberOfTypes; ++j) {
                if(i <= ranges.get(j)) {
                    type = exhibitTypes[j];
                    break;
                }
            }
			
			Exhibit newEx = new Exhibit(type);
			//System.out.println("######## " + newEx);
			context.add(newEx);
			
			space.moveTo(newEx, rand.nextInt(Configuration.gridHeight - 4)+2, rand.nextInt(Configuration.gridWidth -4)+2);
			NdPoint pt = space.getLocation(newEx);
			grid.moveTo(newEx, (int)pt.getX(), (int)pt.getY());
			
		}
	}
	
	private HashMap<Integer, Integer> getRanges(int numberOfExhibits) {
		HashMap<Integer, Integer> ranges = new HashMap<>();
		int range = numberOfExhibits/exhibitTypes.length; 
		
		for(int i = 0; i < exhibitTypes.length; ++i) {
			ranges.put(i, (i+1)*range);
		}
		
		return ranges;		
	}
}
