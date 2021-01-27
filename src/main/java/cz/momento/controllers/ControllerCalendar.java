package cz.momento.controllers;

import cz.momento.Group;
import cz.momento.Main;
import cz.momento.Task;
import cz.momento.User;
import cz.momento.database.DatabaseHandeler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;


public class ControllerCalendar {
    User user1 = new User("Jakub", "Hosek");
    Task task1 = new Task(LocalDateTime.of(LocalDate.of(2021,1,22), LocalTime.of(7,30)), LocalDateTime.of(LocalDate.of(2021,1,22), LocalTime.of(20,45)), 2);

    private User me;

    public DatePicker datePicker;
    public MenuBar menuBar;
    private Stage stage;
    public GridPane gridCalendar;
    public Label labelDate;
    public Menu groupChooser;

    private Group chosenGroup;
    int minHour;
    int maxHour;

    public void init() {
        gridCalendar.setGridLinesVisible(true);
        user1.addTask(task1);
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        labelDate.setText(today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));


        update();
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
        update();
    }

    public void update() {
        updateUsers();
        updateGroupChooser();
    }

    private void updateGroupChooser() {
        String groups;
        try{
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            if(dh.getGroup(me.getCryptedLogin()) == null){
                groups = "";
            }else {
                groups = dh.getGroup(me.getCryptedLogin());
            }
            String groupsSplitted[] = groups.split(",");
            for(String s : groupsSplitted){
                MenuItem mi = new MenuItem(s);
                mi.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                       System.out.println(event.getEventType().getName());
                       //TODO odtud se budou setovat dalsi timeliny podle zvoleny groupy
                    }
                });
                groupChooser.getItems().add(mi);
            }
            dh.endConnection();
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    private void updateTasks(Group group) {
        int rowIndex = 1;
        int columnIndexStart;
        int columnIndexEnd;
        for (User user: group.getUserList()) {
            for (Task task: user.getTasks()) {
                if (isSelectedDay(task)) {
                    columnIndexStart = getQuarterIndex(task.getTimeFrom()) + 1;
                    columnIndexEnd = getQuarterIndex(task.getTimeTo()) + 1;
                    VBox box = new VBox();
                    Label label = new Label("task");
                    box.getChildren().add(label);
                    box.setAlignment(Pos.CENTER);
                    box.setBackground(new Background(new BackgroundFill(Color.rgb(125,125,125), CornerRadii.EMPTY, Insets.EMPTY)));
                    gridCalendar.add(box, columnIndexStart, rowIndex, columnIndexEnd - columnIndexStart, 1);
                }
            }
            rowIndex++;
        }
    }

    private void updateHours(Group group) {
        minHour = hourMin(group);
        maxHour = hourMax(group);

        gridCalendar.getColumnConstraints().add(0, new ColumnConstraints(100, 100, 200));
        for (int x = 1; x < (maxHour-minHour+1)*4+1; x++) {
            gridCalendar.getColumnConstraints().add(x, new ColumnConstraints(20, 20, 20));
        }

        int columnIndex = 1;
        for(int i = minHour; i <= maxHour; i++) {
            Label label = new Label(String.valueOf(i));
            VBox box = new VBox();
            box.getChildren().add(label);
            box.setAlignment(Pos.CENTER);
//            GridPane.setHalignment(label, HPos.CENTER);
//            GridPane.setValignment(label, VPos.CENTER);
//            box.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            gridCalendar.add(box, columnIndex, 0, 4, 1);
            columnIndex += 4;
        }
    }

    private void updateUsers() {
        gridCalendar.getChildren().clear();
        gridCalendar.setGridLinesVisible(true);
        Group group = new Group();
        group.addUserToGroup(user1);
        int rowIndex = 1;
        for (User user: group.getUserList()) {
            gridCalendar.add(new Label(user.getFirstName() + " " + user.getLastName()), 0, rowIndex);
            rowIndex++;
        }
        updateHours(group);
        updateTasks(group);
    }

    private int hourMin(Group group) {
        int hour = 8;
        for (User user: group.getUserList()) {
            for (Task task: user.getTasks()) {
                if(isSelectedDay(task) && task.getTimeFrom().getHour() < hour) {
                    hour = task.getTimeFrom().getHour();
                }
            }
        }
        return hour;
    }

    private int hourMax(Group group) {
        int hour = 13;
        for (User user: group.getUserList()) {
            for (Task task: user.getTasks()) {
                if(isSelectedDay(task) && task.getTimeTo().getHour() > hour) {
                    hour = task.getTimeTo().getHour();
                }
            }
        }
        return hour;
    }

    public void RegisterUser(ActionEvent actionEvent) {
        try {
            Main main = new Main();
            main.user();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logOutUser(ActionEvent actionEvent) {

    }

    public void exitApp(ActionEvent actionEvent) {
        Platform.exit();
    }

    private int getQuarterIndex(LocalDateTime time) {
        int result;
        result = (time.getHour() - minHour) * 4;
        result += Math.floorDiv(time.getMinute(), 15);

        return result;
    }

    private boolean isSelectedDay(Task task) {
        return datePicker.getValue().compareTo(task.getTimeTo().toLocalDate()) >= 0 &&
                datePicker.getValue().compareTo(task.getTimeFrom().toLocalDate()) <= 0;
    }

    public void TaskCreation(ActionEvent actionEvent) {
        try {
            Main main = new Main();
            main.task();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void leaveAll(ActionEvent actionEvent) {
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
           // dh.clearGroup();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addGroup(ActionEvent actionEvent) {
    }

    public void leaveGroup(ActionEvent actionEvent) {
    }

    public void setLoggedUser(String cryptedLogin, String hashPass){
        me = new User();
        me.setCryptedLogin(cryptedLogin);
        try{
            DatabaseHandeler dh = new DatabaseHandeler();
            //TODO from database to user setter
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
