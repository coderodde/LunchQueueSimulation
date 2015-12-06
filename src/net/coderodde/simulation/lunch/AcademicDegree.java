package net.coderodde.simulation.lunch;

/**
 * This class implements an enumeration over academic degrees.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015) 
 */
public enum AcademicDegree {
   
    // The order denotes priority from highest to lowest.
    DOCTOR       ("PhD"),
    MASTER       ("MSc"),
    BACHELOR     ("BSc"),
    UNDERGRADUATE("Undergraduate");
    
    private final String description;
    
    @Override
    public String toString() {
        return description;
    }
    
    private AcademicDegree(String description) {
        this.description = description;
    }
}
