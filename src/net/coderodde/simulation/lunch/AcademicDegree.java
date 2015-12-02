package net.coderodde.simulation.lunch;

/**
 * This class implements an enumeration over academic degrees.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015) 
 */
public enum AcademicDegree {
   
    UNDERGRADUATE("Undergraduate"),
    BACHELOR("BSc"),
    MASTER("MSc"),
    DOCTOR("PhD");
    
    private final String description;
    
    @Override
    public String toString() {
        return description;
    }
    
    private AcademicDegree(String description) {
        this.description = description;
    }
}
