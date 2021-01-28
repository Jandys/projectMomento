package cz.momento;

import java.util.ArrayList;

public class Group {
    private String name;
    private String desc;
    ArrayList<User> userList = new ArrayList<>();

    public ArrayList<User> getUserList(){
        return this.userList;
    }
    public boolean addUserToGroup(User user){
        try {
            this.userList.add(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public boolean removeUserFromGroup(User user){
        try {
            this.userList.remove(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }


}

