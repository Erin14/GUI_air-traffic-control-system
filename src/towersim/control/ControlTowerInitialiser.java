package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;
import towersim.util.NoSpaceException;

import java.io.*;
import java.util.*;

/**
 * Utility class that contains static methods for loading a control tower and
 * associated entities from files.
 */
public class ControlTowerInitialiser {
    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * 1) The number of ticks elapsed is not an integer.
     * 2) The number of ticks elapsed is less than zero.
     *
     * @param reader reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static long loadTick(Reader reader) throws MalformedSaveException, IOException {
        try (BufferedReader tickRead = new BufferedReader(reader)) {
            long ticks = Long.parseLong(tickRead.readLine());
            if (ticks < 0) {
                // the number of ticks elapsed is less than zero.
                throw new MalformedSaveException();
            }
            return ticks;
        } catch (NumberFormatException ex) {
            // the number of ticks elapsed is not an integer
            throw new MalformedSaveException();
        }
    }

    /**
     * Loads the list of all aircraft managed by the control tower from the given reader instance.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * 1) The number of aircraft specified on the first line of the reader is not an integer.
     * 2) The number of aircraft specified on the first line is not equal to the number of aircraft
     *    actually read from the reader.
     * 3) Any of the conditions listed in the Javadoc for readAircraft(String) are true.
     *
     * @param reader reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     */
    public static List<Aircraft> loadAircraft(Reader reader)
            throws IOException, MalformedSaveException {
        // stores aircraft read from the reader
        List<Aircraft> allAircraft = new ArrayList<>();

        try (BufferedReader aircraftReader = new BufferedReader(reader)) {
            int numAircraft = tryParse(aircraftReader.readLine());
            String nextLine;
            while ((nextLine = aircraftReader.readLine()) != null) {
                allAircraft.add(readAircraft(nextLine));
            }
            // check whether the number of aircraft specified on the first line is equal to
            // the number of aircraft actually read from the reader
            checkLength(allAircraft.size(), numAircraft);
        }
        return allAircraft;
    }

    /**
     * Loads the takeoff queue, landing queue and map of loading aircraft from the given reader
     * instance.
     *
     * @param reader reader from which to load the queues and loading map
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue empty takeoff queue that aircraft will be added to
     * @param landingQueue empty landing queue that aircraft will be added to
     * @param loadingAircraft empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(Reader reader, List<Aircraft> aircraft, TakeoffQueue takeoffQueue,
                                  LandingQueue landingQueue, Map<Aircraft, Integer> loadingAircraft)
            throws MalformedSaveException, IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        readQueue(bufferedReader, aircraft, takeoffQueue);
        readQueue(bufferedReader, aircraft, landingQueue);
        readLoadingAircraft(bufferedReader, aircraft, loadingAircraft);
        bufferedReader.close();
    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * 1) The number of terminals specified at the top of the file is not an integer.
     * 2) The number of terminals specified is not equal to the number of terminals actually
     *    read from the reader.
     *
     * @param reader reader from which to load the list of terminals and their gates
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft)
            throws MalformedSaveException, IOException {
        // stores terminals read from the reader
        List<Terminal> terminals = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            int numTerminals = tryParse(bufferedReader.readLine());
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                terminals.add(readTerminal(nextLine, bufferedReader, aircraft));
            }
            // check whether the number of terminals specified is equal to
            // the number of terminals actually read from the reader or not
            checkLength(terminals.size(), numTerminals);
        }
        return terminals;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     *
     * @param tick reader from which to load the number of ticks elapsed
     * @param aircraft reader from which to load the list of aircraft
     * @param queues reader from which to load the aircraft queues and map of loading aircraft
     * @param terminalsWithGates reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException if reading from any of the given readers results in a
     *                                MalformedSaveException, indicating the contents of that
     *                                reader are invalid
     * @throws IOException if an IOException is encountered when reading from any of the readers
     */
    public static ControlTower createControlTower(Reader tick, Reader aircraft,
                                                  Reader queues, Reader terminalsWithGates)
            throws MalformedSaveException, IOException {
        long numTicks = loadTick(tick);
        List<Aircraft> allAircraft = loadAircraft(aircraft);
        List<Terminal> allTerminals = loadTerminalsWithGates(terminalsWithGates, allAircraft);
        // initialize landing queue, takeoff queue and map of loading aircraft before load queues
        LandingQueue landingQueue = new LandingQueue();
        TakeoffQueue takeoffQueue = new TakeoffQueue();
        Map<Aircraft, Integer> loadingAircraft = new
                TreeMap<>(Comparator.comparing(Aircraft::getCallsign));
        loadQueues(queues, allAircraft, takeoffQueue, landingQueue, loadingAircraft);

        ControlTower controlTower = new ControlTower(numTicks, allAircraft, landingQueue,
                takeoffQueue, loadingAircraft);
        for (Terminal terminal : allTerminals) {
            controlTower.addTerminal(terminal);
        }
        return controlTower;
    }

    /**
     * Reads an aircraft from its encoded representation in the given string.
     * If the AircraftCharacteristics.passengerCapacity of the encoded aircraft is greater than
     * zero, then a PassengerAircraft should be created and returned. Otherwise, a FreightAircraft
     * should be created and returned.
     *
     * The encoded string is invalid if any of the following conditions are true:
     * 1) More/fewer colons (:) are detected in the string than expected.
     * 2) The aircraft's AircraftCharacteristics is not valid.
     * 3) The aircraft's fuel amount is not a double.
     * 4) The aircraft's fuel amount is less than zero or greater than the aircraft's maximum
     *    fuel capacity.
     * 5) The amount of cargo (freight/passengers) onboard the aircraft is not an integer.
     * 6) The amount of cargo (freight/passengers) onboard the aircraft is less than zero or
     *    greater than the aircraft's maximum freight/passenger capacity.
     * 7) Any of the conditions listed in the Javadoc for readTaskList(String) are true.
     *
     * @param line line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException if the format of the given string is invalid
     */
    public static Aircraft readAircraft(String line) throws MalformedSaveException {
        String[] aircraftInformation = line.split(":", 6);
        // throw an exception if fewer colons are detected in the string than expected
        checkLength(aircraftInformation.length, 6);

        String callsign = aircraftInformation[0];
        TaskList taskList = readTaskList(aircraftInformation[2]);
        boolean emergencyState = Boolean.parseBoolean(aircraftInformation[4]);
        int cargoAmount = tryParse(aircraftInformation[5]);

        Aircraft aircraftRead;

        double fuelAmount;
        try {
            fuelAmount = Double.parseDouble(aircraftInformation[3]);
        } catch (NumberFormatException ex) {
            // the aircraft's fuel amount is not a double
            throw new MalformedSaveException();
        }

        try {
            AircraftCharacteristics aircraftCharacteristics =
                    AircraftCharacteristics.valueOf(aircraftInformation[1]);

            if (aircraftCharacteristics.passengerCapacity > 0) {
                aircraftRead = new PassengerAircraft(callsign, aircraftCharacteristics,
                        taskList, fuelAmount, cargoAmount);
            } else {
                aircraftRead = new FreightAircraft(callsign, aircraftCharacteristics,
                        taskList, fuelAmount, cargoAmount);
            }
            if (emergencyState) {
                aircraftRead.declareEmergency();
            }
            return aircraftRead;

        } catch (IllegalArgumentException ex) {
            // 1) the aircraft's fuel amount is less than zero or greater than the aircraft's
            //    maximum fuel capacity, or
            // 2) the amount of cargo (freight/passengers) onboard the aircraft is less than
            //    zero or greater than the aircraft's maximum freight/passenger capacity, or
            // 3) the aircraft's AircraftCharacteristics is not valid
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads a task list from its encoded representation in the given string.
     * The encoded string is invalid if any of the following conditions are true:
     * 1) The task list's TaskType is not valid.
     * 2) A task's load percentage is not an integer.
     * 3) A task's load percentage is less than zero.
     * 4) More than one at-symbol (@) is detected for any task in the task list.
     * 5) The task list is invalid according to the rules specified in TaskList(List).
     *
     * @param taskListPart string containing the encoded task list
     * @return decoded task list instance
     * @throws MalformedSaveException if the format of the given string is invalid
     */
    public static TaskList readTaskList(String taskListPart) throws MalformedSaveException {
        String[] allTasks = taskListPart.split(",");
        // stores the tasks read from the string
        List<Task> tasks = new ArrayList<>();
        for (String task : allTasks) {
            tasks.add(readTask(task));
        }
        try {
            return new TaskList(tasks);
        } catch (IllegalArgumentException ex) {
            // the task list is invalid according to the rules specified in TaskList(List).
            throw new MalformedSaveException();
        }
    }

    /**
     * Read a task encoded representation in the given string, and return the related task.
     *
     * @param taskPart string containing the encoded task
     * @return decoded task instance
     * @throws MalformedSaveException if the format of the given string is invalid
     */
    private static Task readTask(String taskPart) throws MalformedSaveException {
        String[] taskSeparate = taskPart.split("@", 2);
        int loadPercent = 0;

        if (taskSeparate.length == 2) {
            // has at least one "@" symbol, check the load percentage is an integer
            // and not less than 0
            loadPercent = tryParse(taskSeparate[1]);
            checkLessThan(0, loadPercent);
        }

        try {
            return new Task(TaskType.valueOf(taskSeparate[0]),
                    taskSeparate[0].equals("LOAD") ? loadPercent : 0);
        } catch (IllegalArgumentException ex) {
            // the task type is not one of those listed in TaskType.values()
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads an aircraft queue from the given reader instance.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * 1) The first line read from the reader is null.
     * 2) The first line contains more/fewer colons (:) than expected.
     * 3) The queue type specified in the first line is not equal to the simple class name of the
     *    queue provided as a parameter.
     * 4) The number of aircraft specified on the first line is not an integer.
     * 5) The number of aircraft specified is greater than zero and the second line read is null.
     * 6) The number of callsigns listed on the second line is not equal to the number of aircraft
     *    specified on the first line.
     * 7) A callsign listed on the second line does not correspond to the callsign of any aircraft
     *    contained in the list of aircraft given as a parameter.
     *
     * @param reader reader from which to load the aircraft queue
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param queue empty queue that aircraft will be added to
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     */
    public static void readQueue(BufferedReader reader,
                                  List<Aircraft> aircraft, AircraftQueue queue)
            throws IOException, MalformedSaveException {
        // the number of aircraft (information extract from the first line)
        int numAircraft = readQueueFirstLine(reader.readLine(), queue);

        if (numAircraft > 0) {
            try {
                String[] callsigns = reader.readLine().split(",", numAircraft);
                // check the number of callsigns listed on the second line is not less than
                // the number of aircraft specified on the first line
                checkLength(callsigns.length, numAircraft);

                for (String callsign : callsigns) {
                    for (Aircraft anAircraft : aircraft) {
                        if (anAircraft.getCallsign().equals(callsign)) {
                            queue.addAircraft(anAircraft);
                            break;
                        }
                    }
                }
                // check if all callsign listed on the second line corresponds to the
                // callsign of any aircraft contained in the given list of aircraft
                checkLength(queue.getAircraftInOrder().size(), callsigns.length);

            } catch (NullPointerException nullPointerException) {
                // the number of aircraft specified is greater than zero but
                // the second line read is null
                throw new MalformedSaveException();
            }
        }
    }

    /**
     * Reads the first line of an aircraft queue's encode (takeoff queue and landing queue)
     * or a loading map's encode (if the queue parameter is null), return the number of aircraft.
     *
     * @param line the first line of an aircraft queue's encode
     * @param queue an empty queue that aircraft will be added to; null for the loading map
     * @return the number of aircraft (extract from the line)
     * @throws MalformedSaveException if the format of the line is invalid
     */
    private static int readQueueFirstLine(String line, AircraftQueue queue)
            throws MalformedSaveException {
        try {
            String[] queueInformation = line.split(":", 2);
            // check if the first line contains fewer colons than expected
            checkLength(queueInformation.length, 2);

            if ((queue != null) && !(queueInformation[0]
                    .equals(queue.getClass().getSimpleName()))) {
                // the queue type is not equal to the simple class name of the queue provided
                // (for landing queue and takeoff queue)
                throw new MalformedSaveException();
            }
            return tryParse(queueInformation[1]);
        } catch (NullPointerException ex) {
            // the first line is null
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads the map of currently loading aircraft from the given reader instance.
     * The contents read from the reader are invalid if any of the following conditions are true:
     * 1) The first line read from the reader is null.
     * 2) The number of colons detected on the first line is more/fewer than expected.
     * 3) The number of aircraft specified on the first line is not an integer.
     * 4) The number of aircraft is greater than 0 and the second line read from the reader is null.
     * 5) The number of aircraft specified on the first line is not equal to the number of callsigns
     *    read on the second line.
     * 6) For any callsign/loading time pair on the second line, the number of colons detected is
     *    not equal to one.
     * 7) A callsign listed on the second line does not correspond to the callsign of any aircraft
     *    contained in the list of aircraft given as a parameter.
     * 8) Any ticksRemaining value on the second line is not an integer.
     * 9) Any ticksRemaining value on the second line is less than one.
     *
     * @param reader reader from which to load the map of loading aircraft
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param loadingAircraft empty map that aircraft and their loading times will be added to
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     */
    public static void readLoadingAircraft(BufferedReader reader, List<Aircraft> aircraft,
                                            Map<Aircraft, Integer> loadingAircraft)
            throws IOException, MalformedSaveException {
        // the number of aircraft (information extract from the first line)
        int numAircraft = readQueueFirstLine(reader.readLine(), null);

        if (numAircraft > 0) {
            try {
                String[] allAircraftInformation = reader.readLine().split(",", numAircraft);
                // check the number of aircraft specified on the first line is not less than the
                // number of callsigns read on the second line or not
                checkLength(allAircraftInformation.length, numAircraft);

                for (String eachAircraft : allAircraftInformation) {
                    String[] aircraftInformation = eachAircraft.split(":", 2);
                    // throw an exception if the number of colons detected is less than one
                    checkLength(aircraftInformation.length, 2);

                    int loadingTime = tryParse(aircraftInformation[1]);
                    checkLessThan(1, loadingTime);
                    for (Aircraft anAircraft : aircraft) {
                        if (aircraftInformation[0].equals(anAircraft.getCallsign())) {
                            loadingAircraft.put(anAircraft, loadingTime);
                            break;
                        }
                    }
                }
                // check all callsigns listed on the second line corresponds to
                // a callsign of any aircraft contained in the list of aircraft
                // given as a parameter
                checkLength(loadingAircraft.size(), allAircraftInformation.length);

            } catch (NullPointerException ex) {
                // the number of aircraft is greater than 0 but the second line is null
                throw new MalformedSaveException();
            }
        }
    }

    /**
     * Reads a terminal from the given string and reads its gates from the given reader instance.
     * The encoded terminal is invalid if any of the following conditions are true:
     * 1）The number of colons (:) detected on the first line is more/fewer than expected.
     * 2）The terminal type specified on the first line is neither AirplaneTerminal nor
     *    HelicopterTerminal.
     * 3）The terminal number is not an integer.
     * 4）The terminal number is less than one (1).
     * 5）The number of gates in the terminal is not an integer.
     * 6）The number of gates is less than zero or is greater than Terminal.MAX_NUM_GATES.
     * 7）A line containing an encoded gate was expected, but EOF (end of file) was received.
     * 8）Any of the conditions listed in the Javadoc for readGate(String, List) are true.
     *
     * @param line string containing the first line of the encoded terminal
     * @param reader reader from which to load the gates of the terminal (subsequent lines)
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded terminal with its gates added
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the given string or the text read from
     *                                the reader is invalid
     */
    public static Terminal readTerminal(String line, BufferedReader reader, List<Aircraft> aircraft)
            throws IOException, MalformedSaveException {
        String[] terminalInformation = line.split(":", 4);
        // throw an exception if the number of colons detected is fewer than expected
        checkLength(terminalInformation.length, 4);

        int terminalNumber = tryParse(terminalInformation[1]);
        // throw an exception if the terminal number is less than 1
        checkLessThan(1, terminalNumber);

        int numGates = tryParse(terminalInformation[3]);
        // throw an exception if the number of gates is less than zero or
        // is greater than the maximum number of gates
        checkLessThan(numGates, Terminal.MAX_NUM_GATES);
        checkLessThan(0, numGates);

        Terminal terminalRead;
        if (terminalInformation[0].equals("AirplaneTerminal")) {
            terminalRead = new AirplaneTerminal(terminalNumber);
        } else if (terminalInformation[0].equals("HelicopterTerminal")) {
            terminalRead = new HelicopterTerminal(terminalNumber);
        } else {
            // the terminal type specified on the first line is neither AirplaneTerminal
            // nor HelicopterTerminal
            throw new MalformedSaveException();
        }

        if (Boolean.parseBoolean(terminalInformation[2])) {
            // the terminal is in an emergency state
            terminalRead.declareEmergency();
        }

        for (int i = 0; i < numGates; i++) {
            try {
                String gateLine = reader.readLine();
                terminalRead.addGate(readGate(gateLine, aircraft));
            } catch (NullPointerException | NoSpaceException ex) {
                // a line containing an encoded gate was expected, but the end of file was received
                throw new MalformedSaveException();
                // NoSpaceException won't be thrown
            }
        }

        return terminalRead;
    }

    /**
     * Reads a gate from its encoded representation in the given string.
     * The encoded string is invalid if any of the following conditions are true:
     * 1) The number of colons (:) detected was more/fewer than expected.
     * 2) The gate number is not an integer.
     * 3) The gate number is less than one (1).
     * 4) The callsign of the aircraft parked at the gate is not empty and the callsign
     *    does not correspond to the callsign of any aircraft contained in the list of
     *    aircraft given as a parameter.
     *
     * @param line string containing the encoded gate
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded gate instance
     * @throws MalformedSaveException if the format of the given string is invalid
     */
    public static Gate readGate(String line, List<Aircraft> aircraft)
            throws MalformedSaveException {
        String[] gateInformation = line.split(":", 2);
        // throw an exception if the number of colons (:) detected was fewer than expected
        checkLength(gateInformation.length, 2);

        int gateNumber = tryParse(gateInformation[0]);
        // throw an exception if the gate number is less than 1
        checkLessThan(1, gateNumber);

        Gate gate = new Gate(gateNumber);

        String callsign = gateInformation[1];
        if (!(callsign.equals("empty"))) {
            for (Aircraft eachAircraft : aircraft) {
                if (eachAircraft.getCallsign().equals(callsign)) {
                    try {
                        gate.parkAircraft(eachAircraft);
                    } catch (NoSpaceException ignored) {
                        // the exception will never be thrown in this case
                    }
                    break;
                }
            }
            if (!gate.isOccupied()) {
                // the callsign does not correspond to the callsign of
                // any aircraft contained in the list of aircraft
                throw new MalformedSaveException();
            }
        }
        return gate;
    }

    /**
     * Throw a MalformedSaveException if the first parameter is larger than the second parameter.
     *
     * @param smallerNumber the number that is expected to be the smaller one
     * @param largerNumber the number that is expected to be the larger one
     * @throws MalformedSaveException if the first parameter is larger than the second parameter
     */
    private static void checkLessThan(int smallerNumber, int largerNumber)
            throws MalformedSaveException {
        if (smallerNumber > largerNumber) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Try parse a string to an integer, thrown an MalformedSaveException if it is not an integer.
     *
     * @param expectedNum the string needed to be parse
     * @return an integer parse from the given string
     * @throws MalformedSaveException if the given string cannot be parsed to an integer
     */
    private static int tryParse(String expectedNum) throws MalformedSaveException {
        try {
            return Integer.parseInt(expectedNum);
        } catch (NumberFormatException ex) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Check if the given real length is equal to the given expected length or not.
     * Throw a MalformedSaveException if they are not equal.
     *
     * @param realLength the real length of an array or a list
     * @param expectedLength the expected length of the array or a list
     * @throws MalformedSaveException if two parameters are not equal
     */
    private static void checkLength(int realLength, int expectedLength)
            throws MalformedSaveException {
        if (realLength != expectedLength) {
            throw new MalformedSaveException();
        }
    }
}
