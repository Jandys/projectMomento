package cz.momento.controllers;

import cz.momento.database.Crypto;
import cz.momento.database.DatabaseHandeler;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ControllerRegister {

    public TextField loginField;
    public PasswordField passField1;
    public PasswordField passField2;
    public TextField nameField;
    public TextField surnameField;
    public Label errorRegister;
    private Stage stage = null;

    /**
     * Check if password1 and password2 are the same
     * @param actualChar is an additonal char to proccess when checking
     */
    private void checkPassword(String actualChar) {
        errorRegister.setText("");
        if((passField2.getText() + actualChar).equals(passField1.getText())){

        }else{
            errorRegister.setText("Passwords are not matching");
        }
    }

    /**
     * Check if password1 and password2 are the same
     * @param howMuch is an number of how much characters must be ignored in checking
     */
    private void checkPassword(int howMuch) {
        errorRegister.setText("");
        if((passField2.getText().substring(0,passField2.getText().length() - howMuch)).equals(passField1.getText())){

        }else{
            errorRegister.setText("Passwords are not matching");
        }
    }

    /**
     * Check if password1 and password2 are the same
     */
    private void checkPassword() {
        errorRegister.setText("");
        if(passField2.getText().equals(passField1.getText())){

        }else{
            errorRegister.setText("Passwords are not matching");
        }
    }

    /**
     * Method that is called when user presses new register,
     * It checks if everytihng is alright otherwise it
     * Writes an error into errorRegister label
     */
    public void tryProcessRegister() {
        DatabaseHandeler dh = null;
        String cryptedLogin = "";
        String hashPass = "";


        String login = loginField.getText();
        if(nameField.getText().length() == 0 || surnameField.getText().length()==0){
            errorRegister.setText("Name or surname not filled");
        }else {
            if (loginField.getText().length() > 4 && passField2.getText().length() == passField2.getText().length() && passField2.getText().length() > 4) {
                if (passField2.getText().equals(passField1.getText())) {
                    dh = new DatabaseHandeler();
                    Crypto crypto = new Crypto();
                    String pass = passField1.getText();
                    try {
                        cryptedLogin = crypto.encrypt(login, "boublegum");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    hashPass = crypto.hash(pass);
                }
                else {
                    errorRegister.setText("Passwords are not matching");
                }
            } else {
                errorRegister.setText("login or password length requirement not matched");
            }
        }

        if (cryptedLogin != "" && hashPass != "") {
            dh.connect();
            if(dh.noSameLoginExist(cryptedLogin)){
                if(dh.createNewUser(cryptedLogin,hashPass,nameField.getText(),surnameField.getText())){
                    closeRegister();
                }else {
                    errorRegister.setText("Unknown error couldn't create login");
                }
            }else {
                errorRegister.setText("There is a user with same login");
            }
            dh.endConnection();
        }
    }


    /**
     * Method that closes register stage
     */
    private void closeRegister() {
        this.stage.close();
    }

    /**
     * Method that checks the correctness of the passwords
     * @param keyEvent is a parameter of pressed key
     */
    private void passwordCorrectness(KeyEvent keyEvent){
        if(passField1.getText().length() < 4){
            errorRegister.setText("Password is too short, atleast 5 characters");
        }else {
            if (keyEvent.getText().hashCode() == 0) {
                if(passField2.getText().length() >= 1){
                    checkPassword(1);
                }

            } else if(keyEvent.getText().hashCode() == 9){
                checkPassword();
            }
            else {
                checkPassword(keyEvent.getText());
            }
        }
    }

    /**
     * Method is called everytime a textField or a passwordField is updated via key pess
     * @param keyEvent is a parameter of pressed key
     */
    public void updateKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getTarget() instanceof PasswordField) {
           passwordCorrectness(keyEvent);
        }
        if(keyEvent.getTarget() instanceof TextField){
            if(((TextField) keyEvent.getTarget()).getId().equals("loginField")){
               // System.out.println("LOGIN");
            }
            if(((TextField) keyEvent.getTarget()).getId().equals("nameField")){
               // System.out.println("NAME");
            }
            if(((TextField) keyEvent.getTarget()).getId().equals("surnameField")){
              //  System.out.println("SURNAME");
            }

        }

    }

    /**
     * Setter of a stage
     * @param stage is a stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
