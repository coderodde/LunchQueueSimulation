package net.coderodde.simulation.lunch;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This class implements a FIFO queue over priority categories. Not to be 
 * confused with a priority queue.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 3, 2015)
 */
public class PrioritizedQueue {
   
    private final Queue<LunchQueueEvent> queueOfDoctors    = new ArrayDeque<>();
    private final Queue<LunchQueueEvent> queueOfMasters    = new ArrayDeque<>();
    private final Queue<LunchQueueEvent> queueOfBachelors  = new ArrayDeque<>();
    private final Queue<LunchQueueEvent> queueOfUndergrads = new ArrayDeque<>();
    
    public void push(LunchQueueEvent event) {
        switch (event.getPerson().getAcademicDegree()) {
            case UNDERGRADUATE: {
                queueOfUndergrads.add(event);
                break; 
            }
            
            case BACHELOR: {
                queueOfBachelors.add(event);
                break;
            }
            
            case MASTER: {
                queueOfMasters.add(event);
                break;
            }
            
            case DOCTOR: {
                queueOfDoctors.add(event);
                break;
            }
        }
    }
    
    public boolean isEmpty() {
        return queueOfUndergrads.isEmpty() 
                && queueOfBachelors.isEmpty()
                && queueOfMasters.isEmpty()
                && queueOfDoctors.isEmpty();
    }
    
    public LunchQueueEvent pop() {
        if (isEmpty()) {
            throw new NoSuchElementException(
                    "Popping from an empty prioritized queue.");
        }
        
        if (!queueOfDoctors.isEmpty()) {
            return queueOfDoctors.remove();
        }
        
        if (!queueOfMasters.isEmpty()) {
            return queueOfMasters.remove();
        }
        
        if (!queueOfBachelors.isEmpty()) {
            return queueOfBachelors.remove();
        }
        
        if (!queueOfUndergrads.isEmpty()) {
            return queueOfUndergrads.remove();
        }
        
        throw new IllegalStateException(
                "This should never happend. Please debug.");
    }
}
