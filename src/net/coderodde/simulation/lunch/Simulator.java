package net.coderodde.simulation.lunch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;

/**
 * This class runs the lunch queue simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class Simulator {

    private final double meanServiceTime;
    private final double standardDeviationOfServiceTime;
    
    private final Random random;
    
    public Simulator(double meanServiceTime,
                     double standardDeviationOfServiceTime,
                     Random random) {
        checkMean(meanServiceTime);
        checkStandardDeviation(standardDeviationOfServiceTime);
        Objects.requireNonNull(random, "Random is null.");
        
        this.meanServiceTime = meanServiceTime;
        this.standardDeviationOfServiceTime = standardDeviationOfServiceTime;
        this.random = random;
    }
    
    public SimulationResult simulate(Population population) {
        Queue<LunchQueueEvent> inputEventQueue = population.toEventQueue();
        Map<Person, LunchQueueEvent> arrivalEventMap = new HashMap<>();
        Map<Person, LunchQueueEvent> servedEventMap = new HashMap<>();
        Map<AcademicDegree, Integer> groupCounts = new HashMap<>();
        
        for (LunchQueueEvent event : inputEventQueue) {
            Person person = event.getPerson();
            arrivalEventMap.put(person, event);
            AcademicDegree degree = person.getAcademicDegree();
            groupCounts.put(degree, groupCounts.getOrDefault(degree, 0) + 1);
        }

        Set<AcademicDegree> presentDegrees = new HashSet<>();
        PrioritizedQueue QUEUE = new PrioritizedQueue();
        double currentClock = inputEventQueue.peek().getTimestamp();
        int personsPending = population.size();
        
        while (personsPending > 0) {
            // Load all hungry people that arrived during the service of the 
            // previously served person.
            while (!inputEventQueue.isEmpty()
                    && inputEventQueue.peek().getTimestamp() 
                    <= currentClock) {
                QUEUE.push(inputEventQueue.remove());
            }
            
            if (QUEUE.isEmpty()) {
                LunchQueueEvent headEvent = inputEventQueue.remove();
                QUEUE.push(headEvent);
                currentClock = headEvent.getTimestamp();
            }
            
            // Admit an earliest + highest priority person to the cashier.
            LunchQueueEvent currentEvent = QUEUE.pop();
            Person currentPerson = currentEvent.getPerson();
            presentDegrees.add(currentPerson.getAcademicDegree());
            
            // Serving...
            double serviceTime = meanServiceTime + 
                                 standardDeviationOfServiceTime * 
                                    random.nextGaussian();
            currentClock += serviceTime;
            LunchQueueEvent servedEvent = new LunchQueueEvent(currentPerson, 
                                                              currentClock);
            servedEventMap.put(currentPerson, servedEvent);
            personsPending--;
            // Served!
        }
        
        // Start computing system statistics.
        Map<AcademicDegree, Double> mapMinimumWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapMaximumWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapAverageWaitTime = new HashMap<>();
        Map<AcademicDegree, Double> mapWaitTimeSum     = new HashMap<>();
        Map<AcademicDegree, Double> mapWaitTimeDeviation = new HashMap<>();
        
        for (AcademicDegree degree : presentDegrees) {
            mapMinimumWaitTime.put(degree, Double.POSITIVE_INFINITY);
            mapMaximumWaitTime.put(degree, Double.NEGATIVE_INFINITY);
            mapWaitTimeSum.put(degree, 0.0);
        }
        
        // Computing minimum/maximum wait time for each academic degree.
        for (Person person : population.getPersonSet()) {
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
        for (AcademicDegree degree : presentDegrees) {
            double average = mapWaitTimeSum.get(degree) / 
                             groupCounts.get(degree);
            mapAverageWaitTime.put(degree, average);
            mapWaitTimeDeviation.put(degree, 0.0);
        }
        
        for (Person person : population.getPersonSet()) {
            AcademicDegree degree = person.getAcademicDegree();
            
            double duration = servedEventMap.get(person).getTimestamp() -
                              arrivalEventMap.get(person).getTimestamp();
            
            double contribution = duration - mapAverageWaitTime.get(degree);
            
            contribution *= contribution;
            mapWaitTimeDeviation.put(degree, 
                                     mapWaitTimeDeviation.get(degree) +
                                             contribution);
        }
        
        for (AcademicDegree degree : presentDegrees) {
            double sum = mapWaitTimeDeviation.get(degree);
            mapWaitTimeDeviation.put(degree, 
                                     Math.sqrt(sum / groupCounts.get(degree)));
        }
        
        SimulationResult result = new SimulationResult();
        
        for (AcademicDegree degree : presentDegrees) {
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
//        Person person1 = new Person("Al", "Funky", AcademicDegree.UNDERGRADUATE);
//        Person person2 = new Person("El", "Funky", AcademicDegree.UNDERGRADUATE);
//        Person person3 = new Person("Ol", "Funky", AcademicDegree.UNDERGRADUATE);
//        
//        Population population = new Population();
//        
//        population.addPerson(person1, 0.0);
//        population.addPerson(person2, 0.0);
//        population.addPerson(person3, 0.0);
//        
//        SimulationResult result1 =  new Simulator(15.0, 2.0, new Random(0)).simulate(population);
//        String s = result1.toString();
//        System.out.println(s);
//        
//        System.exit(0);
        
        long seed = System.nanoTime();
        Random random = new Random(seed);
        ProbabilityDistribution<AcademicDegree> degreeDistribution = 
                new ProbabilityDistribution<>();
        
        degreeDistribution.add(AcademicDegree.DOCTOR,        5.0f);
        degreeDistribution.add(AcademicDegree.MASTER,        10.0f);
        degreeDistribution.add(AcademicDegree.BACHELOR,      30.0f);
        degreeDistribution.add(AcademicDegree.UNDERGRADUATE, 100.0f);
        
        RandomPopulationGenerator populationGenerator = 
                new RandomPopulationGenerator(random,
                                        degreeDistribution,
                                        10800.0,
                                        1800.0);
        
        // All time units represent seconds.
        // For example, mean service time is 15 seconds.
        Simulator simulator = 
                new Simulator(15.0, // The mean service time is 20 seconds.
                              2.0,  // The standard deviaition of the service
                                    // time is 5 seconds.
                              random);
        
        System.out.println("Seed = " + seed);
        
        long startTime = System.nanoTime();
        // Simulate a population of 400 hungry people.
        SimulationResult result = 
                simulator.simulate(populationGenerator.generate(400));
        long endTime = System.nanoTime();
        
        System.out.printf("Simulated in %.2f milliseconds.\n", 
                          (endTime - startTime) / 1e6);
        
        System.out.println(result);
    }
}
