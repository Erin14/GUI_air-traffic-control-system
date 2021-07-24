package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.util.Encodable;

import java.util.List;
import java.util.StringJoiner;

/**
 * Abstract representation of a queue containing aircraft.
 * Aircraft can be added to the queue, and aircraft at the front of the queue can be
 * queried or removed. A list of all aircraft contained in the queue (in queue order)
 * can be obtained. The queue can be checked for containing a specified aircraft.
 * The order that aircraft are removed from the queue depends on the chosen concrete
 * implementation of the AircraftQueue.
 */
public abstract class AircraftQueue implements Encodable {
    /**
     * An abstract method. Adds the given aircraft to the queue.
     *
     * @param aircraft aircraft to add to queue
     */
    public abstract void addAircraft(Aircraft aircraft);

    /**
     * An abstract method. Removes and returns the aircraft at the front of the queue.
     * Returns null if the queue is empty.
     *
     * @return aircraft at front of queue if the queue is not empty;
     *         null if the queue is empty
     */
    public abstract Aircraft removeAircraft();

    /**
     * An abstract method. Returns the aircraft at the front of the queue without removing
     * it from the queue, or null if the queue is empty.
     *
     * @return aircraft at front of queue if the queue is not empty;
     *         null if the queue is empty
     */
    public abstract Aircraft peekAircraft();

    /**
     * Returns a list containing all aircraft in the queue, in order.
     *
     * @return list of all aircraft in queue, in queue order
     */
    public abstract List<Aircraft> getAircraftInOrder();

    /**
     * Returns true if the given aircraft is in the queue.
     *
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    public abstract boolean containsAircraft(Aircraft aircraft);

    /**
     * Returns the human-readable string representation of this aircraft queue.
     * The format of the string to return is: QueueType [callsign1, callsign2, ..., callsignN].
     *
     * @return string representation of this queue
     */
    @Override
    public String toString() {
        // a string joiner storing the callsign part
        StringJoiner callsignPart = new StringJoiner(", ", " [", "]");
        // add the callsign of all aircraft if the queue is not empty
        for (Aircraft aircraft : getAircraftInOrder()) {
            callsignPart.add(aircraft.getCallsign());
        }
        return getClass().getSimpleName() + callsignPart.toString();
    }

    /**
     * Returns the machine-readable string representation of this aircraft queue.
     * The format of the string to return is:
     * QueueType:numAircraft
     * callsign1,callsign2,...,callsignN
     *
     * @return encoded string representation of this aircraft queue
     */
    @Override
    public String encode() {
        // the first line of the queue encode
        String theFirstLine = getClass().getSimpleName() + ":" + getAircraftInOrder().size();
        if (getAircraftInOrder().size() == 0) {
            // if there is no aircraft in the queue
            return theFirstLine;
        }

        // a string joiner storing the callsign part
        StringJoiner callsignPart = new StringJoiner(",");
        // add the callsign of all aircraft if the queue is not empty
        for (Aircraft aircraft : getAircraftInOrder()) {
            callsignPart.add(aircraft.getCallsign());
        }

        return String.join(System.lineSeparator(), theFirstLine, callsignPart.toString());
    }
}
