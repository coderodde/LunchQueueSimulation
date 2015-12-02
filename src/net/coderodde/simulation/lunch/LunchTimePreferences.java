package net.coderodde.simulation.lunch;

import java.util.Random;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;

/**
 * This class describes preferences of a person regarding the lunch time.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class LunchTimePreferences {
   
    private final double timeMean;
    private final double timeStandardDeviation;
    
    public LunchTimePreferences(double timeMean, double timeStandardDeviation) {
        checkMean(timeMean);
        checkStandardDeviation(timeStandardDeviation);
        
        this.timeMean = timeMean;
        this.timeStandardDeviation = timeStandardDeviation;
    }
    
    public double sample(Random random) {
        return timeMean + timeStandardDeviation * random.nextGaussian();
    }
}
