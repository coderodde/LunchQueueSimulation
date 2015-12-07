package net.coderodde.simulation.lunch;

/**
 * This class describes a lunch queue event.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015).
 */
final class LunchQueueEvent implements Comparable<LunchQueueEvent> {
    
    private final Person person;
    private final int timeStamp;
    
    LunchQueueEvent(Person person, int timeStamp) {
        this.person = person;
        this.timeStamp = timeStamp;
    }
    
    Person getPerson() {
        return person;
    }
    
    int getTimestamp() {
        return timeStamp;
    }
    
    @Override
    public int compareTo(LunchQueueEvent anotherEvent) {
        return Double.compare(timeStamp, anotherEvent.timeStamp);
    }
}
