package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import repast.simphony.engine.environment.RunEnvironment;
import simulation.Visitor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import configuration.Configuration;;

public class CSVWriter {
	private static final String directory = "./_results/results";

	public static void saveGlobalSojournTimeMatrix(double[][] matrix) {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String path = directory + "/global_sojourn_times/global_sojourn_time_matrix" + tick + ".csv";
		save(matrix, path);
	}

	public static void saveCollectedRatingsOfVisitor(Visitor visitor) {
		double[][] matrix = visitor.getMobileDevice().getSojournTimeMatrix();
		
		if (visitor.id() == Configuration.traceVisitor && Configuration.printMatrix) 
			sysoutMatrix(matrix);
		
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		String path = directory + "/collected_ratings_of_visitor/" + "collected_ratings_of_" + visitor.toString() + "_"
				+ tick + ".csv";
		save(matrix, path);
	}

	public static void save(double[][] matrix, String filename) {
		CSVFormat format = CSVFormat.RFC4180.withDelimiter(';');
		try {
			try (FileWriter writer = new FileWriter(filename)) {
				try (CSVPrinter printer = new CSVPrinter(writer, format)) {
					List<String> header = new ArrayList<>();
					header.add("User ID");
					for (int column = 1; column <= matrix[0].length; ++column) {
						header.add("Item " + column);
					}
					printer.printRecord(header);
					int userId = 1;
					for (int row = 0; row < matrix.length; ++row) {
						List<String> empData = new ArrayList<>();
						empData.add(Integer.toString(userId));
						for (int column = 0; column < matrix[0].length; ++column) {
							empData.add(Double.toString(matrix[row][column]).replace('.', ','));
						}
						printer.printRecord(empData);
						userId++;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void saveEvaluatedRecommendations(Visitor v) {
		int uid = v.id();
		Map<Integer, EvaluationResult> results = v.getMobileDevice().getEvaluationResults();
		CSVFormat format = CSVFormat.RFC4180.withDelimiter(';');
		String filename = directory + "/evaluation_of_recommendations/evaluated_recommendations_of_visitor_" + uid
				+ ".csv";
		try {
			try (FileWriter writer = new FileWriter(filename)) {
				try (CSVPrinter printer = new CSVPrinter(writer, format)) {
					List<String> header = new ArrayList<>();
					header.add("UserID");
					header.add("ItemID");
					header.add("sojourn_time");
					header.add("local_prediction");
					header.add("global_prediction");
					header.add("random_time");
					header.add("local error");
					header.add("global error");
					header.add("random error");
					header.add("tick");
					header.add("timestamp");

					printer.printRecord(header);

					Collection<EvaluationResult> values = results.values();

					List<EvaluationResult> sortedResults = values.stream().sorted(
							(e1, e2) -> e1.getRecommendationTimestamp().compareTo(e2.getRecommendationTimestamp()))
							.collect(Collectors.toList());

					for (EvaluationResult er : sortedResults) {
						List<String> data = new ArrayList<>();
						data.add(Integer.toString(uid));
						data.add(Integer.toString(er.getItemID()));
						data.add(Integer.toString(er.getMeasuredSojournTime()));
						data.add(Integer.toString(er.getLocalPrediction()));
						data.add(Integer.toString(er.getGlobalPrediction()));
						data.add(Integer.toString(er.getRandomPrediction()));
						data.add(Integer.toString(Math.abs(er.getError())));
						data.add(Integer.toString(Math.abs(er.getGlobalPrediction() - er.getMeasuredSojournTime())));
						data.add(Integer.toString(Math.abs(er.getRandomPrediction() - er.getMeasuredSojournTime())));
						data.add(Double.toString(er.getTick()));
						data.add(er.getRecommendationTimestamp().toString());

						if (er.getUserID() == Visitor.traceVisitor && Configuration.followRecommendedVisits)
							System.out.println(er);
						
						printer.printRecord(data);
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void sysoutMatrix(double[][] matrix) {
		double tick = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		System.out.println("!!!!!!!!!!!!" + "!!!!!!!!!!!!!!!! Ticks:" + tick);
		for (int i = 0; i < matrix.length; ++i) {
			String row = "" + i;
			for (int j = 0; j < matrix[0].length; ++j) {
				row = row + " " + matrix[i][j];
			}
			System.out.println(row);
		}
	}
}
