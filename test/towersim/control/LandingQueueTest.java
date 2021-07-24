package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LandingQueueTest {

    private LandingQueue landingQueue1;
    private LandingQueue landingQueue2;

    private TaskList taskList3;
    private FreightAircraft freightAircraft1;
    private FreightAircraft freightAircraft2;
    private PassengerAircraft passengerAircraft1;
    private PassengerAircraft passengerAircraft2;

    @Before
    public void setUp() {
        landingQueue1 = new LandingQueue();
        landingQueue2 = new LandingQueue();

        TaskList taskList1 = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        taskList3 = new TaskList(List.of(
                new Task(TaskType.LOAD, 30), // load 30% of capacity of freight
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        this.freightAircraft1 = new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F,
                        taskList1,
                        AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                        AircraftCharacteristics.BOEING_747_8F.freightCapacity);

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                6000);

        this.passengerAircraft1 = new PassengerAircraft("ABC123",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.passengerAircraft2 = new PassengerAircraft("XYZ987",
                AircraftCharacteristics.AIRBUS_A320,
                new TaskList(List.of(
                        new Task(TaskType.AWAY),
                        new Task(TaskType.LAND),
                        new Task(TaskType.WAIT),
                        new Task(TaskType.LOAD),
                        new Task(TaskType.TAKEOFF))),
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity / 2);
    }

    @Test
    public void addAircraft_Test1() {
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft2);
    }

    @Test
    public void addAircraft_Test2() {
        landingQueue2.addAircraft(passengerAircraft1);
    }

    @Test
    public void addAircraft_Test3() {
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft2);
    }

    @Test
    public void peekAircraft_Test0() {
        assertNull(landingQueue1.peekAircraft());
    }

    @Test
    public void peekAircraft_Test1() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        assertEquals(freightAircraft1, landingQueue1.peekAircraft());
        assertEquals(2, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void peekAircraft_Test2() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        assertEquals(freightAircraft2, landingQueue1.peekAircraft());
        assertEquals(2, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void peekAircraft_Test3() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        assertEquals(passengerAircraft1, landingQueue1.peekAircraft());
        assertEquals(3, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void peekAircraft_Test4() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        assertEquals(passengerAircraft2, landingQueue1.peekAircraft());
        assertEquals(4, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void peekAircraft_Test5() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }
        assertEquals(freightAircraft1, landingQueue1.peekAircraft());
    }

    @Test
    public void peekAircraft_Test6() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        assertEquals(freightAircraft2, landingQueue1.peekAircraft());
        assertEquals(freightAircraft2, landingQueue1.removeAircraft());
        assertEquals(freightAircraft1, landingQueue1.peekAircraft());
        assertEquals(freightAircraft1, landingQueue1.removeAircraft());
    }

    @Test
    public void peekAircraft_Test7() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        freightAircraft1.declareEmergency();
        assertEquals(freightAircraft1, landingQueue1.peekAircraft());
        assertEquals(freightAircraft1, landingQueue1.removeAircraft());
        assertEquals(freightAircraft2, landingQueue1.peekAircraft());
        assertEquals(freightAircraft2, landingQueue1.removeAircraft());
    }

    @Test
    public void peekAircraft_Test8() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        freightAircraft1.declareEmergency();
        assertEquals(freightAircraft1, landingQueue1.peekAircraft());
    }

    @Test
    public void removeAircraft_Test0() {
        assertNull(landingQueue1.removeAircraft());
    }

    @Test
    public void removeAircraft_Test1_0() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        assertEquals(freightAircraft1, landingQueue1.removeAircraft());
        assertEquals(freightAircraft2, landingQueue1.removeAircraft());
    }

    @Test
    public void removeAircraft_Test1_1() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.removeAircraft();
        assertEquals(1, landingQueue1.getAircraftInOrder().size());
        landingQueue1.removeAircraft();
        assertEquals(0, landingQueue1.getAircraftInOrder().size());
    }


    @Test
    public void removeAircraft_Test2() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        assertEquals(landingQueue1.peekAircraft(), landingQueue1.removeAircraft());
        assertEquals(landingQueue1.peekAircraft(), landingQueue1.removeAircraft());
    }

    @Test
    public void removeAircraft_Test3() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        assertEquals(passengerAircraft1, landingQueue1.removeAircraft());
    }

    @Test
    public void removeAircraft_Test4() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        assertEquals(passengerAircraft2, landingQueue1.removeAircraft());
        assertEquals(3, landingQueue1.getAircraftInOrder().size());
        assertEquals(passengerAircraft1, landingQueue1.removeAircraft());
        assertEquals(2, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void removeAircraft_Test5() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }
        assertEquals(freightAircraft1, landingQueue1.removeAircraft());
    }

    @Test
    public void getOrderedAircraft_Test0() {
        assertEquals(0, landingQueue1.getAircraftInOrder().size());
    }

    @Test
    public void getOrderedAircraft_Test1() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft1);
        except.add(freightAircraft2);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test2() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft2);
        except.add(freightAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test3() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        List<Aircraft> except = new ArrayList<>();
        except.add(passengerAircraft1);
        except.add(freightAircraft2);
        except.add(freightAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());

        freightAircraft1.declareEmergency();
        List<Aircraft> except2 = new ArrayList<>();
        except2.add(freightAircraft1);
        except2.add(passengerAircraft1);
        except2.add(freightAircraft2);
        assertEquals(except2, landingQueue1.getAircraftInOrder());

        freightAircraft2.declareEmergency();
        List<Aircraft> except3 = new ArrayList<>();
        except3.add(freightAircraft2);
        except3.add(freightAircraft1);
        except3.add(passengerAircraft1);
        assertEquals(except3, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test4() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        List<Aircraft> except = new ArrayList<>();
        except.add(passengerAircraft2);
        except.add(passengerAircraft1);
        except.add(freightAircraft2);
        except.add(freightAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test5() {
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft1);
        except.add(passengerAircraft2);
        except.add(passengerAircraft1);
        except.add(freightAircraft2);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test6() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft2);
        except.add(freightAircraft1);
        except.add(passengerAircraft2);
        except.add(passengerAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test7() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        freightAircraft1.declareEmergency();

        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft1);
        except.add(freightAircraft2);
        except.add(passengerAircraft2);
        except.add(passengerAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void getOrderedAircraft_Test8() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        freightAircraft1.declareEmergency();

        List<Aircraft> except = new ArrayList<>();
        except.add(freightAircraft1);
        except.add(freightAircraft2);
        except.add(passengerAircraft2);
        except.add(passengerAircraft1);
        assertEquals(except, landingQueue1.getAircraftInOrder());
    }

    @Test
    public void containsAircraft_Test1() {
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft2);
        assertTrue(landingQueue1.containsAircraft(passengerAircraft1));
        assertTrue(landingQueue1.containsAircraft(passengerAircraft2));
        assertFalse(landingQueue1.containsAircraft(freightAircraft1));
        assertFalse(landingQueue1.containsAircraft(freightAircraft2));
    }

    @Test
    public void conAircraft_Test2() {
        landingQueue2.addAircraft(passengerAircraft1);
        assertTrue(landingQueue2.containsAircraft(passengerAircraft1));
        assertFalse(landingQueue2.containsAircraft(freightAircraft1));
        assertFalse(landingQueue2.containsAircraft(freightAircraft2));
    }

    @Test
    public void conAircraft_Test3() {
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft2);
        assertTrue(landingQueue2.containsAircraft(passengerAircraft1));
        assertTrue(landingQueue2.containsAircraft(passengerAircraft2));
        assertFalse(landingQueue2.containsAircraft(freightAircraft2));
        landingQueue2.removeAircraft();
        assertTrue(landingQueue2.containsAircraft(passengerAircraft1));
        landingQueue2.removeAircraft();
        landingQueue2.removeAircraft();
        assertFalse(landingQueue2.containsAircraft(passengerAircraft1));
        assertTrue(landingQueue2.containsAircraft(passengerAircraft2));
    }

    @Test
    public void encode_Test0() {
        assertEquals("LandingQueue:0", landingQueue1.encode());
        assertEquals("LandingQueue:0", landingQueue2.encode());
    }

    @Test
    public void encode_Test1() {
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft2);
        String except = String.join(System.lineSeparator(),
                "LandingQueue:4",
                "ABC123,ABC123,ABC123,XYZ987");
        assertEquals(except, landingQueue1.encode());
    }

    @Test
    public void encode_Test2() {
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft2);
        String except = String.join(System.lineSeparator(),
                "LandingQueue:4",
                "ABC123,ABC123,ABC123,XYZ987");
        assertEquals(except, landingQueue2.encode());
    }

    @Test
    public void encode_Test3() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        String except = String.join(System.lineSeparator(),
                "LandingQueue:2",
                "ABC001,ABC002");
        assertEquals(except, landingQueue1.encode());
    }

    @Test
    public void encode_Test4() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft1);
        String except = String.join(System.lineSeparator(),
                "LandingQueue:3",
                "ABC123,ABC001,ABC002");
        assertEquals(except, landingQueue1.encode());

        landingQueue1.removeAircraft();
        except = String.join(System.lineSeparator(),
                "LandingQueue:2",
                "ABC001,ABC002");
        assertEquals(except, landingQueue1.encode());
    }

    @Test
    public void encode_Test5() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        freightAircraft1.declareEmergency();

        String except = String.join(System.lineSeparator(),
                "LandingQueue:4",
                "ABC001,ABC002,XYZ987,ABC123");
        assertEquals(except, landingQueue1.encode());

        landingQueue1.removeAircraft();
        except = String.join(System.lineSeparator(),
                "LandingQueue:3",
                "ABC002,XYZ987,ABC123");
        assertEquals(except, landingQueue1.encode());
    }

    @Test
    public void string_Test0() {
        assertEquals("LandingQueue []", landingQueue1.toString());
        assertEquals("LandingQueue []", landingQueue2.toString());
    }

    @Test
    public void string_Test1() {
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);
        landingQueue1.addAircraft(passengerAircraft2);
        assertEquals("LandingQueue [ABC123, ABC123, ABC123, XYZ987]", landingQueue1.toString());
    }

    @Test
    public void string_Test2() {
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft1);
        landingQueue2.addAircraft(passengerAircraft2);
        assertEquals("LandingQueue [ABC123, ABC123, ABC123, XYZ987]", landingQueue2.toString());
    }

    @Test
    public void string_Test3() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        assertEquals("LandingQueue [ABC001, ABC002]", landingQueue1.toString());
    }

    @Test
    public void string_Test4() {
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft1);
        assertEquals("LandingQueue [ABC123, ABC001, ABC002]", landingQueue1.toString());

        landingQueue1.removeAircraft();
        assertEquals("LandingQueue [ABC001, ABC002]", landingQueue1.toString());
    }

    @Test
    public void string_Test5() {

        this.freightAircraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                this.taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.2,
                6000);

        landingQueue1.addAircraft(freightAircraft2);
        landingQueue1.addAircraft(passengerAircraft2);
        landingQueue1.addAircraft(freightAircraft1);
        landingQueue1.addAircraft(passengerAircraft1);

        for (int i = 0; i < 4; i++) {
            freightAircraft1.tick();
        }

        freightAircraft1.declareEmergency();
        assertEquals("LandingQueue [ABC001, ABC002, XYZ987, ABC123]", landingQueue1.toString());

        landingQueue1.removeAircraft();
        assertEquals("LandingQueue [ABC002, XYZ987, ABC123]", landingQueue1.toString());
    }

}
