package towersim.tasks;

import towersim.util.Encodable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a circular list of tasks for an aircraft to cycle through.
 * @ass1
 */
public class TaskList implements Encodable {
    /** List of tasks to cycle through. */
    private final List<Task> tasks;

    /** Index of current task in tasks list. */
    private int currentTaskIndex;

    /**
     * Creates a new TaskList with the given list of tasks.
     * <p>
     * Initially, the current task (as returned by {@link #getCurrentTask()}) should be the first
     * task in the given list.
     * The list of tasks should be validated to ensure that it complies with the rules for task
     * ordering. If the given list is invalid, an IllegalArgumentException should be thrown.
     *
     * @param tasks list of tasks
     * @throws IllegalArgumentException if the task list is invalid
     * @ass1
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
        this.currentTaskIndex = 0;
        // check if the list of tasks is valid or not
        checkValidation();
    }

    /**
     * Check if the task list is valid or not. Throw an IllegalArgumentException if not.
     *
     * @throws IllegalArgumentException if the task list is invalid
     */
    private void checkValidation() {
        if (tasks.size() == 0) {
            // empty task list
            throw new IllegalArgumentException();
        }

        // a list of string containing all the valid order of two task
        List<String> validOrder = new ArrayList<>();
        validOrder.add("AWAY, AWAY");
        validOrder.add("AWAY, LAND");
        validOrder.add("LAND, WAIT");
        validOrder.add("LAND, LOAD");
        validOrder.add("WAIT, WAIT");
        validOrder.add("WAIT, LOAD");
        validOrder.add("LOAD, TAKEOFF");
        validOrder.add("TAKEOFF, AWAY");

        // check the order of the tasks
        for (int i = 0; i < tasks.size(); i++) {
            TaskType currentTaskType = getCurrentTask().getType();
            TaskType nextTaskType = getNextTask().getType();
            if (!(validOrder.contains(currentTaskType + ", " + nextTaskType))) {
                throw new IllegalArgumentException();
            }
            moveToNextTask();
        }
    }

    /**
     * Returns the current task in the list.
     *
     * @return current task
     * @ass1
     */
    public Task getCurrentTask() {
        return this.tasks.get(this.currentTaskIndex);
    }

    /**
     * Returns the task in the list that comes after the current task.
     * <p>
     * After calling this method, the current task should still be the same as it was before calling
     * the method.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * this method should return the first element of the list.
     *
     * @return next task
     * @ass1
     */
    public Task getNextTask() {
        int nextTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
        return this.tasks.get(nextTaskIndex);
    }

    /**
     * Moves the reference to the current task forward by one in the circular task list.
     * <p>
     * After calling this method, the current task should be the next task in the circular list
     * after the "old" current task.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * the new current task should be the first element of the list.
     * @ass1
     */
    public void moveToNextTask() {
        this.currentTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
    }

    /**
     * Returns the human-readable string representation of this task list.
     * <p>
     * The format of the string to return is
     * <pre>TaskList currently on currentTask [taskNum/totalNumTasks]</pre>
     * where {@code currentTask} is the {@code toString()} representation of the current task as
     * returned by {@link Task#toString()},
     * {@code taskNum} is the place the current task occurs in the task list, and
     * {@code totalNumTasks} is the number of tasks in the task list.
     * <p>
     * For example, a task list with the list of tasks {@code [AWAY, LAND, WAIT, LOAD, TAKEOFF]}
     * which is currently on the {@code WAIT} task would have a string representation of
     * {@code "TaskList currently on WAIT [3/5]"}.
     *
     * @return string representation of this task list
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("TaskList currently on %s [%d/%d]",
                this.getCurrentTask(),
                this.currentTaskIndex + 1,
                this.tasks.size());
    }

    /**
     * Returns the machine-readable string representation of this task list.
     * The format of the string to return is: encodedTask1,encodedTask2,...,encodedTaskN
     *
     * @return encoded string representation of this task list
     */
    public String encode() {
        // a string joiner of the task list encode
        StringJoiner taskListEncode = new StringJoiner(",");
        for (int i = 0; i < tasks.size(); i++) {
            taskListEncode.add(getCurrentTask().encode());
            moveToNextTask();
        }
        return taskListEncode.toString();
    }
}
