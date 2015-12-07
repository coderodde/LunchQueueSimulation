package net.coderodde.simulation.lunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * This class runs the lunch queue simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public final class Simulator {

    //// Internals.
    private final Map<Person, LunchQueueEvent> arrivalEventMap = 
            new HashMap<>();
    private final Map<Person, LunchQueueEvent> servedEventMap = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> groupCounts = new HashMap<>();
    
    private final Map<AcademicDegree, Integer> mapMinimumWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> mapMaximumWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> mapAverageWaitTime = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> mapWaitTimeSum     = 
            new HashMap<>();
    private final Map<AcademicDegree, Integer> mapWaitTimeDeviation = 
            new HashMap<>();
    
    private final List<Integer> cashierIdleIntervals = new ArrayList<>();
    private Population population;
    
    public static PopulationSelector simulate() {
        
        return new PopulationSelector();
    }
    
    public static final class PopulationSelector {
        
        public CashierSelector withPopulation(Population population) {
            Objects.requireNonNull(population, "The input population is null.");
            return new CashierSelector(population);
        }
    }
    
    public static final class CashierSelector {
        
        private final Population population;
        
        CashierSelector(Population population) {
            this.population = population;
        }
        
        public SimulationResult withCashier(Cashier cashier) {
            Objects.requireNonNull(cashier, "The input cashier is null.");
            return new Simulator().simulate(population, cashier);
        }
    }
    
    private SimulationResult simulate(Population population, Cashier cashier) {
        this.population = population;
        Queue<LunchQueueEvent> inputEventQueue = population.toEventQueue();
        preprocess(inputEventQueue);
        
        if (population.size() == 0) {
            return new SimulationResult(arrivalEventMap, servedEventMap);
        }
        
        PrioritizedQueue QUEUE = new PrioritizedQueue();
        int currentClock = inputEventQueue.peek().getTimestamp();
        
        for (int personsPending = population.size();
                personsPending > 0;
                personsPending--) {
            // Load all hungry people that arrived during the service of the 
            // previously served person.
            while (!inputEventQueue.isEmpty()
                    && inputEventQueue.peek().getTimestamp() 
                    <= currentClock) {
                QUEUE.push(inputEventQueue.remove());
            }
            
            if (QUEUE.isEmpty()) {
                LunchQueueEvent headEvent = inputEventQueue.remove();
                cashierIdleIntervals.add(headEvent.getTimestamp() - 
                                         currentClock);
                currentClock = headEvent.getTimestamp();
                QUEUE.push(headEvent);
            } else {
                cashierIdleIntervals.add(0);
            }
            
            // Admit an earliest + highest priority person to the cashier.
            LunchQueueEvent currentEvent = QUEUE.pop();
            Person currentPerson = currentEvent.getPerson();
            
            // Serving...
            int serviceTime = cashier.getServiceTime();
            currentClock += serviceTime;
            LunchQueueEvent servedEvent = new LunchQueueEvent(currentPerson, 
                                                              currentClock);
            servedEventMap.put(currentPerson, servedEvent);
            // Served!
        }
        
        return postprocess();
    }
    
    private void preprocess(Queue<LunchQueueEvent> inputEventQueue) {
        // groupCounts.keySet() will now list only those academic degrees that
        // are present in the population.
        for (LunchQueueEvent event : inputEventQueue) {
            Person person = event.getPerson();
            arrivalEventMap.put(person, event);
            AcademicDegree degree = person.getAcademicDegree();
            groupCounts.put(degree, groupCounts.getOrDefault(degree, 0) + 1);
        }
    }
    
    private SimulationResult postprocess() {
        // Start computing system statistics.
        
        for (AcademicDegree degree : groupCounts.keySet()) {
            mapMinimumWaitTime.put(degree, Integer.MAX_VALUE);
            mapMaximumWaitTime.put(degree, Integer.MIN_VALUE);
            mapWaitTimeSum.put(degree, 0);
        }
        
        // Computing minimum/maximum wait time for each academic degree.
        for (Person person : population.getPersonSet()) {
            LunchQueueEvent arrivalEvent = arrivalEventMap.get(person);
            LunchQueueEvent servedEvent  = servedEventMap.get(person);
            
            int waitTime = servedEvent.getTimestamp() - 
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
            int average = (int) Math.round(1.0 * mapWaitTimeSum.get(degree) / 
                                           groupCounts.get(degree));
            
            mapAverageWaitTime.put(degree, average);
            mapWaitTimeDeviation.put(degree, 0);
        }
        
        for (Person person : population.getPersonSet()) {
            AcademicDegree degree = person.getAcademicDegree();
            
            int duration = servedEventMap.get(person).getTimestamp() -
                          arrivalEventMap.get(person).getTimestamp();
            
            int contribution = duration - mapAverageWaitTime.get(degree);
            
            contribution *= contribution;
            mapWaitTimeDeviation.put(degree, 
                                     mapWaitTimeDeviation.get(degree) +
                                             contribution);
        }
        
        for (AcademicDegree degree : groupCounts.keySet()) {
            int sum = mapWaitTimeDeviation.get(degree);
            mapWaitTimeDeviation.put(degree, 
                                     (int) Math.round(
                                             Math.sqrt(sum / 
                                                       groupCounts
                                                               .get(degree))));
        }
        
        SimulationResult result = new SimulationResult(arrivalEventMap, 
                                                       servedEventMap);
        
        for (AcademicDegree degree : groupCounts.keySet()) {
            result.putWaitMinimumTime(degree, mapMinimumWaitTime.get(degree));
            result.putWaitMaximumTime(degree, mapMaximumWaitTime.get(degree));
            result.putAverageWaitTime(degree, mapAverageWaitTime.get(degree));
            result.putWaitTimeStandardDeviation(degree,
                                                mapWaitTimeDeviation
                                                        .get(degree));
        }
        
        // Process cashier idle time statistics:
        if (cashierIdleIntervals.isEmpty()) {
            return result;
        }
        
        int sum = 0;
        int min = cashierIdleIntervals.get(0);
        int max = cashierIdleIntervals.get(0);
        
        for (int value : cashierIdleIntervals) {
            sum += value;
            
            if (min > value) {
                min = value;
            } else if (max < value) {
                max = value;
            }
        }
        
        double average = 1.0 * sum / cashierIdleIntervals.size();
        
        sum = 0;
        
        // Compute standard deviation:
        for (int value : cashierIdleIntervals) {
            double diff = average - value;
            diff *= diff;
            sum += diff;
        }
        
        int standardDeviation = 
                (int)(Math.round(
                        Math.sqrt(1.0 *sum / cashierIdleIntervals.size())));
        
        result.putCashierMinimumIdleTime(min);
        result.putCashierAverageIdleTime((int)(Math.round(average)));
        result.putCashierMaximumIdleTime(max);
        result.putCashierStandardDeviation(standardDeviation);
        
        return result;
    }
}
