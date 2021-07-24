package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftType;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.TaskType;
import towersim.util.NoSpaceException;
import towersim.util.NoSuitableGateException;
import towersim.util.Tickable;

import java.util.*;

/**
 * Represents a the control tower of an airport.
 * <p>
 * The control tower is responsible for managing the operations of the airport, including arrivals
 * and departures in/out of the airport, as well as aircraft that need to be loaded with cargo
 * at gates in terminals.
 * @ass1
 */
public class ControlTower implements Tickable {
    /** number of ticks that have elapsed since the tower was created */
    private long totalTicksElapsed;

    /** List of all aircraft managed by the control tower. */
    private final List<Aircraft> allAircraft;

    /** queue of aircraft waiting to land */
    private LandingQueue landingQueue;

    /** queue of aircraft waiting to take off */
    private TakeoffQueue takeoffQueue;

    /** mapping of aircraft that are loading cargo to the number of ticks remaining for loading */
    private Map<Aircraft, Integer> loadingAircraft;

    /** List of all terminals in the airport. */
    private final List<Terminal> allTerminals;

    /**
     * Creates a new ControlTower. The list of terminals should be initialised as an empty list.
     *
     * @param ticksElapsed number of ticks that have elapsed since the tower was first created
     * @param aircraft list of aircraft managed by the control tower
     * @param landingQueue queue of aircraft waiting to land
     * @param takeoffQueue queue of aircraft waiting to take off
     * @param loadingAircraft mapping of aircraft that are loading cargo to the number of ticks
     *                        remaining for loading
     */
    public ControlTower(long ticksElapsed, List<Aircraft> aircraft, LandingQueue landingQueue,
                        TakeoffQueue takeoffQueue, Map<Aircraft, Integer> loadingAircraft) {
        this.totalTicksElapsed = ticksElapsed;
        this.allAircraft = aircraft;
        this.landingQueue = landingQueue;
        this.takeoffQueue = takeoffQueue;
        this.loadingAircraft = loadingAircraft;
        // the list of terminals should be initialised as an empty list
        this.allTerminals = new ArrayList<>();
    }

    /**
     * Adds the given terminal to the jurisdiction of this control tower.
     *
     * @param terminal terminal to add
     * @ass1
     */
    public void addTerminal(Terminal terminal) {
        this.allTerminals.add(terminal);
    }

    /**
     * Returns a list of all terminals currently managed by this control tower.
     * <p>
     * The order in which terminals appear in this list should be the same as the order in which
     * they were added by calling {@link #addTerminal(Terminal)}.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all terminals
     * @ass1
     */
    public List<Terminal> getTerminals() {
        return new ArrayList<>(this.allTerminals);
    }

    /**
     * Adds the given aircraft to the jurisdiction of this control tower.
     * <p>
     * If the aircraft's current task type is {@code WAIT} or {@code LOAD}, it should be parked at a
     * suitable gate as found by the {@link #findUnoccupiedGate(Aircraft)} method.
     * If there is no suitable gate for the aircraft, the {@code NoSuitableGateException} thrown by
     * {@code findUnoccupiedGate()} should be propagated out of this method.
     *
     * @param aircraft aircraft to add
     * @throws NoSuitableGateException if there is no suitable gate for an aircraft with a current
     *                                 task type of {@code WAIT} or {@code LOAD}
     * @ass1
     */
    public void addAircraft(Aircraft aircraft) throws NoSuitableGateException {
        TaskType currentTaskType = aircraft.getTaskList().getCurrentTask().getType();
        if (currentTaskType == TaskType.WAIT || currentTaskType == TaskType.LOAD) {
            Gate gate = findUnoccupiedGate(aircraft);
            try {
                gate.parkAircraft(aircraft);
            } catch (NoSpaceException ignored) {
                // not possible, gate unoccupied
            }
        }
        this.allAircraft.add(aircraft);
        placeAircraftInQueues(aircraft);
    }

