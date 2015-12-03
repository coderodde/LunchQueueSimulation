package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import static net.coderodde.simulation.lunch.Utils.checkTime;

/**
 * This class represents simulated population.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 3, 2015)
 */
public class Population {
    
    private final List<Person> personList = new ArrayList<>();
    private final Map<Person, Double> arrivalTimeMap = new HashMap<>();
    
    public void addPerson(Person person, double arrivalTime) {
        Objects.requireNonNull(person, "The input person is null.");
        checkTime(arrivalTime);
        personList.add(person);
        arrivalTimeMap.put(person, arrivalTime);
    } 
   
    List<Person> getPersonList() {
        return Collections.<Person>unmodifiableList(personList);
    }
    
    Queue<LunchQueueEvent> toEventQueue() {
        List<LunchQueueEvent> eventList = new ArrayList<>(personList.size());
        
        personList.stream().forEach((person) -> {
            eventList.add(new LunchQueueEvent(person, 
                                              arrivalTimeMap.get(person)));
        });
        
        Collections.sort(eventList);
        return new ArrayDeque<>(eventList);
    }
}
