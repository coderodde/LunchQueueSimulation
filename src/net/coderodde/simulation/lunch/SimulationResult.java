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
public class SimulationResult {

    private static final String NL = "\n";
    private static final String SKIP = "    ";
    
    private final Map<AcademicDegree, Double> waitAverageMap = new HashMap<>();
    private final Map<AcademicDegree, Double> waitStandardDeviationMap = 
            new HashMap<>();
    private final Map<AcademicDegree, Double> waitMinMap = new HashMap<>();
    private final Map<AcademicDegree, Double> waitMaxMap = new HashMap<>();
    
    private final Map<Person, LunchQueueEvent> arrivalEventMap;
    private final Map<Person, LunchQueueEvent> servedEventMap;
    
    public double getWaitAverage(AcademicDegree degree) {
        return waitAverageMap.getOrDefault(degree, Double.NaN);
    }
    
    public double getWaitStandardDeviation(AcademicDegree degree) {
        return waitStandardDeviationMap.getOrDefault(degree, Double.NaN);
    }
    
    public double getMinimumWaitTime(AcademicDegree degree) {
        return waitMinMap.getOrDefault(degree, Double.NaN);
    }
    
    public double getMaximumWaitTime(AcademicDegree degree) {
        return waitMaxMap.getOrDefault(degree, Double.NaN);
    }
    
    SimulationResult(Map<Person, LunchQueueEvent> arrivalEventMap,
                     Map<Person, LunchQueueEvent> servedEventMap) {
        this.arrivalEventMap = arrivalEventMap;
        this.servedEventMap = servedEventMap;
    }
    
    void putAverageWaitTime(AcademicDegree degree, double averageTime) {
        waitAverageMap.put(degree, averageTime);
    }
    
    void putWaitTimeStandardDeviation(AcademicDegree degree, 
                                      double timeStandardDeviation) {
        waitStandardDeviationMap.put(degree, timeStandardDeviation);
    }
    
    void putWaitMinimumTime(AcademicDegree degree, double minimumWaitTime) {
        waitMinMap.put(degree, minimumWaitTime);
    }
    
    void putWaitMaximumTime(AcademicDegree degree, double maximumWaitTime) {
        waitMaxMap.put(degree, maximumWaitTime);
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
        // Cut off the last character which is a new line character.
        return sb.substring(0, sb.length() - 1);
    }
    
    private void toString(StringBuilder sb, AcademicDegree degree) {
        sb.append(degree.toString()).append(":").append(NL);
        
        sb.append(SKIP)
          .append("Average wait time:            ")
          .append(String.format("%.0f", getWaitAverage(degree)))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Wait time standard deviation: ")
          .append(String.format("%.0f", getWaitStandardDeviation(degree)))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Minimum wait time:            ")
          .append(String.format("%.0f", getMinimumWaitTime(degree)))
          .append(" seconds.")
          .append(NL);
        
        sb.append(SKIP)
          .append("Maximum wait time:            ")
          .append(String.format("%.0f", getMaximumWaitTime(degree)))
          .append(" seconds.")
          .append(NL);
    }
}
