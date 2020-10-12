package configuration;

public class Configuration {
	public static int visitorTerminatingSimulation= 70;  // if this visitor leaves then the simulation terminates
	public static int visitorCapacity= 40;               // maximumNumer of visitors allowed in the space
	public static int exhibit_count = 25;				 // number of exhibits
	public static int traceVisitor = 5;  				 // for tracing a certain visitor
	public static int numberOfExhibits = 30;
	public static int gridHeight = 25;
	public static int gridWidth = 25;	
	public static double exitProbability = 0.01;		// probability that a visitors goes from exhibit to exit
	
	public static int tick_steps = 50;					// step size to make print time line in output
	public static int store_steps = 200;				// step size to store results in file (sojourn time)
	public static int ttl = 2;							// ttl of MANET
	
	
	// control output on console
	public static boolean followRecommendedVisits = true; 
	public static boolean followRandomVisits = true; 
	public static boolean printRecommendationResults = true;
	public static boolean printEnter = true;
	public static boolean printLeave = true;
	public static boolean printWhomImet = true;
	public static boolean printMatrix = false;
}
