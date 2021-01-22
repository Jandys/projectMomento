package cz.momento.controllers;

import cz.momento.Group;
import cz.momento.Main;
import cz.momento.Task;
import cz.momento.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;


public class ControllerCalendar {
    public DatePicker datePicker;
    public MenuBar menuBar;
    private Stage stage;
    public GridPane gridCalendar;
    public Label labelDate;

    public void init() {
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        labelDate.setText(today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        for (int i = 1; i <= 6;  i++) {
            gridCalendar.add(new Label(String.valueOf(7 + i)), i, 0 );
        }
        for (int i = 1; i <=4; i++) {
            gridCalendar.add(new Label("A"), 0, i);
        }
    }

    /**
     * Setter of a stage
     * @param stage is a stage
     */
    public void setStage(Stage stage) {
        init();
        this.stage = stage;
    }

    public void pickDate(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        labelDate.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    }

    public void update() {
        updateUsers();
    }

    private void updateTasks() {
    }

    private void updateHours(Group group) {
        int columnIndex = 1;
        for(int i = hourMin(group); i <= hourMax(group); i++) {
            gridCalendar.add(new Label(String.valueOf(i)), columnIndex, 0);
            columnIndex++;
        }
    }

    private void updateUsers() {
        Group group = new Group();
        int rowIndex = 1;
        for (Object object: group.getUserList()) {
            User user = User.class.cast(object);
            gridCalendar.add(new Label(user.getName()), 0, rowIndex);
            rowIndex++;
        }
        updateHours(group);
        updateTasks();
    }

    private int hourMin(Group group) {
        int hour = 8;
        for (Object object: group.getUserList()) {
            User user = User.class.cast(object);
            for (Task task: user.getTasks()) {
                if(task.getTimeFrom().getHour() < hour) {
                    hour = task.getTimeFrom().getHour();
                }
            }
        }
        return hour;
    }

    private int hourMax(Group group) {
        int hour = 14;
        for (Object object: group.getUserList()) {
            User user = User.class.cast(object);
            for (Task task: user.getTasks()) {
                if(task.getTimeFrom().getHour() > hour) {
                    hour = task.getTimeFrom().getHour();
                }
            }
        }
        return hour;
    }

    public void RegisterUser(ActionEvent actionEvent) {
        try {
            Main main = new Main();
            main.register();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pickUser(ActionEvent actionEvent) throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);

        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("calendar.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerCalendar controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }
}
