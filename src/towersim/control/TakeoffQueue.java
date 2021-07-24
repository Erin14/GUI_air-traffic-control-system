package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a first-in-first-out (FIFO) queue of aircraft waiting to take off.
 *
 * FIFO ensures that the order in which aircraft are allowed to take off is based on how
 * long they have been waiting in the queue. An aircraft that has been waiting for longer
 * than another aircraft will always be allowed to take off before the other aircraft.
 */
public class TakeoffQueue extends AircraftQueue {

    /** the aircraft list in this queue */
    private final List<Aircraft> allAircraft;

    /**
     * Constructs a new TakeoffQueue with an initially empty queue of aircraft.
     */
    public TakeoffQueue() {
        this.allAircraft = new ArrayList<>();
    }

    /**
     * Adds the given aircraft to the queue.
     *
     * @param aircraft aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        allAircraft.add(aircraft);
    }

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     *
     * @return the aircraft at the front of the queue; null if the queue is empty
     */
    @Override
    public Aircraft peekAircraft() {
        if (allAircraft.size() == 0) {
            return null;
        }
        return allAircraft.get(0);
    }

    /**
     * Removes and returns the aircraft at the front of the queue.
     * Returns null if the queue is empty.
     *
     * @return aircraft at front of queue; null if the queue is empty
     */
    @Override
    public Aircraft removeAircraft() {
        // the aircraft to be removed
        Aircraft removedAircraft = peekAircraft();
        allAircraft.remove(removedAircraft);
        return removedAircraft;
    }

    /**
     * Returns a list containing all aircraft in the queue, in order.
     *
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        return new ArrayList<>(allAircraft);
    }

    /**
     * Returns true if the given aircraft is in the queue.
     *
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    @Override
    public boolean containsAircraft(Aircraft aircraft) {
        return allAircraft.contains(aircraft);
    }
}
