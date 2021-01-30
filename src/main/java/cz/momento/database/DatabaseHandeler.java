package cz.momento.database;

import cz.momento.Group;
import cz.momento.Task;
import cz.momento.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DatabaseHandeler {

    Connection conn;

    public DatabaseHandeler(){

    }

    /**
     * Method that connects to oracle database.
     */
    public void connect() throws SQLException {
        String dbURL = "jdbc:oracle:thin:@kit-oracle.vse.cz:1521:ora9";
        String us = "janj40";
        String ps = "jakub";
        DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());

        try{
            this.conn = DriverManager.getConnection(dbURL,us,ps);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method that expects parameter sql, in format of sql select command.
     * @param sql String of sql select quarry type.
     * @return whole resultset to further work.
     */
    public ResultSet select(String sql){
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }


    public boolean executeSQL(String sql){
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * Mehtod takes login and password in and looks through database if there is a account with the same login and password
     * @param login String as crypted login
     * @param pass String as hash password
     * @return gives back true if the user exists, otherwise returns false
     */
    public boolean doesUserExist(String login, String pass){
        String sql = "select COUNT(*) from JANJ40.\"login\" where \"login\" like '"+login+"' and \"pass\" like '"+pass+"'";
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                if(rs.getInt(1) == 1){
                    System.out.println("ACCOUNT FOUND");
                    return true;
                }
            }
            rs.close();
            stmt.close();

        }catch (SQLSyntaxErrorException e){
            System.out.println("ACCOUNT NOT FOUND");
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    /**
     * Method that returns String array of groups associated with user
     * @param cryptedLogin crypted login duh
     * @return array of groups
     */
    public String getGroup(String cryptedLogin){
        String sql = "select \"group\" from JANJ40.\"login\" where \"login\" like '"+ cryptedLogin +"'";
        String groups = null;
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                groups = rs.getString(1);
            }
            rs.close();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return groups;
    }


    public void clearGroup(String cryptedLogin){
        String sql = "update \"login\" set \"group\" = null where \"login\" like ?";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1,cryptedLogin);
            int result = stmt.executeUpdate();
            // stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void editGrop(String whatToAdd, String cryptedLogin){
        String currentGroup = getGroup(cryptedLogin);


        String sql = "update \"login\" set \"group\" = ? where \"login\" like ?";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            if(currentGroup==null){
                stmt.setString(1,whatToAdd);
            }else {
                stmt.setString(1,currentGroup+","+whatToAdd);
            }
            stmt.setString(2,cryptedLogin);
            int result = stmt.executeUpdate();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method takes login and password in crypted form and returns id of row in databse with same login and password
     * @param login String as crypted login
     * @param pass String as hash password
     * @return number representing the ID
     */
    public int getLoginID(String login, String pass){
        int returnInt = 0;
        String sql = "select \"us_id\" from JANJ40.\"login\" where \"login\" like '"+login+"' and \"pass\" like '"+pass+"'";
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                returnInt = rs.getInt(1);
            }
            rs.close();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnInt;
    }

    /**
     * Method takes login and password in crypted form and returns number of 'usrtbl' in databse with same login and password
     * @param login String as crypted login
     * @param pass String as hash password
     * @return number representing 'usrtbl' column in database
     */
    public String getUserTableId(String login, String pass){
        String returnString = "";
        String sql = "select \"usrtbl\" from JANJ40.\"login\" where \"login\" like '"+login+"' and \"pass\" like '"+pass+"'";
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                returnString = rs.getString(1);
            }
            rs.close();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnString;
    }

    /**
     * Method looks into database and returns the largest last id from the table 'login'
     * @return number representing the largest id
     */
    public int getLastUserID(){
        int returnInt = 0;
        String sql = "select \"us_id\" from (select \"us_id\" from JANJ40.\"login\" order by \"us_id\" DESC) where ROWNUM = 1";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                returnInt = rs.getInt(1);
            }
            rs.close();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnInt;
    }

    /**
     * Mehtod takes parameters also creates some new ones and tries to create new login and new usertable via calling newLogin() and newUserTable()
     * @param login crypted String of login
     * @param pass String of hashed password
     * @param name Plain text name of user
     * @param surname Plain text surname of user
     * @return false if there is same user in existance and return true if it succesfully went through whole method
     */

    public boolean createNewUser(String login, String pass, String name, String surname){
        if(noSameLoginExist(login)){
            int id = getLastUserID() + 1;
            String usrtbl = System.currentTimeMillis()+"";
            if(newLogin(id,login,pass,usrtbl,0,name,surname)){
                System.out.println("Login created successfully");
            }
            else {
                System.out.println("Couldn't create new Login");
                return false;
            }
            newUserTable(String.valueOf(id),usrtbl);
            return true;
        }
        System.out.println();
        return false;
    }


    /**
     * Mehtod looks into database for user with same login
     * @param login String of crypted login
     * @return true if there is no user with same login otherwise returns false if there is a user with the same login
     */
    public boolean noSameLoginExist(String login){
        String sql = "select * from JANJ40.\"login\" where \"login\" like '"+login+"'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                return false;
            }
            rs.close();
            stmt.close();

        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }


    /**
     * Method that can delete user and table that is assosiated with this uer
     * @param cryptedLogin is a string of a crypted Login
     * @param hashPass is a hashed password
     * @return returns true method successfully deleted user otherwise returns false
     */

    public boolean deleteUser(String cryptedLogin, String hashPass){
        int us_id = -1;
        String usrtbl = "";

        String sql = "select \"us_id\",\"usrtbl\" from \"login\" where \"login\" like \'" + cryptedLogin + "\' and \"pass\" like \'" + hashPass+"\'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                us_id = rs.getInt(1);
                usrtbl = rs.getString(2);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        if(us_id == -1 || usrtbl.isEmpty()){
            return false;
        }
        if(tableDoesExits("\"user_tasks_"+us_id+"_"+usrtbl+"\"")){
            try {
                dropUserTable(String.valueOf(us_id),usrtbl);
                String sql2 = "delete from \"login\" where \"us_id\" like '" + us_id + "'";
                Statement stmt = conn.createStatement();
                stmt.execute(sql2);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        else {
            try {
                dropUserTable(String.valueOf(us_id),usrtbl);
                String sql2 = "delete from \"login\" where \"us_id\" like '" + us_id + "'";
                Statement stmt = conn.createStatement();
                stmt.execute(sql2);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            dropUserTable(String.valueOf(us_id),usrtbl);
        }
        return true;
    }


    /**
     * Method takes inputs and creates new row in database for login
     * @param us_id int of incremented last id in database
     * @param login String of crypted login
     * @param pass String of hashed password
     * @param usrtbl local milliseconds time
     * @param group represents numbered workgorup
     * @param name of the user
     * @param surname of the user
     * @return false if it failed or true if the creation went right
     */
    private boolean newLogin(int us_id, String login, String pass, String usrtbl, int group, String name, String surname){
        String create = "insert into \"login\" " +
                "(\"us_id\", \"login\", \"pass\", \"usrtbl\", \"group\", \"name\", \"surname\", \"url_obrazek\") " +
                "values ("+us_id+",'"+login+"','"+pass+"','"+usrtbl+"',"+group+",'"+name+"','"+surname+"','NOURL')";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Method that tries to delete the table of the tasks then create new.
     * @param us_id is identification number of user
     * @param usrtbl is specific number for task table
     */
    public void newUserTable(String us_id, String usrtbl){
        try {
            if(!dropUserTable(us_id,usrtbl)){
                System.out.println("Trying to delete table that doesn't exist!");
            }
            if(createUserTable(us_id,usrtbl)){
                System.out.println("Successfully created new user table");
            }
            else {
                System.out.println("Could not create new user table");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method that tries to create new table for tasks
     * @param us_id number of the user
     * @param usrtbl specific nubmer for the task table
     * @return false if there is an error otherwise returns true
     */
    public boolean createUserTable(String us_id, String usrtbl){
        try {
            String create = "create table \"user_tasks_"+us_id+"_"+usrtbl+"\" (" +
                    "\"task_id\" INTEGER not null," +
                    "\"k_id\" NUMBER(6) not null," +
                    "\"us_id\" NUMBER(6) not null," +
                    "\"task\" CLOB not null," +
                    "\"desc\" CLOB not null," +
                    "\"timestart\" INTEGER not null," +
                    "\"timeend\" INTEGER not null," +
                    "\"priority\" INTEGER not null," +
                    "\"status\" CLOB not null" +
                    " )";
            Statement statement = conn.createStatement();
            statement.executeUpdate(create);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Method that tries to delete task table named by parameters
     * @param us_id number that represents the user position
     * @param usrtbl specific nubmer for table
     * @return true if everything went right otherwise returns true
     */
    public boolean dropUserTable(String us_id, String usrtbl){
        try {
            String drop = "drop table \"user_tasks_"+us_id+"_"+usrtbl+"\" cascade constraints;";
            Statement statement = conn.createStatement();
            statement.execute(drop);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean tableDoesExits(String name){
        try {
            String sql = "SELECT * FROM " + name;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rs.close();
                stmt.close();
                return true;
            }
            rs.close();
            stmt.close();
            return false;
        }catch (Exception e) {
            return false;
        }
    }


    /**
     * Method that ends connection to database
     */
    public void endConnection(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public User setLoggedUser(String cryptedLogin, String hashPass) {
        User loggedUser = new User();
        try {
            connect();
            String sql = "select \"us_id\",\"login\",\"usrtbl\",\"name\",\"surname\" from \"login\" where \"login\" like \'" + cryptedLogin + "\' and \"pass\" like \'" + hashPass+"\'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                loggedUser.setId((short)rs.getInt(1));
                loggedUser.setCryptedLogin(rs.getString(2));
                loggedUser.setUsrtbl(rs.getString(3));
                loggedUser.setFirstName(rs.getString(4));
                loggedUser.setLastName(rs.getString(5));
                addTasksToUser(loggedUser);
            }
            endConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return loggedUser;
    }

    public Group getUserWithGroup(Group rGroup) {
        try {
            connect();
            String sql = "select * from \"login\" where REGEXP_LIKE(\"group\" , '(^|,)("+rGroup.getName()+")(,|$)')";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                User nUser = new User();
                nUser.setId((short)rs.getInt(1));
                nUser.setUsrtbl(rs.getString(4));
                nUser.setFirstName(rs.getString(6));
                nUser.setLastName(rs.getString(7));
                addTasksToUser(nUser);
                rGroup.addUserToGroup(nUser);
            }
            endConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return rGroup;

    }

    private void addTasksToUser(User nUser) {
        try {
            String sql = "select * from \"user_tasks_"+nUser.getId()+"_"+nUser.getUsrtbl()+"\" ";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt(1);
                long from = rs.getLong(6);
                long to = rs.getLong(7);
                String name = rs.getString(4);
                String desc = rs.getString(5);
                int prio = rs.getInt(8);
                String status = rs.getString(9);
                LocalDateTime start = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDateTime();
                Task nTask = new Task(start,end,prio);
                nTask.setId(id);
                nTask.setDescription(desc);
                nTask.setName(name);
                nTask.setStatus(status);
                nUser.addTask(nTask);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }

    }

    public void creteTask(Task task, User user) {
        String table = "\"user_tasks_"+user.getId()+"_"+user.getUsrtbl()+"\"";
        String sql = "insert into "+table+" values(?,0,?,?,?,?,?,?,'open')";
        int lastTaskId = getLastTaskID(table);
        int usID = user.getId();
        String name = (task.getName()==null)?"Task":task.getName();
        String desc = (task.getDescription()==null)?"No description of task":task.getDescription();
        long timeStart = task.getTimeFrom().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long timeEnd = task.getTimeTo().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        int prio = task.getPriority();
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setInt(1,lastTaskId+1);
            stmt.setInt(2,usID);
            stmt.setString(3,name);
            stmt.setString(4,desc);
            stmt.setLong(5,timeStart);
            stmt.setLong(6,timeEnd);
            stmt.setInt(7,prio);
            stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public String getUserIDfromUserTable(String usrtbl){
        String sql = "select \"us_id\" from \"login\" where \"usrtbl\" like '" + usrtbl+"'";
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }


    private int getLastTaskID(String table) {
        int returnInt = 1;

        String sql = "select \"task_id\" from (select \"task_id\" from "+table+" order by \"task_id\" DESC) where ROWNUM = 1";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                returnInt = rs.getInt(1);
            }
            rs.close();
            stmt.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnInt;

    }

    public void editTask(User user, Task task) {
        String table ="\"user_tasks_"+user.getId()+"_"+user.getUsrtbl()+"\"";
        String values = "\"task\" = ?, \"desc\"=?, \"timestart\" = ?, \"timeend\" = ?, \"priority\" = ?, \"status\" = ? where \"task_id\" = ?";
        String sql = "update "+table+" set " + values;
        long timeStart = task.getTimeFrom().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long timeEnd = task.getTimeTo().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1,task.getName());
            stmt.setString(2,task.getDescription());


            stmt.setLong(3,timeStart);
            stmt.setLong(4,timeEnd);
            stmt.setInt(5,task.getPriority());
            stmt.setString(6,task.getStatus());
            stmt.setInt(7,task.getId());


            stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
