package net.coderodde.simulation.lunch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

/**
 * This class runs the lunch queue simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class Simulator {

    //// Internals.
    private final Map<Person, LunchQueueEvent> arrivalEventMap = 
            new HashMap<>();
    private final Map<Person, LunchQueueEvent> servedEventMap = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> groupCounts = new HashMap<>();
    private final Map<AcademicDegree, Double> mapMinimumWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Double> mapMaximumWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Double> mapAverageWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Double> mapWaitTimeSum     = 
            new HashMap<>();
    private final Map<AcademicDegree, Double> mapWaitTimeDeviation = 
            new HashMap<>();
    
    private Queue<LunchQueueEvent> inputEventQueue;
    private Population population;
    
    private void preprocess(Population population) {
        arrivalEventMap.clear();
        servedEventMap.clear();
        mapMinimumWaitTime.clear();
        mapMaximumWaitTime.clear();
        mapAverageWaitTime.clear();
        mapWaitTimeSum.clear();
        mapWaitTimeDeviation.clear();
        
        this.population = population;
        inputEventQueue = population.toEventQueue();

        // groupCounts.keySet() will now list only those academic degrees that
        // are present in the population.
        for (LunchQueueEvent event : inputEventQueue) {
            Person person = event.getPerson();
            arrivalEventMap.put(person, event);
            AcademicDegree degree = person.getAcademicDegree();
            groupCounts.put(degree, groupCounts.getOrDefault(degree, 0) + 1);
        }
    }
    
    public SimulationResult simulate(Population population, Cashier cashier) {
        preprocess(population);
        
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
            
            // Serving...
            double serviceTime = cashier.getServiceTime();
            currentClock += serviceTime;
            LunchQueueEvent servedEvent = new LunchQueueEvent(currentPerson, 
                                                              currentClock);
            servedEventMap.put(currentPerson, servedEvent);
            personsPending--;
            // Served!
        }
        
        return postprocess();
    }
    
    private SimulationResult postprocess() {
        // Start computing system statistics.
        
        for (AcademicDegree degree : groupCounts.keySet()) {
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
        for (AcademicDegree degree : groupCounts.keySet()) {
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
        
        for (AcademicDegree degree : groupCounts.keySet()) {
            double sum = mapWaitTimeDeviation.get(degree);
            mapWaitTimeDeviation.put(degree, 
                                     Math.sqrt(sum / groupCounts.get(degree)));
        }
        
        SimulationResult result = new SimulationResult();
        
        for (AcademicDegree degree : groupCounts.keySet()) {
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
        Person person1 = new Person("Al", "Funky", AcademicDegree.UNDERGRADUATE);
        Person person2 = new Person("El", "Funky", AcademicDegree.UNDERGRADUATE);
        Person person3 = new Person("Ol", "Funky", AcademicDegree.UNDERGRADUATE);
        Cashier cashier = new Cashier(12.0, 0.0, new Random(1));
        Population population = new Population();
        
        population.addPerson(person1, 0.0);
        population.addPerson(person2, 0.0);
        population.addPerson(person3, 0.0);
        
        SimulationResult result1 =  new Simulator().simulate(population, cashier);
        String s = result1.toString();
        System.out.println(s);
        
        System.exit(0);
        
        long seed = System.nanoTime();
        Random random = new Random(seed);
        ProbabilityDistribution<AcademicDegree> degreeDistribution = 
                new ProbabilityDistribution<>();
        
        // Degree distribution:
        degreeDistribution.add(AcademicDegree.DOCTOR,        5.0f);
        degreeDistribution.add(AcademicDegree.MASTER,        10.0f);
        degreeDistribution.add(AcademicDegree.BACHELOR,      30.0f);
        degreeDistribution.add(AcademicDegree.UNDERGRADUATE, 100.0f);
        
        RandomPopulationGenerator populationGenerator = 
                new RandomPopulationGenerator(random,
                                        degreeDistribution,
                                        10800.0,
                                        1800.0);
        
        // Cashier serves in average in 15 seconds, s.d. 2 seconds.
        cashier = new Cashier(15.0, 2.0, random);
        System.out.println("Seed = " + seed);
        
        long startTime = System.nanoTime();
        // Simulate a population of 400 hungry people.
        SimulationResult result = new Simulator()
                .simulate(populationGenerator.generate(400), 
                          cashier);
        long endTime = System.nanoTime();
        
        System.out.printf("Simulated in %.2f milliseconds.\n", 
                          (endTime - startTime) / 1e6);
        
        System.out.println(result);
    }
}
