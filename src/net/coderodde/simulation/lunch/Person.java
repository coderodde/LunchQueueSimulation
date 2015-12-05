package net.coderodde.simulation.lunch;

import java.util.Objects;

/**
 * This class implements a record for a person.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public final class Person {

    private final String firstName;
    private final String lastName;
    private final AcademicDegree academicDegree;
    private final String stringRepresentation;
    private final String identity;
    
    public static LastNameSelector withFirstName(String firstName) {
        Objects.requireNonNull(firstName, 
                               "The first name of a person is null.");
        Configuration configuration = new Configuration();
        configuration.firstName = firstName;
        return new LastNameSelector(configuration);
    }
    
    public static final class LastNameSelector {
        
        private final Configuration configuration;
        
        private LastNameSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        public AcademicDegreeSelector withLastName(String lastName) {
            Objects.requireNonNull(lastName, 
                                   "The last name of a person is null.");
            configuration.lastName = lastName;
            return new AcademicDegreeSelector(configuration);
        }
    }
    
    public static final class AcademicDegreeSelector {
        
        private final Configuration configuration;
        
        AcademicDegreeSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        public Person withAcademicDegree(AcademicDegree degree) {
            Objects.requireNonNull(degree, "The academic degree is null.");
            return new Person(configuration.firstName,
                              configuration.lastName,
                              degree);
        }
    }
    
    private Person(String firstName, 
                   String lastName, 
                   AcademicDegree academicDegree) {
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
    
    private static final class Configuration {
        private String firstName;
        private String lastName;
    }
}