    /**
     * Finds the gate where the given aircraft is parked, and returns null if the aircraft is
     * not parked at any gate in any terminal.
     *
     * @param aircraft aircraft whose gate to find
     * @return gate occupied by the given aircraft; or null if none exists
     * @ass1
     */
    public Gate findGateOfAircraft(Aircraft aircraft) {
        for (Terminal terminal : this.allTerminals) {
            for (Gate gate : terminal.getGates()) {
                if (Objects.equals(gate.getAircraftAtGate(), aircraft)) {
                    return gate;
                }
            }
        }
        return null;
    }

    /**
     * Returns a list of all aircraft currently managed by this control tower.
     * <p>
     * The order in which aircraft appear in this list should be the same as the order in which
     * they were added by calling {@link #addAircraft(Aircraft)}.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all aircraft
     * @ass1
     */
    public List<Aircraft> getAircraft() {
        return new ArrayList<>(this.allAircraft);
    }

    /**
     * Returns the number of ticks that have elapsed for this control tower. If the control tower
     * was created with a non-zero number of elapsed ticks, this number should be taken into account
     * in the return value of this method.
     *
     * @return number of ticks elapsed
     */
    public long getTicksElapsed() {
        return this.totalTicksElapsed;
    }

    /**
     * Returns the queue of aircraft waiting to land.
     *
     * @return landing queue
     */
    public AircraftQueue getLandingQueue() {
        return this.landingQueue;
    }

    /**
     * Returns the queue of aircraft waiting to take off.
     *
     * @return takeoff queue
     */
    public AircraftQueue getTakeoffQueue() {
        return this.takeoffQueue;
    }

    /**
     * Returns the mapping of loading aircraft to their remaining load times.
     *
     * @return loading aircraft map
     */
    public Map<Aircraft, Integer> getLoadingAircraft() {
        return this.loadingAircraft;
    }

    /**
     * Attempts to find an unoccupied gate in a compatible terminal for the given aircraft.
     * <p>
     * Only terminals of the same type as the aircraft's AircraftType (see
     * {@link towersim.aircraft.AircraftCharacteristics#type}) should be considered. For example,
     * for an aircraft with an AircraftType of {@code AIRPLANE}, only AirplaneTerminals may be
     * considered.
     * Terminals that are currently in a state of emergency should not be considered.
     * <p>
     * For each compatible terminal, the {@link Terminal#findUnoccupiedGate()} method should be
     * called to attempt to find an unoccupied gate in that terminal. If
     * {@code findUnoccupiedGate()} does not find a suitable gate, the next compatible terminal
     * in the order they were added should be checked instead, and so on.
     * <p>
     * If no unoccupied gates could be found across all compatible terminals, a
     * {@code NoSuitableGateException} should be thrown.
     *
     * @param aircraft aircraft for which to find gate
     * @return gate for given aircraft if one exists
     * @throws NoSuitableGateException if no suitable gate could be found
     * @ass1
     */
    public Gate findUnoccupiedGate(Aircraft aircraft) throws NoSuitableGateException {
        AircraftType aircraftType = aircraft.getCharacteristics().type;
        for (Terminal terminal : allTerminals) {
            /*
             * Only check for available gates at terminals that are of the same aircraft type as
             * the aircraft and not in a state of emergency
             */
            if ((!terminal.hasEmergency())
                    && ((terminal instanceof AirplaneTerminal
                            && aircraftType == AircraftType.AIRPLANE)
                        || (terminal instanceof HelicopterTerminal
                                && aircraftType == AircraftType.HELICOPTER))) {
                try {
                    // This terminal found a gate, return it
                    return terminal.findUnoccupiedGate();
                } catch (NoSuitableGateException e) {
                    // If this terminal has no unoccupied gates, try the next one
                }
            }
        }
        throw new NoSuitableGateException("No gate available for aircraft");
    }

