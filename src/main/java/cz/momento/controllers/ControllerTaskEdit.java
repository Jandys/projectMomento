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

public class ControllerTaskEdit {
    public TextField employee;
    public ComboBox priority;
    private Group chosenGroup;
    private Stage stage;
    private Task task;
    private User user;

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
        employee.setText(user.getFirstName() + " " + user.getLastName());
        taskName.setText(task.getName());
        taskDesc.setText(task.getDescription());
        timeFrom.setDateTimeValue(task.getTimeFrom());
        timeTo.setDateTimeValue(task.getTimeTo());
        priority.getSelectionModel().select(task.getPriority() - 1);
    }

    /**
     * setter for task
     * @param task
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * setter for user
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method that updates selected task
     * @param event
     */
    public void updateTask(MouseEvent event) {
        if(timeFrom.getDateTimeValue().toString().isEmpty() || timeTo.getDateTimeValue().toString().isEmpty()){
            errorMessage.setStyle("-fx-text-fill: #ff0000;\n -fx-font-size: 16px;\n -fx-font-family: sans-serif;\n -fx-text-alignment: left;");
            errorMessage.setText("Fill all things that need to be filled");

        }else {
            LocalDateTime from = timeFrom.getDateTimeValue();
            LocalDateTime to = timeTo.getDateTimeValue();
            int prio = priority.getSelectionModel().getSelectedIndex() + 1;
            System.out.println(prio);
            Task task = new Task(from,to,prio);
            task.setStatus(this.task.getStatus());
            task.setId(this.task.getId());
            task.setName(taskName.getText());
            task.setDescription(taskDesc.getText());
            try{
                DatabaseHandeler dh = new DatabaseHandeler();
                dh.connect();
                dh.editTask(user, task);
                dh.endConnection();
            }catch (Exception e){
                e.printStackTrace();
            }
            close();
        }
    }

    /**
     * closing stage
     */
    private void close(){
        stage.close();
    }
}
