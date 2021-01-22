package cz.momento;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    ArrayList<Task> tasks = new ArrayList<>();

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    /**
     * Method returns first name of a person
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Method returns last name of a person
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
