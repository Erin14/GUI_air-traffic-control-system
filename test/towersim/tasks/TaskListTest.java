package towersim.tasks;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaskListTest {
    @Test
    public void initialTaskTest() {
        Task awayTask = new Task(TaskType.AWAY);
        TaskList list = new TaskList(List.of(awayTask,
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        // First task returned by getCurrentTask() should be first task in list
        assertEquals("After initialising a TaskList, getCurrentTask() should return the first task "
                + "in the list", awayTask, list.getCurrentTask());
    }

    @Test
    public void orderTest() {
        TaskList taskList = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)));

        TaskList taskList1 = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 109),
                new Task(TaskType.TAKEOFF)));

        TaskList taskList2 = new TaskList(List.of(new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));

        TaskList taskList3 = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.LOAD, 20),
                new Task(TaskType.TAKEOFF)));
    }

    @Test
    public void encodeTest() {
        TaskList taskList = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)));
        assertEquals(taskList.encode(), "AWAY,AWAY,AWAY");
        taskList.moveToNextTask();
        assertEquals(taskList.encode(), "AWAY,AWAY,AWAY");
        taskList.moveToNextTask();
        assertEquals(taskList.encode(), "AWAY,AWAY,AWAY");

        TaskList taskList1 = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 109),
                new Task(TaskType.TAKEOFF)));
        assertEquals("AWAY,LAND,WAIT,LOAD@109,TAKEOFF", taskList1.encode());
        taskList1.moveToNextTask();
        assertEquals("LAND,WAIT,LOAD@109,TAKEOFF,AWAY", taskList1.encode());
        taskList1.moveToNextTask();
        assertEquals("WAIT,LOAD@109,TAKEOFF,AWAY,LAND", taskList1.encode());

        TaskList taskList2 = new TaskList(List.of(new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));

        TaskList taskList3 = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.LOAD, 20),
                new Task(TaskType.TAKEOFF)));
        assertEquals("AWAY,LAND,LOAD@20,TAKEOFF", taskList3.encode());
        taskList3.moveToNextTask();
        assertEquals("LAND,LOAD@20,TAKEOFF,AWAY", taskList3.encode());
        taskList3.moveToNextTask();
        assertEquals("LOAD@20,TAKEOFF,AWAY,LAND", taskList3.encode());
        taskList3.moveToNextTask();
        assertEquals("TAKEOFF,AWAY,LAND,LOAD@20", taskList3.encode());
        taskList3.moveToNextTask();
        assertEquals("AWAY,LAND,LOAD@20,TAKEOFF", taskList3.encode());
    }

    @Test
    public void getNextTask_BasicTest() {
        Task landTask = new Task(TaskType.LAND);
        TaskList list = new TaskList(List.of(new Task(TaskType.AWAY),
                landTask,
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        // First task returned by getNextTask() should be second task in list
        assertEquals("getNextTask() should return the task immediately following the current task",
                landTask, list.getNextTask());
    }

    @Test
    public void getNextTask_CircularTest() {
        Task awayTask = new Task(TaskType.AWAY);
        TaskList list = new TaskList(List.of(awayTask,
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask(); // current task should now be the last task in the list

        // Task returned by getNextTask() should be first task in list (wrapped back to start)
        assertEquals("If the current task is the last in the list, getNextTask() should return the "
                + "first task in the list", awayTask, list.getNextTask());
    }

    @Test
    public void moveToNextTask_BasicTest() {
        Task landTask = new Task(TaskType.LAND);
        Task waitTask = new Task(TaskType.WAIT);
        TaskList list = new TaskList(List.of(new Task(TaskType.AWAY),
                landTask,
                waitTask,
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        list.moveToNextTask(); // move to LAND

        // getCurrentTask() should now be second task in list
        assertEquals("After calling moveToNextTask(), the current task as returned by "
                        + "getCurrentTask() should be moved forward by one",
                landTask, list.getCurrentTask());

        // getNextTask() should now be third task in list
        assertEquals("After calling moveToNextTask(), the next task as returned by "
                + "getNextTask() should be moved forward by one",
                waitTask, list.getNextTask());
    }

    @Test
    public void moveToNextTask_CircularTest() {
        Task awayTask = new Task(TaskType.AWAY);
        Task landTask = new Task(TaskType.LAND);
        TaskList list = new TaskList(List.of(awayTask,
                landTask,
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask(); // current task should now be the first task in the list (wrapped)

        // Task returned by getCurrentTask() should be first task in list
        assertEquals("getCurrentTask() should return first task in list when all tasks have been "
                + "moved through", awayTask, list.getCurrentTask());

        // Task returned by getNextTask() should be second task in list
        assertEquals("getNextTask() should return second task in list when all tasks have been "
                + "moved through", landTask, list.getNextTask());
    }

    @Test
    public void toString_BasicTest() {
        TaskList list = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        assertEquals("TaskList currently on AWAY [1/5]", list.toString());
    }

    @Test
    public void toString_CircularTest() {
        TaskList list = new TaskList(List.of(new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD),
                new Task(TaskType.TAKEOFF)));

        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask();

        assertEquals("TaskList currently on WAIT [4/7]", list.toString());

        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask();
        list.moveToNextTask(); // should now wrap back to first task in list

        assertEquals("TaskList currently on AWAY [1/7]", list.toString());
    }
}
