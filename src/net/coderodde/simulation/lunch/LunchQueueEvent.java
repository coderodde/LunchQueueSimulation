package net.coderodde.simulation.lunch;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015).
 */
public class LunchQueueEvent implements Comparable<LunchQueueEvent> {
    
    public enum EventType {
        ENTER_QUEUE,
        SERVED
    }
    
    private final Person person;
    private final EventType eventType;
    private final double timeStamp;
    
    public LunchQueueEvent(Person person, 
                           EventType eventType, 
                           double timeStamp) {
        this.person = person;
        this.eventType = eventType;
        this.timeStamp = timeStamp;
    }
    
    public Person getPerson() {
        return person;
    }
    
    @Override
    public int compareTo(LunchQueueEvent anotherEvent) {
        return Double.compare(timeStamp, anotherEvent.timeStamp);
    }
}
