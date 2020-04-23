package utils;

import java.util.Random;

public class GaussianRandomGenerator {
    private static final Random rand = new Random();

    public static double nextPositivDouble(int mean, double standardDeviation){
        double number;
        do {
            number = rand.nextGaussian() * standardDeviation + mean;
        } while (number < 0.0);
        return number;
    }

    
    public static int nextPositivInt(int mean, double standardDeviation){
        return (int) nextPositivDouble(mean, standardDeviation);
    }
}
