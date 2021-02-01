package cz.momento;

import java.time.LocalDateTime;

/**
 * Class Task which describes task
 */
public class Task {
    LocalDateTime timeFrom;
    LocalDateTime timeTo;
    int id;
    int priority;
    String description;
    String name;
    String status;

    /**
     * Creation of task with given time from and to and its priotiry
     * @param priority priority of task
     * @param timeFrom time and date from which task starts
     * @param timeTo time and date in which tasks end
     */
    public Task(LocalDateTime timeFrom, LocalDateTime timeTo, int priority) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.priority = priority;
    }

    /**
     * Setter
     * @param description value which will be value set to
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter
     * @param name value which will be value set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter
     * @param status value which will be value set to
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Setter
     * @param id value which will be value set to
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getters
     * @return returns requested variable
     */
    public LocalDateTime getTimeFrom() {
        return timeFrom;
    }

    public LocalDateTime getTimeTo() {
        return timeTo;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }
}
