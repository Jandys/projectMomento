package cz.momento;

import java.time.LocalDate;

public class Task {
    LocalDate timeFrom;
    LocalDate timeTo;
    int priority;
    String description;

    public Task(LocalDate timeFrom, LocalDate timeTo, int priority) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTimeFrom() {
        return timeFrom;
    }

    public LocalDate getTimeTo() {
        return timeTo;
    }

    public int getPriority() {
        return priority;
    }
}
