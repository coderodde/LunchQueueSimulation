package net.coderodde.simulation.lunch;

/**
 * This class describes a lunch queue event.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 2, 2015).
 */
final class LunchQueueEvent implements Comparable<LunchQueueEvent> {
    
    private final Person person;
    private final double timeStamp;
    
    LunchQueueEvent(Person person, double timeStamp) {
        this.person = person;
        this.timeStamp = timeStamp;
    }
    
    Person getPerson() {
        return person;
    }
    
    double getTimestamp() {
        return timeStamp;
    }
    
    @Override
    public int compareTo(LunchQueueEvent anotherEvent) {
        return Double.compare(timeStamp, anotherEvent.timeStamp);
    }
}
