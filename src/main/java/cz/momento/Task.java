package cz.momento;

import java.time.LocalDateTime;

public class Task {
    LocalDateTime timeFrom;
    LocalDateTime timeTo;
    int priority;
    String description;

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
}
