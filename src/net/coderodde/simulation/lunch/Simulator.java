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
    
    private final double meanLunchTime;
    private final double meanServiceTime;
    private final double standardDeviationOfLunchTime;
    private final double standardDeviationOfServiceTime;
    
    private final Random random;
    private final ProbabilityDistribution<AcademicDegree> degreeDistribution;
    
    public Simulator(int populationSize,
                     double meanLunchTime,
                     double meanServiceTime,
                     double standardDeviationOfLunchTime,
                     double standardDeviationOfServiceTime,
                     Random random,
                     ProbabilityDistribution<AcademicDegree>
                             degreeDistribution) {
        checkMean(meanLunchTime);
        checkMean(meanServiceTime);
        
        checkStandardDeviation(standardDeviationOfLunchTime);
        checkStandardDeviation(standardDeviationOfServiceTime);
        
        Objects.requireNonNull(random, "Random is null.");
        Objects.requireNonNull(degreeDistribution,
                              "The degree distribution is null.");
        
        this.populationSize = populationSize;
        
        this.meanLunchTime = meanLunchTime;
        this.meanServiceTime = meanServiceTime;
        
        this.standardDeviationOfLunchTime = standardDeviationOfLunchTime;
        this.standardDeviationOfServiceTime = standardDeviationOfServiceTime;
        
        this.random = random;
        this.degreeDistribution = degreeDistribution;
    }
    
    public SimulationResult simulate() {
        PopulationGenerator populationGenerator = 
                new PopulationGenerator(random,
                                        degreeDistribution,
                                        meanLunchTime,
                                        standardDeviationOfLunchTime);
        Person[] population = 
                populationGenerator.createPopulation(populationSize);
        
        LunchQueueEvent[] arrivalEventArray = 
                new LunchQueueEvent[populationSize];
        
        Map<Person, LunchQueueEvent> arrivalEventMap = new HashMap<>();
        Map<Person, LunchQueueEvent> servedEventMap = new HashMap<>();
        Map<AcademicDegree, Integer> groupCounts = new HashMap<>();
        
        for (Person person : population) {
            AcademicDegree degree = person.getAcademicDegree();
            groupCounts.put(degree, groupCounts.getOrDefault(degree, 0) + 1);
        }
        
        for (int i = 0; i < population.length; ++i) {
            LunchQueueEvent event = 
                    new LunchQueueEvent(population[i],
                                        populationGenerator.
                                                getRandomLunchTime());
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
            // previously served person.
            while (!inputEventQueue.isEmpty()
                    && inputEventQueue.peek().getTimestamp() 
                    <= currentClock) {
                QUEUE.push(inputEventQueue.remove());
            }
            
            // Admit an earliest + highest priority person to the cashier.
            LunchQueueEvent currentEvent = QUEUE.pop();
            // Serving...
            double serviceTime = meanServiceTime + 
                                 standardDeviationOfServiceTime * 
                                    random.nextGaussian();
            
            currentClock += serviceTime;
            LunchQueueEvent servedEvent = 
                    new LunchQueueEvent(currentEvent.getPerson(), currentClock);
            servedEventMap.put(servedEvent.getPerson(), servedEvent);
            // Served!
        }
        
        // Start computing system statistics.
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
        
        // Computing minimum/maximum wait time for each academic degree.
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
        
        // Computing the average waiting time for each academic degree.
        for (AcademicDegree degree : AcademicDegree.values()) {
            double average = mapWaitTimeSum.get(degree) / 
                             groupCounts.get(degree);
            mapAverageWaitTime.put(degree, average);
        }
        
        for (Person person : population) {
            AcademicDegree degree = person.getAcademicDegree();
            double duration = servedEventMap.get(person).getTimestamp() -
                              arrivalEventMap.get(person).getTimestamp();
            double contribution = duration - mapAverageWaitTime.get(degree);
            contribution *= contribution;
            mapWaitTimeDeviation.put(degree, 
                                     contribution + 
                                     mapWaitTimeDeviation.getOrDefault(degree, 
                                                                       0.0));
        }
        
        for (AcademicDegree degree : AcademicDegree.values()) {
            double sum = mapWaitTimeDeviation.get(degree);
            mapWaitTimeDeviation.put(degree, 
                                     Math.sqrt(sum / groupCounts.get(degree)));
        }
        
        SimulationResult result = new SimulationResult();
        
        for (AcademicDegree degree : AcademicDegree.values()) {
            result.putWaitMinimumTime(degree, mapMinimumWaitTime.get(degree));
            result.putWaitMaximumTime(degree, mapMaximumWaitTime.get(degree));
            result.putAverageWaitTime(degree, mapAverageWaitTime.get(degree));
            result.putWaitTimeStandardDeviation(degree,
                                                mapWaitTimeDeviation
                                                        .get(degree));
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
                new Simulator(400,     // Population size: 400 persons.
                              10800.0, // On average people go to lunch after
                                       // 3 hours of work/studying.
                              20.0,    // The mean service time.
                              1200.0,  // The standard deviation of the lunch 
                                       // time: 20 minutes.
                              5.0,     // The standard deviaition of the service
                                       // time.
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
