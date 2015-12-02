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
    
    public Person(String firstName, 
                  String lastName, 
                  AcademicDegree academicDegree) {
        Objects.requireNonNull(firstName,      "The first name is null.");
        Objects.requireNonNull(lastName,       "The last name is null.");
        Objects.requireNonNull(academicDegree, "The academic degree is null.");
        
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.academicDegree = academicDegree;
        
        // +11 for delimiter characters and the length of the degree 
        // description.
        StringBuilder sb = 
                new StringBuilder(firstName.length() + 
                                  lastName.length() + 
                                  academicDegree.toString().length() + 5);
        
        sb.append("[")
          .append(firstName)
          .append(" ")
          .append(lastName)
          .append(", ")
          .append(academicDegree)
          .append("]");
        
        this.stringRepresentation = sb.toString();
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
        return stringRepresentation.hashCode();
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
        return Objects.equals(stringRepresentation, other.stringRepresentation);
    }
}
