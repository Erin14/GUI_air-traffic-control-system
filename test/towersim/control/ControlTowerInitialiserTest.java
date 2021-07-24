package towersim.control;

import org.junit.Before;
import org.junit.Test;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class ControlTowerInitialiserTest {

    private ControlTower tower;
    String fileContents;

    Aircraft aircraft1;
    Aircraft aircraft2;
    Aircraft aircraft3;
    Aircraft aircraft4;

    TakeoffQueue takeoffQueue;
    LandingQueue landingQueue;
    Map<Aircraft, Integer> loadingAircraft;

    List<Aircraft> fullAircraftList = new ArrayList<>();

    List<Task> tasks1;
    List<Task> tasks2;
    List<Task> tasks3;
    List<Task> tasks4;


    @Before
    public void setUp() {

        tasks1 = new ArrayList<>();
        tasks1.add(new Task(TaskType.AWAY));
        tasks1.add(new Task(TaskType.AWAY));
        tasks1.add(new Task(TaskType.LAND));
        tasks1.add(new Task(TaskType.WAIT));
        tasks1.add(new Task(TaskType.WAIT));
        tasks1.add(new Task(TaskType.LOAD, 60));
        tasks1.add(new Task(TaskType.TAKEOFF));
        tasks1.add(new Task(TaskType.AWAY));

        this.aircraft1 = new PassengerAircraft("QFA481", AircraftCharacteristics.AIRBUS_A320,
                new TaskList(tasks1), 10000.00, 132);
        fullAircraftList.add(this.aircraft1);

        tasks4 = new ArrayList<>();
        tasks4.add(new Task(TaskType.LAND));
        tasks4.add(new Task(TaskType.WAIT));
        tasks4.add(new Task(TaskType.LOAD, 75));
        tasks4.add(new Task(TaskType.TAKEOFF));
        tasks4.add(new Task(TaskType.AWAY));
        tasks4.add(new Task(TaskType.AWAY));

        this.aircraft4 = new PassengerAircraft("VH-BFK", AircraftCharacteristics.ROBINSON_R44,
                new TaskList(tasks4), 40.00, 4);
        fullAircraftList.add(this.aircraft4);

        tasks2 = new ArrayList<>();
        tasks2.add(new Task(TaskType.WAIT));
        tasks2.add(new Task(TaskType.LOAD, 100));
        tasks2.add(new Task(TaskType.TAKEOFF));
        tasks2.add(new Task(TaskType.AWAY));
        tasks2.add(new Task(TaskType.AWAY));
        tasks2.add(new Task(TaskType.AWAY));
        tasks2.add(new Task(TaskType.LAND));

        this.aircraft2 = new PassengerAircraft("UTD302", AircraftCharacteristics.BOEING_787,
                new TaskList(tasks2), 10000.00, 0);
        fullAircraftList.add(this.aircraft2);

        tasks3 = new ArrayList<>();
        tasks3.add(new Task(TaskType.WAIT));
        tasks3.add(new Task(TaskType.LOAD, 50));
        tasks3.add(new Task(TaskType.TAKEOFF));
        tasks3.add(new Task(TaskType.AWAY));
        tasks3.add(new Task(TaskType.AWAY));
        tasks3.add(new Task(TaskType.AWAY));
        tasks3.add(new Task(TaskType.LAND));

        this.aircraft3 = new FreightAircraft("UPS119", AircraftCharacteristics.BOEING_747_8F,
                new TaskList(tasks3), 4000.00, 0);
        fullAircraftList.add(this.aircraft3);

        takeoffQueue = new TakeoffQueue();
        landingQueue = new LandingQueue();
        loadingAircraft = new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest1() throws IOException, MalformedSaveException {
        fileContents = "";
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest2() throws IOException, MalformedSaveException {
        fileContents = "-1";
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest3() throws IOException, MalformedSaveException {
        fileContents = "1.2";
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest4() throws IOException, MalformedSaveException {
        fileContents = "ABC";
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest5() throws IOException, MalformedSaveException {
        fileContents = String.join(System.lineSeparator(), "B", "1", "2");
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test
    public void tickTest6() throws IOException {
        fileContents = String.join(System.lineSeparator(), "0", "1", "2");
        try {
            assertEquals(0, ControlTowerInitialiser.loadTick(new StringReader(fileContents)));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void tickTest7() throws IOException {
        fileContents = String.join(System.lineSeparator(), "0", "B", "2");
        try {
            assertEquals(0, ControlTowerInitialiser.loadTick(new StringReader(fileContents)));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void tickTest8() throws IOException {
        fileContents = "1";
        try {
            assertEquals(1, ControlTowerInitialiser.loadTick(new StringReader(fileContents)));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void tickTest9() throws IOException {
        fileContents = "012";
        try {
            assertEquals(12, ControlTowerInitialiser.loadTick(new StringReader(fileContents)));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void tickTest10() throws IOException {
        fileContents = "012";
        try {
            assertEquals(12, ControlTowerInitialiser.loadTick(new StringReader(fileContents)));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test(expected = MalformedSaveException.class)
    public void tickTest11() throws IOException, MalformedSaveException {
        fileContents = "0 A";
        ControlTowerInitialiser.loadTick(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void aircraftTest2() throws IOException, MalformedSaveException {
        fileContents = "A";
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void aircraftTest3() throws IOException, MalformedSaveException {
        fileContents = "0.2";
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void aircraftTest4() throws IOException, MalformedSaveException {
        fileContents = String.join(System.lineSeparator(), "0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void aircraftTest5() throws IOException, MalformedSaveException {
        fileContents = "1";
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test(expected = MalformedSaveException.class)
    public void aircraftTest6() throws IOException, MalformedSaveException {
        fileContents = String.join(System.lineSeparator(), "2",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0");
        ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
    }

    @Test
    public void aircraftTest7() throws IOException {

        fileContents = String.join(System.lineSeparator(), "3",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            List<Aircraft> lists = new ArrayList<>();
            lists.add(aircraft2);
            lists.add(aircraft3);
            lists.add(aircraft4);

            assertEquals(lists, ControlTowerInitialiser.loadAircraft(new StringReader(fileContents)));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void loadQueues_Test1() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void loadQueues_Test2() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "VH-BFK",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            assertEquals(1, takeoffQueue.getAircraftInOrder().size());
            assertEquals(aircraft4, takeoffQueue.peekAircraft());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test(expected = MalformedSaveException.class)
    public void loadQueues_Test3() throws IOException, MalformedSaveException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "VH-BFK",
                "LandingQueue:0",
                "LoadingAircraft:0");
        ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
    }

    @Test
    public void loadQueues_Test4() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(1, landingQueue.getAircraftInOrder().size());
            assertEquals(aircraft4, landingQueue.peekAircraft());
            assertEquals(0, loadingAircraft.size());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void loadQueues_Test5() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:0",
                "LoadingAircraft:1",
                "VH-BFK:3");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(1, loadingAircraft.size());
            assertEquals(3, (int) loadingAircraft.get(aircraft4));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void loadQueues_Test6() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "LandingQueue:0",
                "TakeoffQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test7() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0:",
                "TakeoffQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test8() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:)",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test9() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test10() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "VH-BFK",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test11() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:1",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:0",
                "LoadingAircraft:1");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), fullAircraftList,
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadQueues_Test12() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "LandingQueue:0",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents), new ArrayList<>(),
                    takeoffQueue, landingQueue, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
            assertEquals(0, landingQueue.getAircraftInOrder().size());
            assertEquals(0, loadingAircraft.size());
        }
    }

    @Test
    public void loadTerminalsWithGates_Test0() throws IOException {
        fileContents = "0";
        try {
            List<Terminal> except = new ArrayList<>();
            assertEquals(except,
                    ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList));
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void loadTerminalsWithGates_Test1() throws IOException {
        fileContents = "B";
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test2() throws IOException {
        fileContents = "1.2";
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test3() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "0",
                "AirplaneTerminal:1:false:1",
                "1:empty");
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test4() throws IOException {
        fileContents = "1";
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test5() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "4",
                "AirplaneTerminal:1:false:1",
                "1:empty",
                "HelicopterTerminal:2:false:2",
                "7:empty",
                "8:empty",
                "AirplaneTerminal:3:false:1",
                "13:UPS119",
                "HelicopterTerminal:4:true:0");

        Terminal terminal1 = new AirplaneTerminal(1);
        Terminal terminal2 = new HelicopterTerminal(2);
        Terminal terminal3 = new AirplaneTerminal(3);
        Terminal terminal4 = new HelicopterTerminal(4);

        try {
            terminal1.addGate(new Gate(1));
            terminal2.addGate(new Gate(7));
            terminal2.addGate(new Gate(8));
            Gate gate1 = new Gate(13);
            gate1.parkAircraft(aircraft3);
            terminal3.addGate(gate1);
            terminal4.declareEmergency();
        } catch (NoSpaceException ignored) {}


        List<Terminal> expected = new ArrayList<>();
        expected.add(terminal1);
        expected.add(terminal2);
        expected.add(terminal3);
        expected.add(terminal4);

        try {
            assertEquals(expected, ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void loadTerminalsWithGates_Test6() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "3",
                "AirplaneTerminal:1:false:1",
                "1:empty",
                "HelicopterTerminal:2:false:2",
                "7:empty",
                "8:empty",
                "AirplaneTerminal:3:false:1",
                "13:UPS119",
                "HelicopterTerminal:4:true:0");
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test7() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "4",
                "AirplaneTerminal:1:false:1",
                "1:empty",
                "HelicopterTerminal:2:false:2",
                "7:empty",
                "8:empty",
                "AirplaneTerminal:3:false:0",
                "13:UPS119",
                "HelicopterTerminal:4:true:0");
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void loadTerminalsWithGates_Test8() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "4",
                "AirplaneTerminal:1:false:1",
                "1:empty",
                "HelicopterTerminal:2:false:2",
                "7:empty",
                "8:empty",
                "AirplaneTerminal:3:false:1",
                "13:UPS119",
                "HelicopterTerminal:4:true:0");
        try {
            ControlTowerInitialiser.loadTerminalsWithGates(new StringReader(fileContents), new ArrayList<>());
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void createControlTower_Test10() throws IOException {
        String aircraft_default = "0";
        String queues_default = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:0",
                "LoadingAircraft:0");
        String terminalsWithGates_default = "0";
        String tick_default = "0";
        ControlTower expected = new ControlTower(0, new ArrayList<>(), new LandingQueue(),
                new TakeoffQueue(), new TreeMap<>(Comparator.comparing(Aircraft::getCallsign)));

        try {
            tower = ControlTowerInitialiser.createControlTower(new StringReader(tick_default),
                    new StringReader(aircraft_default), new StringReader(queues_default), new StringReader(terminalsWithGates_default));
            assertEquals(expected.toString(), tower.toString());
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void createControlTower_Test11() throws IOException {
        String aircraft_default = "0";
        String queues_default = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:1",
                "LoadingAircraft:0");
        String terminalsWithGates_default = "0";
        String tick_default = "0";

        try {
            tower = ControlTowerInitialiser.createControlTower(new StringReader(tick_default),
                    new StringReader(aircraft_default), new StringReader(queues_default), new StringReader(terminalsWithGates_default));
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void createControlTower_Test12() throws IOException {
        String aircraft_default = "0";
        String queues_default = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:2",
                "VH-BFK",
                "LoadingAircraft:0");
        String terminalsWithGates_default = "0";
        String tick_default = "0";

        try {
            tower = ControlTowerInitialiser.createControlTower(new StringReader(tick_default),
                    new StringReader(aircraft_default), new StringReader(queues_default), new StringReader(terminalsWithGates_default));
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void createControlTower_Test20() throws IOException {
        String aircraft_default = String.join(System.lineSeparator(),
                "4",
                "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132",
                "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0",
                "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        String queues_default = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:0");
        String terminalsWithGates_default = "5\n" +
                "AirplaneTerminal:1:false:6\n" +
                "1:UTD302\n" +
                "2:empty\n" +
                "3:empty\n" +
                "4:empty\n" +
                "5:empty\n" +
                "6:empty\n" +
                "HelicopterTerminal:2:false:5\n" +
                "7:empty\n" +
                "8:empty\n" +
                "9:empty\n" +
                "10:empty\n" +
                "11:empty\n" +
                "AirplaneTerminal:3:false:2\n" +
                "12:empty\n" +
                "13:UPS119\n" +
                "HelicopterTerminal:4:true:0\n" +
                "HelicopterTerminal:5:false:0";
        String terminal1 = "1:UTD302\n" +
                "2:empty\n" +
                "3:empty\n" +
                "4:empty\n" +
                "5:empty\n" +
                "6:empty";
        String terminal2 = "7:empty\n" +
                "8:empty\n" +
                "9:empty\n" +
                "10:empty\n" +
                "11:empty";
        String terminal3 = "12:empty\n" +
                "13:UPS119";

        String tick_default = "5";
        LandingQueue expectedLandingQueue = new LandingQueue();
        expectedLandingQueue.addAircraft(aircraft4);
        ControlTower expected = new ControlTower(5, fullAircraftList, expectedLandingQueue,
                new TakeoffQueue(), new TreeMap<>(Comparator.comparing(Aircraft::getCallsign)));
        try {
            expected.addTerminal(ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:false:6", new BufferedReader(new StringReader(terminal1)), fullAircraftList));
            expected.addTerminal(ControlTowerInitialiser.readTerminal("HelicopterTerminal:2:false:5", new BufferedReader(new StringReader(terminal2)), fullAircraftList));
            expected.addTerminal(ControlTowerInitialiser.readTerminal("AirplaneTerminal:3:false:2", new BufferedReader(new StringReader(terminal3)), fullAircraftList));
            expected.addTerminal(ControlTowerInitialiser.readTerminal("HelicopterTerminal:4:true:0", new BufferedReader(new StringReader(terminal3)), fullAircraftList));
            expected.addTerminal(ControlTowerInitialiser.readTerminal("HelicopterTerminal:5:false:0", new BufferedReader(new StringReader(terminal3)), fullAircraftList));

        } catch (MalformedSaveException ignored) {}

        try {
            tower = ControlTowerInitialiser.createControlTower(new StringReader(tick_default),
                    new StringReader(aircraft_default), new StringReader(queues_default), new StringReader(terminalsWithGates_default));
            assertEquals(expected.toString(), tower.toString());
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void readAircraft_Test1() {
        // passenger aircraft
        fileContents = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        try {
            assertEquals(aircraft1, ControlTowerInitialiser.readAircraft(fileContents));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readAircraft_Test2() {
        // fre aircraft
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        try {
            assertEquals(aircraft3, ControlTowerInitialiser.readAircraft(fileContents));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readAircraft_Test3() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0:";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test4() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false 0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test5() {
        fileContents = "UPS119:BOEING_747:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false 0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test6() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:226117:false:0";
        try {
            aircraft3 = new FreightAircraft("UPS119", AircraftCharacteristics.BOEING_747_8F,
                    new TaskList(tasks3), 226117.00, 0);
            assertEquals(aircraft3, ControlTowerInitialiser.readAircraft(fileContents));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readAircraft_Test7() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:0:false:0";
        try {
            aircraft3 = new FreightAircraft("UPS119", AircraftCharacteristics.BOEING_747_8F,
                    new TaskList(tasks3), 0, 0);
            assertEquals(aircraft3, ControlTowerInitialiser.readAircraft(fileContents));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readAircraft_Test8() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:226118:false:0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test9() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:-0.1:false:0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test10() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:B:false:0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test11() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND: :false:0";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test12() {
        fileContents = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132.1";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test13() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0.2";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test14() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:-1";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test15() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:137757";
        try {
            ControlTowerInitialiser.readAircraft(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readAircraft_Test16() {
        fileContents = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:137756";
        try {
            aircraft3 = new FreightAircraft("UPS119", AircraftCharacteristics.BOEING_747_8F,
                    new TaskList(tasks3), 4000, 137756);
            assertEquals(aircraft3, ControlTowerInitialiser.readAircraft(fileContents));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test1() {
        fileContents = "WAIT";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.WAIT));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test2() {
        fileContents = "WAIT,WAIT";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.WAIT));
            except.add(new Task(TaskType.WAIT));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test3() {
        fileContents = "AWAY";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test4() {
        fileContents = "AWAY,AWAY";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.AWAY));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test5() {
        fileContents = "";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test6() {
        fileContents = "AWAY,WAIT";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test7() {
        fileContents = "AWAY,LAND,WAIT,LOAD@60,TAKEOFF";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.LAND));
            except.add(new Task(TaskType.WAIT));
            except.add(new Task(TaskType.LOAD, 60));
            except.add(new Task(TaskType.TAKEOFF));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test8() {
        fileContents = "AWAY,LAND,WAIT,LOAD@60,TAKEOFF,TAKEOFF";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test9() {
        fileContents = "AWAY,LAND,WAIT,LOAD@60";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test10() {
        fileContents = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.LAND));
            except.add(new Task(TaskType.WAIT));
            except.add(new Task(TaskType.WAIT));
            except.add(new Task(TaskType.LOAD, 60));
            except.add(new Task(TaskType.TAKEOFF));
            except.add(new Task(TaskType.AWAY));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test11() {
        fileContents = "AWAY,LAND,LOAD@60,TAKEOFF";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.LAND));
            except.add(new Task(TaskType.LOAD, 60));
            except.add(new Task(TaskType.TAKEOFF));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test12() {
        fileContents = "AWAY,LAND,LOAD@60,TAKEOFF,AWAY";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.LAND));
            except.add(new Task(TaskType.LOAD, 60));
            except.add(new Task(TaskType.TAKEOFF));
            except.add(new Task(TaskType.AWAY));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test13() {
        fileContents = "AWAY,LAND,LOAD@60,WAIT,TAKEOFF,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test14() {
        fileContents = "AWAY,RANDOM,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test15() {
        fileContents = "AWAY,LAND,LOAD@0.6,TAKEOFF,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test16() {
        fileContents = "AWAY,LAND,LOAD@-1,TAKEOFF,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test17() {
        fileContents = "AWAY,LAND,LOAD@60,TAKEOFF@60,AWAY";
        try {
            List<Task> except = new ArrayList<>();
            except.add(new Task(TaskType.AWAY));
            except.add(new Task(TaskType.LAND));
            except.add(new Task(TaskType.LOAD, 60));
            except.add(new Task(TaskType.TAKEOFF));
            except.add(new Task(TaskType.AWAY));
            assertEquals((new TaskList(except)).toString(),
                    (ControlTowerInitialiser.readTaskList(fileContents)).toString());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTaskList_Test18() {
        fileContents = "AWAY,LAND,LOAD@60,TAKEOFF@@,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test19() {
        fileContents = "AWAY,LAND,LOAD@60@60,TAKEOFF@60,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTaskList_Test20() {
        fileContents = "AWAY,LAND,LOAD@60,TAKEOFF@A,AWAY";
        try {
            ControlTowerInitialiser.readTaskList(fileContents);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test0() throws IOException {
        fileContents = "" + System.lineSeparator() + "QFA481";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test1() throws IOException {
        fileContents = "TakeoffQueue:0";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            assertEquals(0, takeoffQueue.getAircraftInOrder().size());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readQueue_Test2() throws IOException {
        fileContents = "LandingQueue:0";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, landingQueue);
            assertEquals(0, landingQueue.getAircraftInOrder().size());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readQueue_Test3() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "LandingQueue:1",
                "QFA481");
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, landingQueue);
            assertEquals(1, landingQueue.getAircraftInOrder().size());
            assertEquals(aircraft1, landingQueue.peekAircraft());
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readQueue_Test4() throws IOException {
        fileContents = "LandingQueue:0:";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, landingQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test5() throws IOException {
        fileContents = "LandingQueue0";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, landingQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test6() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "LandingQueue:1",
                "QFA481");
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test7() throws IOException {
        fileContents = "TakeoffQueue:0";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, landingQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test8() throws IOException {
        fileContents = "TakeoffQueue:1";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test9() throws IOException {
        fileContents = "TakeoffQueue:A";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test10() throws IOException {
        fileContents = "TakeoffQueue:0.5";
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test11() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:2",
                "QFA481");
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readQueue_Test12() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "QFA481");
        try {
            ControlTowerInitialiser.readQueue(new BufferedReader(new StringReader(fileContents)),
                    new ArrayList<>(), takeoffQueue);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test1() throws IOException {
        fileContents = "LoadingAircraft:0";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            assertEquals(0, loadingAircraft.size());
        } catch (MalformedSaveException ex) {
            fail();
        }
    }

    @Test
    public void readLoadingAircraft_Test2() throws IOException {
        fileContents = "" + System.lineSeparator() + "QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test3() throws IOException {
        fileContents = "LoadingAircraft:0:";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test4() throws IOException {
        fileContents = "LoadingAircraft0";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test5() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            assertEquals(1, loadingAircraft.size());
            assertEquals(1, (int) loadingAircraft.get(aircraft1));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readLoadingAircraft_Test6() throws IOException {
        fileContents = "LoadingAircraft:1.5" + System.lineSeparator() + "QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test7() throws IOException {
        fileContents = "LoadingAircraft:A" + System.lineSeparator() + "QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test8() throws IOException {
        fileContents = "LoadingAircraft:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test9() throws IOException {
        fileContents = "LoadingAircraft:2" + System.lineSeparator() + "QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test92() throws IOException {
        fileContents = "LoadingAircraft:2" + System.lineSeparator() + "VH-BFK:2,QFA481:1";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            Map<Aircraft, Integer> expected = new HashMap<>();
            expected.put(aircraft1, 1);
            expected.put(aircraft4, 2);
            assertEquals(expected, loadingAircraft);
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readLoadingAircraft_Test10() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481:1:";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test11() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test12() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "NOT_EXIST";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test13() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481:1.5";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test14() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481:A";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readLoadingAircraft_Test15() throws IOException {
        fileContents = "LoadingAircraft:1" + System.lineSeparator() + "QFA481:0";
        try {
            ControlTowerInitialiser.readLoadingAircraft(new BufferedReader(new StringReader(fileContents)),
                    fullAircraftList, loadingAircraft);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test1() throws IOException, NoSpaceException {

        fileContents = String.join(System.lineSeparator(),
                "1:UTD302", "2:empty", "3:empty", "4:empty", "5:empty", "6:empty");
        try {
            Terminal theTerminal = ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:false:6",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            Terminal expect = new AirplaneTerminal(1);
            Gate gate1 = new Gate(1);
            gate1.parkAircraft(aircraft1);
            expect.addGate(gate1);
            expect.addGate(new Gate(2));
            expect.addGate(new Gate(3));
            expect.addGate(new Gate(4));
            expect.addGate(new Gate(5));
            expect.addGate(new Gate(6));

            assertEquals(expect, theTerminal);
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTerminal_Test2() throws IOException, NoSpaceException {

        fileContents = String.join(System.lineSeparator(),
                "1:UTD302", "2:empty");
        try {
            Terminal theTerminal = ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:2",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            Terminal expect = new AirplaneTerminal(1);
            Gate gate1 = new Gate(1);
            gate1.parkAircraft(aircraft1);
            expect.addGate(gate1);
            expect.addGate(new Gate(2));
            expect.declareEmergency();

            assertEquals(expect, theTerminal);
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readTerminal_Test3() throws IOException {

        fileContents = String.join(System.lineSeparator(),
                "1:UTD302", "2:empty");
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:2:",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test4() throws IOException {

        fileContents = String.join(System.lineSeparator(),
                "1:UTD302", "2:empty");
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true2",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test5() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("PassengerTerminal:1:true:1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test6() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1.5:true:1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test7() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:A:true:1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test8() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:0:true:1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test9() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:1.5",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test10() throws IOException {
        fileContents = "1:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:A",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test11() throws IOException {
        fileContents = String.join(System.lineSeparator(),
                "1:UTD302", "2:empty", "3:empty", "4:empty", "5:empty", "6:empty", "7:empty");
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:7",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test12() throws IOException {
        fileContents = "";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:-1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test13() throws IOException {
        fileContents = "";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:1",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readTerminal_Test14() throws IOException {
        fileContents = "2:empty";
        try {
            ControlTowerInitialiser.readTerminal("AirplaneTerminal:1:true:2",
                    new BufferedReader(new StringReader(fileContents)), fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {}
    }

    @Test
    public void readGate_Test1() {
        fileContents = "2:empty";
        try {
            Gate except = new Gate(2);
            assertEquals(except, ControlTowerInitialiser.readGate(fileContents, fullAircraftList));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readGate_Test2() throws NoSpaceException {
        fileContents = "1:UTD302";
        try {
            Gate except = new Gate(1);
            except.parkAircraft(aircraft1);
            assertEquals(except, ControlTowerInitialiser.readGate(fileContents, fullAircraftList));
        } catch (MalformedSaveException ignored) {
            fail();
        }
    }

    @Test
    public void readGate_Test3() {
        fileContents = "1:UTD302:";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test4() {
        fileContents = "1UTD302:";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test5() {
        fileContents = "1";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test6() {
        fileContents = "1.5:UTD302";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test7() {
        fileContents = "a:UTD302";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test8() {
        fileContents = "0:UTD302";
        try {
            ControlTowerInitialiser.readGate(fileContents, fullAircraftList);
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }

    @Test
    public void readGate_Test9() {
        fileContents = "1:UTD302";
        try {
            ControlTowerInitialiser.readGate(fileContents, new ArrayList<>());
            fail();
        } catch (MalformedSaveException ignored) {
        }
    }
}
