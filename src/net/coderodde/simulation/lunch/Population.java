package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public final class Population {
    
    private final Map<Person, Integer> arrivalTimeMap = new HashMap<>();
    
    public final class ArrivalTimeSelector {
        private final Person person;

        ArrivalTimeSelector(Person person) {
            this.person = Objects.requireNonNull(person,
                                                 "The input person is null.");
        }
        
        public boolean withArrivalTime(int arrivalTime) {
            checkTime(arrivalTime);
            
            if (arrivalTimeMap.containsKey(person)) {
                return false;
            }
            
            arrivalTimeMap.put(person, arrivalTime);
            return true;
        }
    }
    
    public ArrivalTimeSelector addPerson(Person person) {
        return new ArrivalTimeSelector(person);
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
        
        Collections.sort(eventList, 
                        (event1, event2) -> {
            // Try to compare by the time stamps of the events.
            int cmp = event1.compareTo(event2);
            
            if (cmp != 0) {
                return cmp;
            }
            
            // The two input events have same time stamp, break ties by person
            // priority.
            return event1.getPerson().compareTo(event2.getPerson());
        });
        
        return new ArrayDeque<>(eventList);
    }
}
