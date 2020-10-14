package configuration;

public class Configuration {
	public static int gridWidth = 200;	
	public static int gridHeight = 200;
	public static int numberOfExhibits = 30;
	public static int visitorTerminatingSimulation= 40;  // if this visitor leaves then the simulation terminates
	public static int visitorCapacity= 10;               // maximumNumer of visitors allowed in the space
	public static double exitProbability = 0.07;		// probability that a visitors goes from exhibit to exit
	public static int ttl = 2;							// ttl of MANET
	
	// control output on console
	public static int tick_steps = 50;					// step size to make print time line in output
	public static int store_steps = 200;				// step size to store results in file (sojourn time)

	
	public static int traceVisitor = 20;  				 // for tracing a certain visitor
	public static boolean followRecommendedVisits = true; 
	public static boolean followRandomVisits = true; 
	public static boolean printRecommendationResults = true;
	public static boolean printEnter = false;
	public static boolean printLeave = false;
	public static boolean printWhomImet = true;
	public static boolean printMatrix = false;
	public static boolean showState = true; 
}