    /**
     * Attempts to land one aircraft waiting in the landing queue and park it at a suitable gate.
     * 1) If there are no aircraft in the landing queue waiting to land, then the method should
     * return false and no further action should be taken.
     * 2) If there is at least one aircraft in the landing queue, then a suitable gate should be
     * found for the aircraft at the front of the queue. If there is no suitable gate, the aircraft
     * should not be landed and should remain in the queue, and the method should return false and
     * no further action should be taken.
     * 3) If there is a suitable gate, the aircraft should be removed from the queue and it should
     * be parked at that gate. The aircraft's passengers or freight should be unloaded immediately,
     * by calling Aircraft.unload().
     * 4) Finally, the landed aircraft should move on to the next task in its task list and the
     * method should return true.
     *
     * @return true if an aircraft was successfully landed and parked; false otherwise
     */
    public boolean tryLandAircraft() {
        // the aircraft at the front of the landing queue
        Aircraft landingAircraft = this.landingQueue.peekAircraft();
        if (landingAircraft == null) {
            // there are no aircraft in the landing queue waiting to land
            return false;
        }
        try {
            // park the aircraft to the suitable gate
            findUnoccupiedGate(landingAircraft).parkAircraft(landingAircraft);

            // remove the aircraft from the landing queue
            // and unload the aircraft's passengers or freight immediately
            this.landingQueue.removeAircraft().unload();

            // the landed aircraft should move on to the next task
            landingAircraft.getTaskList().moveToNextTask();
            return true;

        } catch (NoSuitableGateException | NoSpaceException ex) {
            // return false if there is no suitable gate for landing
            // NoSpaceException would not be thrown
            return false;
        }
    }

    /**
     * Attempts to allow one aircraft waiting in the takeoff queue to take off.
     * If there are no aircraft waiting in the takeoff queue, then the method should return.
     * Otherwise, the aircraft at the front of the takeoff queue should be removed from the
     * queue and it should move to the next task in its task list.
     */
    public void tryTakeOffAircraft() {
        // remove the aircraft at the front of the takeoff queue if any
        Aircraft takeoffAircraft = this.takeoffQueue.removeAircraft();
        if (takeoffAircraft == null) {
            // there are no aircraft waiting in the takeoff queue
            return;
        }
        // the takeoff aircraft should move on to the next task
        takeoffAircraft.getTaskList().moveToNextTask();
    }

    /**
     * Updates the time remaining to load on all currently loading aircraft and removes aircraft
     * from their gate once finished loading.
     * Any aircraft in the loading map should have their time remaining decremented by one tick.
     * If any aircraft's time remaining is now zero, it has finished loading and should be removed
     * from the loading map. Additionally, it should leave the gate it is parked at and should move
     * on to its next task.
     */
    public void loadAircraft() {
        // a list storing the aircraft that have zero time remaining after updated
        List<Aircraft> loadedAircraft = new ArrayList<>();

        for (Aircraft aircraft : loadingAircraft.keySet()) {
            if (loadingAircraft.get(aircraft) - 1 == 0) {
                loadedAircraft.add(aircraft);
            } else {
                // updates the time remaining to load
                loadingAircraft.put(aircraft, loadingAircraft.get(aircraft) - 1);
            }
        }
        for (Aircraft aircraft : loadedAircraft) {
            // remove from the loading map
            loadingAircraft.remove(aircraft);
            // leave the gate it is parked at
            findGateOfAircraft(aircraft).aircraftLeaves();
            // move on to its next task
            aircraft.getTaskList().moveToNextTask();
        }
    }

    /**
     * Calls placeAircraftInQueues(Aircraft) on all aircraft managed by the control tower
     */
    public void placeAllAircraftInQueues() {
        for (Aircraft aircraft : allAircraft) {
            placeAircraftInQueues(aircraft);
        }
    }

