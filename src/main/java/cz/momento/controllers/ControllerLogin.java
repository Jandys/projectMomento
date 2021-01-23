package cz.momento.controllers;

import cz.momento.Main;
import cz.momento.database.Crypto;
import cz.momento.database.DatabaseHandeler;
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
                    String usertabl = dh.getUserTableId(cryptedLogin,hashPass);
                    int id = dh.getLoginID(cryptedLogin,hashPass);
                    Main main = new Main();
                    main.calendar();
                    stage.close();
                    System.out.println("user_"+id+"_"+usertabl);
                }else{

                    errorLogin.setText("Wrong username or password.");
                }
            }catch (Exception e){
               e.printStackTrace();
                }
            dh.endConnection();
            }

        }


    /**
     * Method is called when user press register
     * it opens new Stage of register
     *
     */
    public void tryRegister() {
        System.out.println("TRY REGISTER");
        try {
            Main main = new Main();
            main.register();
            stage.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Setter for stage
     * @param primaryStage is stage
     */
    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void enterReco(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            try {
                tryLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
