package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeMap;

/**
 * This class implements a FIFO queue over priority categories. Not to be 
 * confused with a priority queue.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 3, 2015)
 */
final class PrioritizedQueue {
   
    private final Map<AcademicDegree, Queue<LunchQueueEvent>> map 
            = new TreeMap<>();
    
    private int size;
    
    void push(LunchQueueEvent event) {
        AcademicDegree degree = event.getPerson().getAcademicDegree();
        map.putIfAbsent(degree, new ArrayDeque<>());
        map.get(degree).add(event);
        ++size;
    }
    
    int size() {
        return size;
    }
    
    boolean isEmpty() {
        return size == 0;
    }
    
    LunchQueueEvent pop() {
        if (isEmpty()) {
            throw new NoSuchElementException(
                    "Popping from an empty prioritized queue.");
        }
        
        for (Queue<LunchQueueEvent> queue : map.values()) {
            if (!queue.isEmpty()) {
                --size;
                return queue.remove();
            }
        }
        
        throw new IllegalStateException(
                "This should never happend. Please debug.");
    }
}
