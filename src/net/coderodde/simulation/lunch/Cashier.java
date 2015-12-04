package net.coderodde.simulation.lunch;

import java.util.Objects;
import java.util.Random;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;

/**
 * This class models the action of a cashier.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 4, 2015)
 */
public class Cashier {
    
    private final double meanServiceTime;
    private final double standardDeviationOfServiceTime;
    private final Random random;
    
    public Cashier(double meanServiceTime, 
                   double standardDeviationOfServiceTime,
                   Random random) {
        checkMean(meanServiceTime);
        checkStandardDeviation(standardDeviationOfServiceTime);
        Objects.requireNonNull(random, "The input Random is null.");
        
        this.meanServiceTime = meanServiceTime;
        this.standardDeviationOfServiceTime = standardDeviationOfServiceTime;
        this.random = random;
    }
    
    public double getServiceTime() {
        return meanServiceTime + standardDeviationOfServiceTime * 
                                 random.nextGaussian();
    }
}
