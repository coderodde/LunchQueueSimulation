package net.coderodde.simulation.lunch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds the statistics of a simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public final class SimulationResult {

    private static final String NL = "\n";
    private static final String SKIP = "    ";
    private static final int NO_DATA = -1;
    
    private final Map<AcademicDegree, Integer> waitAverageMap = new HashMap<>();
    private final Map<AcademicDegree, Integer> waitStandardDeviationMap = 
            new HashMap<>();
    
    private final Map<AcademicDegree, Integer> waitMinMap = new HashMap<>();
    private final Map<AcademicDegree, Integer> waitMaxMap = new HashMap<>();
    
    private final Map<Person, LunchQueueEvent> arrivalEventMap;
    private final Map<Person, LunchQueueEvent> servedEventMap;
    
    private int cashierMinimumIdleTime = NO_DATA;
    private int cashierAverageIdleTime = NO_DATA;
    private int cashierMaximumIdleTime = NO_DATA;
    private int cashierStandardDeviation = NO_DATA;
    
    public int getMinimumWaitTime(AcademicDegree degree) {
        return waitMinMap.getOrDefault(degree, NO_DATA);
    }
    
    public int getWaitAverage(AcademicDegree degree) {
        return waitAverageMap.getOrDefault(degree, NO_DATA);
    }
    
    public int getMaximumWaitTime(AcademicDegree degree) {
        return waitMaxMap.getOrDefault(degree, NO_DATA);
    }
    
    public int getWaitStandardDeviation(AcademicDegree degree) {
        return waitStandardDeviationMap.getOrDefault(degree, NO_DATA);
    }
    
    public int getCashierMinimumIdleTime() {
        return cashierMinimumIdleTime;
    }
    
    public int getCashierAverageIdleTime() {
        return cashierAverageIdleTime;
    }
    
    public int getCashierMaximumIdleTime() {
        return cashierMaximumIdleTime;
    }
    
    public int getCashierStandardDeviation() {
        return cashierStandardDeviation;
    }
    
    SimulationResult(Map<Person, LunchQueueEvent> arrivalEventMap,
                     Map<Person, LunchQueueEvent> servedEventMap) {
        this.arrivalEventMap = arrivalEventMap;
        this.servedEventMap = servedEventMap;
    }
    
    void putWaitMinimumTime(AcademicDegree degree, int minimumWaitTime) {
        waitMinMap.put(degree, minimumWaitTime);
    }
    
    void putAverageWaitTime(AcademicDegree degree, int averageTime) {
        waitAverageMap.put(degree, averageTime);
    }
    
    void putWaitMaximumTime(AcademicDegree degree, int maximumWaitTime) {
        waitMaxMap.put(degree, maximumWaitTime);
    }
    
    void putWaitTimeStandardDeviation(AcademicDegree degree, 
                                      int timeStandardDeviation) {
        waitStandardDeviationMap.put(degree, timeStandardDeviation);
    }
    
    void putCashierMinimumIdleTime(int cashierMinimumIdleTime) {
        this.cashierMinimumIdleTime = cashierMinimumIdleTime;
    }
    
    void putCashierAverageIdleTime(int cashierAverageIdleTime) {
        this.cashierAverageIdleTime = cashierAverageIdleTime;
    }
    
    void putCashierMaximumIdleTime(int cashierMaximumIdleTime) {
        this.cashierMaximumIdleTime = cashierMaximumIdleTime;
    }
    
    void putCashierStandardDeviation(int cashierStandardDeviation) {
        this.cashierStandardDeviation = cashierStandardDeviation;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Person> personList = new ArrayList<>(arrivalEventMap.keySet());
        
        Collections.<Person>sort(personList, 
                                (p1, p2) -> {
            double arrivalTime1 = arrivalEventMap.get(p1).getTimestamp();
            double servedTime1 = servedEventMap.get(p1).getTimestamp();

            double arrivalTime2 = arrivalEventMap.get(p2).getTimestamp();
            double servedTime2 = servedEventMap.get(p2).getTimestamp();
            
            return Double.compare(servedTime1 - arrivalTime1, 
                                  servedTime2 - arrivalTime2);
        });
        
        for (Person person : personList) {
            sb.append(person.toString())
              .append(", wait time: ")
              .append((int)(servedEventMap.get(person).getTimestamp() -
                            arrivalEventMap.get(person).getTimestamp()))
              .append(" seconds.")
              .append(NL);
        }
        
        toString(sb, AcademicDegree.DOCTOR);
        toString(sb, AcademicDegree.MASTER);
        toString(sb, AcademicDegree.BACHELOR);
        toString(sb, AcademicDegree.UNDERGRADUATE);
        
        sb.append("Cashier:")
          .append(NL)
          .append(SKIP)
          .append("Minimum idle time:  ")
          .append(getCashierMinimumIdleTime())
          .append(" seconds.")
          .append(NL)
          .append(SKIP)
          .append("Average idle time:  ")
          .append(getCashierAverageIdleTime())
          .append(" seconds.")
          .append(NL)
          .append(SKIP)
          .append("Maximum idle time:  ")
          .append(getCashierMaximumIdleTime())
          .append(" seconds.")
          .append(NL)
          .append(SKIP)
          .append("Standard deviation: ")
          .append(getCashierStandardDeviation())
          .append(" seconds.");
        
        return sb.toString();
    }
    
    private void toString(StringBuilder sb, AcademicDegree degree) {
        sb.append(degree.toString()).append(":").append(NL);
        
        sb.append(SKIP)
          .append("Minimum wait time:  ")
          .append(getMinimumWaitTime(degree))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Average wait time:  ")
          .append(getWaitAverage(degree))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Maximum wait time:  ")
          .append(getMaximumWaitTime(degree))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Standard deviation: ")
          .append(getWaitStandardDeviation(degree))
          .append(" seconds.")
          .append(NL);
    }
}