    /**
     * Moves the given aircraft to the appropriate queue based on its current task.
     * 1) If the aircraft's current task type is LAND and the landing queue does not already
     * contain the aircraft, it should be added to the landing queue.
     * 2) If the aircraft's current task type is TAKEOFF and the takeoff queue does not already
     * contain the aircraft, it should be added to the takeoff queue.
     * 3) If the aircraft's current task type is LOAD and the loading map does not already contain
     * the aircraft, it should be added to the loading map with an associated value of
     * Aircraft.getLoadingTime() (this is the number of ticks it will remain in the loading phase).
     *
     * @param aircraft aircraft to move to appropriate queue
     */
    public void placeAircraftInQueues(Aircraft aircraft) {
        // the type of the aircraft's current task
        TaskType currentTaskType = aircraft.getTaskList().getCurrentTask().getType();

        if (currentTaskType == TaskType.LAND && (!(landingQueue.containsAircraft(aircraft)))) {
            // add to the landing queue
            landingQueue.addAircraft(aircraft);
        } else if (currentTaskType == TaskType.TAKEOFF
                && (!(takeoffQueue.containsAircraft(aircraft)))) {
            // add to the takeoff queue
            takeoffQueue.addAircraft(aircraft);
        } else if (currentTaskType == TaskType.LOAD && (!(loadingAircraft.containsKey(aircraft)))) {
            // add to the loading map with an associated value of loading time
            loadingAircraft.put(aircraft, aircraft.getLoadingTime());
        }
    }

    /**
     * Advances the simulation by one tick.
     * On each tick, the control tower should perform the following actions:
     * 1) Call Aircraft.tick() on all aircraft.
     * 2) Move all aircraft with a current task type of AWAY or WAIT to their next task.
     * 3) Process loading aircraft by calling loadAircraft().
     * 4) On every second tick, attempt to land an aircraft by calling tryLandAircraft().
     *    If an aircraft cannot be landed, attempt to allow an aircraft to take off instead by
     *    calling tryTakeOffAircraft(). Note that this begins from the second time tick() is called
     *    and every second tick thereafter.
     * 5) If this is not a tick where the control tower is attempting to land an aircraft, an
     *    aircraft should be allowed to take off instead. This ensures that aircraft wishing to
     *    take off and land are given an equal share of the runway.
     * Place all aircraft in their appropriate queues by calling placeAllAircraftInQueues().
     */
    @Override
    public void tick() {
        // Call tick() on all other sub-entities
        for (Aircraft aircraft : this.allAircraft) {
            aircraft.tick();
            // move all aircraft with a current task type of AWAY or WAIT to their next task
            if (aircraft.getTaskList().getCurrentTask().getType() == TaskType.AWAY
                || aircraft.getTaskList().getCurrentTask().getType() == TaskType.WAIT) {
                aircraft.getTaskList().moveToNextTask();
            }
        }
        // Process loading aircraft
        loadAircraft();

        // for each tick() method be called, the number of total ticks elapsed should be added by 1
        this.totalTicksElapsed++;

        // On every second tick, attempt to land an aircraft
        if (getTicksElapsed() % 2 == 0) {
            // if an aircraft cannot be landed, attempt to allow an aircraft to take off instead
            if (!tryLandAircraft()) {
                tryTakeOffAircraft();
            }
        } else {
            // If this is not a tick where the control tower is attempting to land an aircraft,
            // an aircraft should be allowed to take off instead
            tryTakeOffAircraft();
        }
        // place all aircraft in their appropriate queues
        placeAllAircraftInQueues();

    }

    /**
     * Returns the human-readable string representation of this control tower.
     * The format of the string to return is:
     * ControlTower: numTerminals terminals, numAircraft total aircraft (numLanding LAND,
     * numTakeoff TAKEOFF, numLoad LOAD)
     *
     * @return string representation of this control tower
     */
    @Override
    public String toString() {
        return String.format("ControlTower: %d terminals, %d total aircraft "
                        + "(%d LAND, %d TAKEOFF, %d LOAD)",
                allTerminals.size(),
                allAircraft.size(),
                landingQueue.getAircraftInOrder().size(),
                takeoffQueue.getAircraftInOrder().size(),
                loadingAircraft.size());
    }
}
