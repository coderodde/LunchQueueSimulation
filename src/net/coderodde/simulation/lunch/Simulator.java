package net.coderodde.simulation.lunch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;

/**
 * This class runs the lunch queue simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class Simulator {

    private final int populationSize;
    
    private final double meanOfMeanLunchTime;
    private final double sdOfMeanLunchTime;
    private final double meanOfLunchTimeDeviation;
    private final double sdOfLunchTimeDeviation;
    private final double meanServiceTime;
    private final double sdOfServiceTime;
    
    private final Random random;
    private final ProbabilityDistribution<AcademicDegree> degreeDistribution;
    
    /**
     * Creates this simulator.
     * 
     * @param populationSize           the size of population being simulated.
     * @param meanOfMeanLunchTime      the mean of the mean lunch time.
     * @param meanOfLunchTimeDeviation the mean of the standard deviation of 
     *                                 lunch time.
     * @param sdOfMeanLunchTime        the standard deviation of mean lunch 
     *                                 time.
     * @param sdOfLunchTimeDeviation   the standard deviation of the standard
     *                                 deviation.
     * @param meanServiceTime          the average service time.
     * @param sdOfServiceTime          the standard deviation of service time.
     * @param random                   the random number generator.
     * @param degreeDistribution       the distribution of academic degrees in
     *                                 the population.
     */
    public Simulator(int populationSize,
                     double meanOfMeanLunchTime,
                     double sdOfMeanLunchTime,
                     double meanOfLunchTimeDeviation,
                     double sdOfLunchTimeDeviation,
                     double meanServiceTime,
                     double sdOfServiceTime,
                     Random random,
                     ProbabilityDistribution<AcademicDegree>
                             degreeDistribution) {
        checkMean(meanOfMeanLunchTime);
        checkMean(meanOfLunchTimeDeviation);
        checkMean(meanServiceTime);
        
        checkStandardDeviation(sdOfMeanLunchTime);
        checkStandardDeviation(sdOfLunchTimeDeviation);
        checkStandardDeviation(sdOfServiceTime);
        
        Objects.requireNonNull(random, "Random is null.");
        Objects.requireNonNull(degreeDistribution,
                              "The degree distribution is null.");
        
        this.populationSize = populationSize;
        
        this.meanOfMeanLunchTime = meanOfMeanLunchTime;
        this.sdOfMeanLunchTime   = sdOfMeanLunchTime;
        
        this.meanOfLunchTimeDeviation = meanOfLunchTimeDeviation;
        this.sdOfLunchTimeDeviation   = sdOfLunchTimeDeviation;
        
        this.meanServiceTime = meanServiceTime;
        this.sdOfServiceTime = sdOfServiceTime;
        
        this.random = random;
        this.degreeDistribution = degreeDistribution;
    }
    
    public void simulate() {
        PopulationGenerator populationGenerator = 
                new PopulationGenerator(random,
                                        degreeDistribution,
                                        meanOfMeanLunchTime,
                                        meanOfLunchTimeDeviation,
                                        sdOfMeanLunchTime,
                                        sdOfLunchTimeDeviation);
        Person[] population = 
                populationGenerator.createPopulation(populationSize);
        
        Map<Person, LunchTimePreferences> mapPersonToPreferences = 
                new HashMap<>();
        
        for (Person person : population) {
            mapPersonToPreferences.
                    put(person, 
                        populationGenerator.createRandomLunchTimePreferences());
        }
        
        
    }
    
    public static void main(final String... args) {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        ProbabilityDistribution<AcademicDegree> degreeDistribution = 
                new ProbabilityDistribution<>();
        
        degreeDistribution.add(AcademicDegree.DOCTOR,        30.0f);
        degreeDistribution.add(AcademicDegree.MASTER,        43.0f);
        degreeDistribution.add(AcademicDegree.BACHELOR,      65.0f);
        degreeDistribution.add(AcademicDegree.UNDERGRADUATE, 100.0f);
        
        // All time units represent seconds.
        // For example, mean service time is 15 seconds.
        Simulator simulator = 
                new Simulator(400,     // 400 persons going to lunch.
                              10800.0, // On average, people go to lunch after
                                       // 3 hours of work.
                              1200.0,  // The average lunch time standard
                                       // deviation: 20 minutes.
                              1800.0,  // The mean of lunch time deviation.
                              1000.0,  // The s.d. of lunch time deviation.
                              20.0,    // Mean service time.
                              5.0,     // The s.d of serice time.
                              random,
                              degreeDistribution);
    }
}
