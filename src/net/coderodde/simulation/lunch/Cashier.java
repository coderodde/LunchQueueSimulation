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
public final class Cashier {
    
    private final double meanServiceTime;
    private final double standardDeviationOfServiceTime;
    private final Random random;
    
    /**
     * Initiates a strong fluent API for creating a {@code Cashier}.
     * 
     * @param  random the random number generator to use.
     * @return the mean service time selector.
     */
    public static MeanServiceTimeSelector withRandom(Random random) {
        Objects.requireNonNull(random, "The input Random is null.");
        Configuration configuration = new Configuration();
        configuration.random = random;
        return new MeanServiceTimeSelector(configuration);
    }
    
    /**
     * Initiates a strong fluent API for creating a {@code Cashier} using a 
     * default random number generator.
     * 
     * @return the mean service time selector. 
     */
    public static MeanServiceTimeSelector withDefaultRandom() {
        return withRandom(new Random());
    }
    
    public final static class MeanServiceTimeSelector {
        
        private final Configuration configuration;
        
        private MeanServiceTimeSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        /**
         * Selects the mean service time and returns a standard deviation 
         * selector.
         * 
         * @param  meanServiceTime the mean service time in seconds.
         * @return a standard deviation selector.
         */
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
        
        /**
         * Selects a standard deviation for the service time and returns the 
         * {@code Cashier} using the gathered parameters.
         * 
         * @param  standardDeviationOfServiceTime the standard deviation of the
         *                                        service time in seconds.
         * @return a {@code Cashier} object.
         */
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
    
    public int getServiceTime() {
        return (int)(Math.round(meanServiceTime + 
                                    standardDeviationOfServiceTime * 
                                    random.nextGaussian()));
    }
    
    private static final class Configuration {
        private Random random;
        private double meanServiceTime;
    }
}
