package net.coderodde.simulation.lunch;

import java.util.Objects;

/**
 * This class implements a record for a person.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class Person {

    private final String firstName;
    private final String lastName;
    private final AcademicDegree academicDegree;
    private final String stringRepresentation;
    private final String identity;
    
    public Person(String firstName, 
                  String lastName, 
                  AcademicDegree academicDegree) {
        Objects.requireNonNull(firstName,      "The first name is null.");
        Objects.requireNonNull(lastName,       "The last name is null.");
        Objects.requireNonNull(academicDegree, "The academic degree is null.");
        
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.academicDegree = academicDegree;
        
        this.stringRepresentation = "[" + firstName + " " + lastName + ", " + 
                                          academicDegree + "]";
        this.identity = firstName + " " + lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public AcademicDegree getAcademicDegree() {
        return academicDegree;
    }
    
    @Override
    public String toString() {
        return stringRepresentation;
    }
    
    @Override
    public int hashCode() {
        return identity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Person other = (Person) obj;
        return Objects.equals(identity, other.identity);
    }
}
