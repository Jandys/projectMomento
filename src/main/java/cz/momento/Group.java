package cz.momento;

import java.util.ArrayList;

public class Group {
    private String name;
    private String desc;
    ArrayList<User> userList = new ArrayList<>();

    /**
     * returns user list array
     * @return
     */
    public ArrayList<User> getUserList(){
        return this.userList;
    }

    /**
     * adds user to group
     * @param user
     * @return
     */
    public boolean addUserToGroup(User user){
        try {
            this.userList.add(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * removes user from group
     * @param user
     * @return
     */
    public boolean removeUserFromGroup(User user){
        try {
            this.userList.remove(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * getter for name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * setter for name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for description
     * @return
     */
    public String getDesc() {
        return desc;
    }

    /**
     * setter for description
     * @param desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }


}

