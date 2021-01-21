package cz.momento.database;

import java.sql.*;

public class DatabaseHandeler {

    Connection conn;

    public DatabaseHandeler(){

    }

    /**
     * Method that connects to oracle database.
     */
    public void connect(){
        String dbURL = "jdbc:oracle:thin:@kit-oracle.vse.cz:1521:ora9";
        String us = "janj40";
        String ps = "jakub";
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
            String create = "create table \"user_"+us_id+"_"+usrtbl+"\" (" +
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
            String drop = "drop table \"user_"+us_id+"_"+usrtbl+"\" cascade constraints;";
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




}
