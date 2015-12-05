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
    
    public static MeanServiceTimeSelector withRandom(Random random) {
        Objects.requireNonNull(random, "The input Random is null.");
        Configuration configuration = new Configuration();
        configuration.random = random;
        return new MeanServiceTimeSelector(configuration);
    }
    
    public static MeanServiceTimeSelector withDefaultRandom() {
        return withRandom(new Random());
    }
    
    public final static class MeanServiceTimeSelector {
        
        private final Configuration configuration;
        
        private MeanServiceTimeSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        public StandardDeviationSelector 
            withMeanServiceTime(double meanServiceTime) {
            checkMean(meanServiceTime);
            configuration.meanServiceTime = meanServiceTime;
            return new StandardDeviationSelector(configuration);
        }
    }
    
    public final static class StandardDeviationSelector {
        
        private final Configuration configuration;
        
        private StandardDeviationSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        public Cashier withStandardDeviationOfServiceTime(
                double standardDeviationOfServiceTime) {
            checkStandardDeviation(standardDeviationOfServiceTime);
            return new Cashier(configuration.meanServiceTime,
                               standardDeviationOfServiceTime,
                               configuration.random);
        }
    }
    
    private Cashier(double meanServiceTime, 
                    double standardDeviationOfServiceTime,
                    Random random) {
        this.meanServiceTime = meanServiceTime;
        this.standardDeviationOfServiceTime = standardDeviationOfServiceTime;
        this.random = random;
    }
    
    public double getServiceTime() {
        return meanServiceTime + standardDeviationOfServiceTime * 
                                 random.nextGaussian();
    }
    
    private static final class Configuration {
        private Random random;
        private double meanServiceTime;
    }
}
