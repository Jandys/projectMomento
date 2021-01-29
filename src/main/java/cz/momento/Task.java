package cz.momento;

import java.time.LocalDateTime;

public class Task {
    LocalDateTime timeFrom;
    LocalDateTime timeTo;
    int id;
    int priority;
    String description;
    String name;
    String status;

    public Task(LocalDateTime timeFrom, LocalDateTime timeTo, int priority) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimeFrom() {
        return timeFrom;
    }

    public LocalDateTime getTimeTo() {
        return timeTo;
    }

    public int getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
