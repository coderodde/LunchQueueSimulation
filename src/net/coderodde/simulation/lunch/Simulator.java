package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
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
    
    public SimulationResult simulate() {
        PopulationGenerator populationGenerator = 
                new PopulationGenerator(random,
                                        degreeDistribution,
                                        meanOfMeanLunchTime,
                                        meanOfLunchTimeDeviation,
                                        sdOfMeanLunchTime,
                                        sdOfLunchTimeDeviation);
        Person[] population = 
                populationGenerator.createPopulation(populationSize);
        
        LunchQueueEvent[] arrivalEventArray = 
                new LunchQueueEvent[populationSize];
        
        Map<Person, LunchQueueEvent> arrivalEventMap = new HashMap<>();
        Map<Person, LunchQueueEvent> servedEventMap = new HashMap<>();
        
        for (int i = 0; i < population.length; ++i) {
            LunchQueueEvent event = 
                    new LunchQueueEvent(population[i],
                                        populationGenerator
                                             .createRandomLunchTimePreferences()
                                             .sample(random));
            arrivalEventArray[i] = event;
            arrivalEventMap.put(event.getPerson(), event);
        }
        
        // Order the queue entry events by their arrival time stamps.
        Arrays.sort(arrivalEventArray);
        
        Queue<LunchQueueEvent> inputEventQueue = 
                new ArrayDeque<>(Arrays.asList(arrivalEventArray));
        
        PrioritizedQueue QUEUE = new PrioritizedQueue();
        double currentClock = inputEventQueue.peek().getTimestamp();
        
        while (!inputEventQueue.isEmpty()) {
            // Load all hungry people that arrived during the service of the 
            // previously served people.
            if (QUEUE.isEmpty()) {
                while (!inputEventQueue.isEmpty()
                        && inputEventQueue.peek().getTimestamp() 
                        <= currentClock) {
                    QUEUE.push(inputEventQueue.remove());
                }
            }
            
            // Admit an earliest + highest priority person to the cashier.
            LunchQueueEvent currentEvent = QUEUE.pop();
            // Serving...
            double serviceTime = meanServiceTime + 
                                 sdOfServiceTime * random.nextGaussian();
            currentClock += serviceTime;
            LunchQueueEvent servedEvent = 
                    new LunchQueueEvent(currentEvent.getPerson(), currentClock);
            servedEventMap.put(servedEvent.getPerson(), servedEvent);
            // Served!
        }
        
        // Start computing system statistics.
        SimulationResult result = new SimulationResult();
        Map<AcademicDegree, Double> mapMinimumWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapMaximumWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapAverageWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapWaitTimeSum     = new HashMap<>();
        Map<AcademicDegree, Double> mapWaitTimeDeviation = new HashMap<>();
        
        for (AcademicDegree degree : AcademicDegree.values()) {
            mapMinimumWaitTime.put(degree, Double.POSITIVE_INFINITY);
            mapMaximumWaitTime.put(degree, Double.NEGATIVE_INFINITY);
            mapWaitTimeSum.put(degree, 0.0);
        }
        
        for (Person person : population) {
            LunchQueueEvent arrivalEvent = arrivalEventMap.get(person);
            LunchQueueEvent servedEvent  = servedEventMap.get(person);
            double waitTime = servedEvent.getTimestamp() - 
                              arrivalEvent.getTimestamp();
            
            AcademicDegree degree = person.getAcademicDegree();
            
            if (mapMinimumWaitTime.get(degree) > waitTime) {
                mapMinimumWaitTime.put(degree, waitTime);
            }
            
            if (mapMaximumWaitTime.get(degree) < waitTime) {
                mapMaximumWaitTime.put(degree, waitTime);
            }
            
            mapWaitTimeSum.put(degree, mapWaitTimeSum.get(degree) + waitTime);
        }
        
        for (AcademicDegree degree : AcademicDegree.values()) {
            double average = mapWaitTimeSum.get(degree) / populationSize;
            mapAverageWaitTime.put(degree, average);
        }
        
        return result;
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
        System.out.println("Seed = " + seed);
        
        long startTime = System.nanoTime();
        SimulationResult result = simulator.simulate();
        long endTime = System.nanoTime();
        
        System.out.printf("Simulated in %.2f milliseconds.\n", 
                          (endTime - startTime) / 1e6);
        
        System.out.println(result);
    }
}
