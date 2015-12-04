package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import static net.coderodde.simulation.lunch.Utils.checkTime;

/**
 * This class represents simulated population.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 3, 2015)
 */
public class Population {
    
    private final Map<Person, Double> arrivalTimeMap = new HashMap<>();
    
    public boolean addPerson(Person person, double arrivalTime) {
        Objects.requireNonNull(person, "The input person is null.");
        checkTime(arrivalTime);
        
        if (arrivalTimeMap.containsKey(person)) {
            return false;
        }
        
        arrivalTimeMap.put(person, arrivalTime);
        return true;
    } 
    
    public int size() {
        return arrivalTimeMap.size();
    }
   
    Set<Person> getPersonSet() {
        return Collections.<Person>unmodifiableSet(arrivalTimeMap.keySet());
    }
    
    Queue<LunchQueueEvent> toEventQueue() {
        List<LunchQueueEvent> eventList = new ArrayList<>(size());
        
        getPersonSet().stream().forEach((person) -> {
            eventList.add(new LunchQueueEvent(person, 
                                              arrivalTimeMap.get(person)));
        });
        
        Collections.sort(eventList);
        return new ArrayDeque<>(eventList);
    }
}
