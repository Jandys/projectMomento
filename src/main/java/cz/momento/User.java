package cz.momento;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    private String cryptedLogin;
    ArrayList<Task> tasks = new ArrayList<>();

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public User() {

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

    public String getCryptedLogin() {
        return this.cryptedLogin;
    }

    public void setCryptedLogin(String cryptedLogin) {
        this.cryptedLogin = cryptedLogin;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
