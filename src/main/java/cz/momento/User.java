package cz.momento;

public class User {
    private String firstName;
    private String lastName;


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

}
