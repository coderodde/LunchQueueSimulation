package net.coderodde.simulation.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

/**
 * This class implements a simple data structure for dealing with probability 
 * distributions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015)
 * @param <T> the actual item type.
 */
public class ProbabilityDistribution<T> {
   
    private final List<T> elementList    = new ArrayList<>();
    private final List<Float> weightList = new ArrayList<>();
    private final Random random;
    private float totalWeight = 0.0f;
    
    public ProbabilityDistribution(Random random) {
        this.random = 
                Objects.requireNonNull(random, 
                                       "The random number generator is null.");
    }
    
    public ProbabilityDistribution() {
        this(new Random());
    }
    
    public void add(T element, float weight) {
        elementList.add(element);
        weightList.add(weight);
        totalWeight += weight;
    }
    
    public T choose() {
        if (elementList.isEmpty()) {
            throw new NoSuchElementException("The distribution is empty.");
        }
        
        float f = totalWeight * random.nextFloat();
        float sum = weightList.get(0);
        
        for (int i = 1; i < weightList.size(); ++i) {
            if (sum >= f) {
                return elementList.get(i - 1);
            }
            
            sum += weightList.get(i);
        }
        
        return elementList.get(elementList.size() - 1);
    }
}
