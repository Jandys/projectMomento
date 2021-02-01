package cz.momento;

import java.util.ArrayList;

public class User {
    private String firstName;
    private String lastName;
    private String cryptedLogin;
    private short id;
    private String usrtbl;

    ArrayList<Task> tasks = new ArrayList<>();

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

    /**
     *
     * @return
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Method adds a task
     * @param task
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Method returns crypted login
     * @return crypted login
     */
    public String getCryptedLogin() {
        return this.cryptedLogin;
    }

    /**
     * Method sets crypted login
     * @param cryptedLogin
     */
    public void setCryptedLogin(String cryptedLogin) {
        this.cryptedLogin = cryptedLogin;
    }

    /**
     * Method sets first name
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Method sets last name
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Method returns user id
     * @return user id
     */
    public short getId() {
        return id;
    }

    /**
     * Method sets user id
     * @param id
     */
    public void setId(short id) {
        this.id = id;
    }

    /**
     * Method returns user table as string
     * @return user table
     */
    public String getUsrtbl() {
        return usrtbl;
    }

    /**
     * Method sets user table
     * @param usrtbl
     */
    public void setUsrtbl(String usrtbl) {
        this.usrtbl = usrtbl;
    }
}
