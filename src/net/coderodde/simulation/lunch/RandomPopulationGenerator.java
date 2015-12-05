package net.coderodde.simulation.lunch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import static net.coderodde.simulation.lunch.Utils.checkMean;
import static net.coderodde.simulation.lunch.Utils.checkStandardDeviation;

/**
 * This class facilitates random generation of population.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 */
public class RandomPopulationGenerator {
    
    private final Random random;
    private final Map<AcademicDegree, Integer> distribution;
    private final double meanLunchTime;
    private final double standardDeviationOfLunchTime;
    
    private static final class Configuration {
        private final Map<AcademicDegree, Integer> distribution = 
                new HashMap<>();
        private Random random;
        private double meanLunchTime;
    }
    
    public static final class DegreeSelector {
        
        private final Configuration configuration;
        
        DegreeSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        DegreeSelector withDegreeCount(AcademicDegree degree, int count) {
            Objects.requireNonNull(degree, "The input degree is null.");
            
            if (count <= 0) {
                throw new IllegalArgumentException(
                        "The degree count is not positive: " + count);
            }
            
            configuration.distribution.put(degree, count);
            return this;
        }
        
        StandardDeviationSelector withMeanLunchTime(double meanLunchTime) {
            checkMean(meanLunchTime);
            configuration.meanLunchTime = meanLunchTime;
            return new StandardDeviationSelector(configuration);
        }
    }
    
    public static final class StandardDeviationSelector {
        
        private final Configuration configuration;
        
        StandardDeviationSelector(Configuration configuration) {
            this.configuration = configuration;
        }
        
        public Population withLunchTimeStandardDeviation(
                double lunchTimeStandardDeviation) {
            checkStandardDeviation(lunchTimeStandardDeviation);
            return new RandomPopulationGenerator(
                    configuration.random,
                    configuration.distribution,
                    configuration.meanLunchTime,
                    lunchTimeStandardDeviation).generate();
        }
    }
    
    public static DegreeSelector withRandom(Random random) {
        Objects.requireNonNull(random, "The input Random is null.");
        Configuration configuration = new Configuration();
        configuration.random = random;
        return new DegreeSelector(configuration);
    }
    
    public static DegreeSelector withDefaultRandom() {
        return withRandom(new Random());
    }
    
    private RandomPopulationGenerator(Random random, 
                                      Map<AcademicDegree, Integer> distribution,
                                      double meanLunchTime,
                                      double standardDeviationOfLunchTime) {
        this.random       = random;
        this.distribution = distribution;
        this.meanLunchTime = meanLunchTime;
        this.standardDeviationOfLunchTime = standardDeviationOfLunchTime;
    }
    
    public Population generate() {
        int populationSize = 0;
        
        for (Map.Entry<AcademicDegree, Integer> entry : distribution.entrySet()) {
            populationSize += entry.getValue();
        }
        
        List<Person> allPersonList = 
                new ArrayList<>(FIRST_NAMES.length * LAST_NAMES.length);
        
        List<AcademicDegree> degreeList = new ArrayList<>(populationSize);
        
        for (AcademicDegree degree : AcademicDegree.values()) {
            int count = distribution.getOrDefault(degree, 0);
            
            for (int i = 0; i < count; ++i) {
                degreeList.add(degree);
            }
        }
        
        
        Collections.<AcademicDegree>shuffle(degreeList, random);
        int i = 0;
        
        outer:
        for (String firstName : FIRST_NAMES) {
            for (String lastName : LAST_NAMES) {
                if (i == degreeList.size()) {
                    break outer;
                }
                
                allPersonList.add(new Person(firstName, 
                                             lastName, 
                                             degreeList.get(i++)));
            }
        }
        
        Collections.shuffle(allPersonList, random);
        populationSize = Math.min(populationSize, allPersonList.size());
        
        Population population = new Population();
        
        for (i = 0; i < populationSize; ++i) {
            population.addPerson(allPersonList.get(i), getRandomLunchTime());
        }
        
        return population;
    }
    
    private double getRandomLunchTime() {
        return meanLunchTime + standardDeviationOfLunchTime * 
                               random.nextGaussian();
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
