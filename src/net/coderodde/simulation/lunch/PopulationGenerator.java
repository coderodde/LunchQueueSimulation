package net.coderodde.simulation.lunch;

import java.util.Objects;
import java.util.Random;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;
import static net.coderodde.simulation.lunch.Utils.choose;

/**
 * This class facilitates random generation of person records.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class PopulationGenerator {
    
    private final Random random;
    private final ProbabilityDistribution<AcademicDegree> degreeDistribution;
    private final double meanOfMeanLunchTime;
    private final double meanOfLunchTimeDeviation;
    private final double sdOfMeanLunchTime;
    private final double sdOfLunchTimeDeviation;
    
    public PopulationGenerator(
            Random random, 
            ProbabilityDistribution<AcademicDegree> degreeDistribution,
            double meanOfMeanLunchTime,
            double meanOfLunchTimeDeviation,
            double sdOfMeanLunchTime,
            double sdOfLunchTimeDeviation) {
        Objects.requireNonNull(random, "The random number generator is null.");
        Objects.requireNonNull(degreeDistribution, 
                               "The degree distribution is null.");
        
        checkMean(meanOfMeanLunchTime);
        checkMean(meanOfLunchTimeDeviation);
        checkStandardDeviation(sdOfMeanLunchTime);
        checkStandardDeviation(sdOfLunchTimeDeviation);
        
        this.random                   = random;
        this.degreeDistribution       = degreeDistribution;
        this.meanOfMeanLunchTime      = meanOfMeanLunchTime;
        this.meanOfLunchTimeDeviation = meanOfLunchTimeDeviation;
        this.sdOfMeanLunchTime        = sdOfMeanLunchTime;
        this.sdOfLunchTimeDeviation   = sdOfLunchTimeDeviation;
    }
    
    public Person createRandomPerson() {
        return new Person(choose(FIRST_NAMES, random),
                          choose(LAST_NAMES, random),
                          degreeDistribution.choose());
    }
    
    public Person[] createPopulation(int size) {
        Person[] population = new Person[size];
        
        for (int i = 0; i < size; ++i) {
            population[i] = createRandomPerson();
        }
        
        return population;
    }
    
    public LunchTimePreferences createRandomLunchTimePreferences() {
        return new LunchTimePreferences(
                meanOfMeanLunchTime + sdOfMeanLunchTime * random.nextGaussian(),
                meanOfLunchTimeDeviation + 
                        sdOfLunchTimeDeviation * random.nextGaussian());
    }
    
    private static final String[] FIRST_NAMES = {
        "Ada",
        "Alice",
        "Al",
        "Alma",
        "Alvin",
        "Amanda",
        "Bob",
        "Brandon",
        "Brooke",
        "Bruce",
        "Camilla",
        "Cecilia",
        "Carl",
        "David",
        "Elsa",
        "Ida",
        "Jack",
        "John",
        "Nathan",
        "Nick",
        "Phoebe",
        "Rachel",
        "Richard",
        "Rodion",
        "Roger",
        "Roland",
        "Rolf",
        "Roy",
        "Terence",
        "Terry",
        "Viola"
    };
    
    private static final String[] LAST_NAMES = {
        "Abbey",
        "Ackerman",
        "Bonham",
        "Bradly",
        "Cantrell",
        "Carter",
        "Dawkins",
        "Dawson",
        "Edison",
        "Efremov",
        "Fay",
        "Fleming",
        "Garrett",
        "Hallman",
        "Irvine",
        "Jacobson",
        "Kidd",
        "Lacey",
        "Marlow",
        "Nelson",
        "Oliver",
        "Parks",
        "Pearson",
        "Peterson",
        "Quincey",
        "Ridley",
        "Saunders",
        "Thompson",
        "Walton",
        "Wilkerson"
    };
}
