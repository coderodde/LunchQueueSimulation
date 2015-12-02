package net.coderodde.simulation.lunch;

import java.util.List;
import java.util.Random;

/**
 * This class contains miscellaneous utilities.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class Utils {
   
    public static <T> T choose(List<T> list, Random random) {
        if (list.isEmpty()) {
            return null;
        }
        
        return list.get(random.nextInt(list.size()));
    }
    
    public static <T> T choose(T[] array, Random random) {
        if (array.length == 0) {
            return null;
        }
        
        return array[random.nextInt(array.length)];
    }
    
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
}
