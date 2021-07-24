package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.*;

/**
 * Represents a rule-based queue of aircraft waiting in the air to land.
 *
 * The rules in the landing queue are designed to ensure that aircraft are prioritised for
 * landing based on "urgency" factors such as remaining fuel onboard, emergency status and
 * cargo type.
 */
public class LandingQueue extends AircraftQueue {

    /** the aircraft list in this queue */
    private final List<Aircraft> allAircraft;
    
    /** the map storing the added order of all the aircraft */
    private final Map<Aircraft, Integer> addedOrder;

    /** the number of added order, 1 for the first added aircraft */
    private int numOrder;

    /**
     * Constructs a new LandingQueue with an initially empty queue of aircraft.
     */
    public LandingQueue() {
        // the aircraft list in this queue, initially empty
        this.allAircraft = new ArrayList<>();
        // the map storing the added order of all the aircraft, initially empty
        this.addedOrder = new HashMap<>();
        this.numOrder = 1;
    }

    /**
     * Adds the given aircraft to the queue.
     *
     * @param aircraft aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        allAircraft.add(aircraft);
        // store the order of the aircraft been added
        addedOrder.put(aircraft, numOrder++);
    }

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty. The rules for determining which aircraft in the queue
     * should be returned next are described in the OrderRule class.
     *
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        // return null if the queue is empty
        if (allAircraft.size() == 0) {
            return null;
        }
        return getAircraftInOrder().get(0);
    }

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null if the queue
     * is empty. The same rules as described in peekAircraft() should be used for determining
     * which aircraft to remove and return.
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
        // sort the aircraft list according to the order rule
        sortOrder();
        return new ArrayList<>(allAircraft);
    }

    /**
     * Sort the aircraft list according to the order rule.
     * The rule of the order should be as follows:
     * 1) If an aircraft is currently in a state of emergency, it should be in front.
     * 2) If an aircraft has less than or equal to 20 percent fuel remaining, a critical level,
     *    it should be in front.
     * 3) If one of them is passenger aircraft, it should be in front.
     * 4) Otherwise, the aircraft with add first should be in front.
     */
    private void sortOrder() {
        allAircraft.sort((aircraft1, aircraft2) -> {
            // check the emergency state of two aircraft
            if (aircraft1.hasEmergency()) {
                // if both of them has emergency, then compare the added order
                return aircraft2.hasEmergency()
                        ? addedOrder.get(aircraft1).compareTo(addedOrder.get(aircraft2)) : -1;
            } else if (aircraft2.hasEmergency()) {
                return 1;
            }

            // check the fuel percent remaining of two aircraft
            if (aircraft1.getFuelPercentRemaining() <= 20) {
                // if both of them has a critical level, then compare the added order
                return (aircraft2.getFuelPercentRemaining() <= 20)
                        ? addedOrder.get(aircraft1).compareTo(addedOrder.get(aircraft2)) : -1;
            } else if (aircraft2.getFuelPercentRemaining() <= 20) {
                return 1;
            }

            // check if two aircraft are passenger aircraft or not
            if (aircraft1.getClass().getSimpleName().equals("PassengerAircraft")) {
                // if both of them has are passenger aircraft, then compare the added order
                return (aircraft2.getClass().getSimpleName().equals("PassengerAircraft"))
                        ? addedOrder.get(aircraft1).compareTo(addedOrder.get(aircraft2)) : -1;
            } else if (aircraft2.getClass().getSimpleName().equals("PassengerAircraft")) {
                return 1;
            }

            // check the added order
            return addedOrder.get(aircraft1).compareTo(addedOrder.get(aircraft2));
        });
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
