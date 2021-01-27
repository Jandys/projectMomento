package cz.momento.controllers;

import cz.momento.Main;
import cz.momento.database.Crypto;
import cz.momento.database.DatabaseHandeler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class ControllerLogin {

    public Label errorLogin;
    public PasswordField passField;
    public TextField loginField;
    private Stage stage = null;


    public ControllerLogin(){

    }


    /**
     * Method that is called when user presses login
     * It checks if the login and password are correct,
     * and if the user exist
     * @throws Exception
     */
    public void tryLogin() throws Exception {
        stage.getScene().setCursor(Cursor.WAIT);
        String login = loginField.getText();
        String pass = passField.getText();

        if(login.length()<4){
            errorLogin.setText("Login error: short login (atleast 4)");
        } else if (pass.length()<5){
            errorLogin.setText("Login error: short password (atleast 5)");
        }
        else {
            Crypto crypto = new Crypto();
            String cryptedLogin = crypto.encrypt(login,"boublegum");
            String hashPass = crypto.hash(pass);

            DatabaseHandeler dh = new DatabaseHandeler();
            try{
                dh.connect();
                if(dh.doesUserExist(cryptedLogin,hashPass)){
                    processLogin(dh, cryptedLogin, hashPass);
                }else{

                    errorLogin.setText("Wrong username or password.");
                }
            }catch (Exception e){
               e.printStackTrace();
                }
            dh.endConnection();
            }
        stage.getScene().setCursor(Cursor.HAND);


    }

    private void processLogin(DatabaseHandeler dh, String cryptedLogin, String hashPass) {
        try {
            String usertabl = dh.getUserTableId(cryptedLogin,hashPass);
            System.out.println(cryptedLogin);
            int id = dh.getLoginID(cryptedLogin,hashPass);
            Main main = new Main();
            main.calendar(cryptedLogin, hashPass);
            stage.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * Method is called when user press register
     * it opens new Stage of register
     *
     */
    public void tryRegister() {
        stage.getScene().setCursor(Cursor.WAIT);
        try {
            Main main = new Main();
            main.register();
            stage.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        stage.getScene().setCursor(Cursor.HAND);

    }


    /**
     * Setter for stage
     * @param primaryStage is stage
     */
    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void enterReco(KeyEvent keyEvent) {
        stage.getScene().setCursor(Cursor.WAIT);
        if(keyEvent.getCode() == KeyCode.ENTER) {
            try {
                tryLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
