package net.coderodde.simulation.lunch;

/**
 * This class contains miscellaneous utilities.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public final class Utils {
   
    public static void checkMean(double mean) {
        if (Double.isNaN(mean)) {
            throw new IllegalArgumentException(
                    "The mean is NaN (not-a-number):");
        }
        
        if (Double.isInfinite(mean)) {
            throw new IllegalArgumentException("The mean is infinite: " + mean);
        }
    }
    
    public static void checkStandardDeviation(double deviation) {
        if (Double.isNaN(deviation)) {
            throw new IllegalArgumentException(
                    "The standard deviation is NaN (not-a-number):");
        }
        
        if (Double.isInfinite(deviation)) {
            throw new IllegalArgumentException(
                    "The standard deviation is infinite: " + deviation);
        }
        
        if (deviation < 0.0) {
            throw new IllegalArgumentException(
                    "The standard deviation is negative: " + deviation);
        }
    }
    
    public static void checkTime(double time) {
        if (Double.isNaN(time)) {
            throw new IllegalArgumentException(
                    "The input time is NaN (not-a-number).");
        }
        
        if (Double.isInfinite(time)) {
            throw new IllegalArgumentException(
                    "The input time is infinite: " + time);
        }
    }
}
