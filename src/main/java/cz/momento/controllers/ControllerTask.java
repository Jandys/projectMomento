package cz.momento.controllers;

import cz.momento.DateTimePicker;
import cz.momento.Group;
import cz.momento.Task;
import cz.momento.User;
import cz.momento.database.DatabaseHandeler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class ControllerTask {
    public ComboBox employee;
    public ComboBox priority;
    private Group chosenGroup;
    private Stage stage;

    @FXML
    private TextField taskName;
    @FXML
    private TextField taskDesc;
    @FXML
    private Label errorMessage;
    @FXML
    private DateTimePicker timeFrom;
    @FXML
    private DateTimePicker timeTo;

    public void ControllerTask(){


    }

    /**
     * method that fills combobox
     */
    public void setPriorityOptions(){
        for(int i = 1; i <= 5;i++){
            priority.getItems().add(String.valueOf(i));
        }
    }
    /**
     * method that fills combobox with users
     */
    public void setEmployeeOptions(){
        for(User u : chosenGroup.getUserList()){
            employee.getItems().add(u.getFirstName() + " " + u.getLastName());
        }
    }
    /**
     * setter for stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        init();
    }
    /**
     * initialization
     */
    private void init() {
        setPriorityOptions();
        setEmployeeOptions();
    }

    /**
     * setter for group
     */
    public void setGroup(Group chosenGroup) {
        this.chosenGroup = chosenGroup;
    }

    /**
     * Method that creates new task from user input
     * @param event
     */
    public void createNewTask(MouseEvent event) {
        if(employee.getSelectionModel().isEmpty() || timeFrom.getDateTimeValue().toString().isEmpty() || timeTo.getDateTimeValue().toString().isEmpty()){
            errorMessage.setStyle("-fx-text-fill: #ff0000;\n -fx-font-size: 16px;\n -fx-font-family: sans-serif;\n -fx-text-alignment: left;");
            errorMessage.setText("Fill all things that need to be filled");

        }else {
            User user = getChosernUser();
            LocalDateTime from = timeFrom.getDateTimeValue();
            LocalDateTime to = timeTo.getDateTimeValue();
            int prio = priority.getSelectionModel().getSelectedIndex() + 1;
            System.out.println(prio);
            Task task = new Task(from,to,prio);
            task.setName(taskName.getText());
            task.setDescription(taskDesc.getText());
            try{
                DatabaseHandeler dh = new DatabaseHandeler();
                dh.connect();
                dh.creteTask(task,user);
                dh.endConnection();
            }catch (Exception e){
                e.printStackTrace();
            }
            close();
        }
    }

    /**
     * Returns user chosen by name in combobox
     * @return
     */
    private User getChosernUser() {
        for(User u : chosenGroup.getUserList()){
            if((u.getFirstName() +" "+ u.getLastName()).equals(employee.getSelectionModel().getSelectedItem().toString())){
                return u;
            }
        }
        return new User();
    }

    /**
     * exit stage
     */
    private void close(){
        stage.close();
    }
}
