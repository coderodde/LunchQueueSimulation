package net.coderodde.simulation.lunch;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the statistics of a simulation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class SimulationResult {

    private final Map<AcademicDegree, Double> waitAverageMap = new HashMap<>();
    private final Map<AcademicDegree, Double> waitStandardDeviationMap = 
            new HashMap<>();
    
    public double getWaitAverage(AcademicDegree degree) {
        return waitAverageMap.getOrDefault(degree, Double.NaN);
    }
    
    public double getWaitStandardDeviation(AcademicDegree degree) {
        return waitStandardDeviationMap.getOrDefault(degree, Double.NaN);
    }
    
    void putAverageWaitTime(AcademicDegree degree, double time) {
        waitAverageMap.put(degree, time);
    }
    
    void putWaitStandardDeviation(AcademicDegree degree, double deviation) {
        waitStandardDeviationMap.put(degree, deviation);
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
          .append("Doctors:        average wait time ")
          .append(getWaitAverage(AcademicDegree.DOCTOR))
          .append(", wait s.d. ")
          .append(getWaitStandardDeviation(AcademicDegree.DOCTOR))
          .append("\n")
          .append("Masters:        average wait time ")
          .append(getWaitAverage(AcademicDegree.MASTER))
          .append(", wait s.d. ")
          .append(getWaitStandardDeviation(AcademicDegree.MASTER))
          .append("\n")
          .append("Bachelors:      average wait time ")
          .append(getWaitAverage(AcademicDegree.BACHELOR))
          .append(", wait s.d. ")
          .append(getWaitStandardDeviation(AcademicDegree.BACHELOR))
          .append("\n")
          .append("Undergraduates: average wait time ")
          .append(getWaitAverage(AcademicDegree.UNDERGRADUATE))
          .append(", wait s.d. ")
          .append(getWaitStandardDeviation(AcademicDegree.UNDERGRADUATE))
          .toString();
    }
}
