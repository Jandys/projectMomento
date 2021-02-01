package cz.momento.controllers;

import cz.momento.Group;
import cz.momento.Main;
import cz.momento.Task;
import cz.momento.User;
import cz.momento.database.DatabaseHandeler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Class ControllerCalendar which implements and handle displaying tasks of selected group in selected day
 */
public class ControllerCalendar {
    private User me;

    public DatePicker datePicker;
    public MenuBar menuBar;
    private Stage stage;
    public GridPane gridCalendar;
    public Label labelDate;
    public Menu groupChooser;

    private Group chosenGroup = new Group();
    int minHour;
    int maxHour;
    int maxNameLenght = 0;
    private ContextMenu menu;


    /**
     * Method which initialize the graphic interface and set the default values
     */
    public void init() {
        chosenGroup.addUserToGroup(me);
        menu = new ContextMenu();

        gridCalendar.setGridLinesVisible(true);

        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        labelDate.setText(today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));


        setChosenGroupToTheFirstGroup();
        update();
    }

    /**
     * Method which initialize first group as chosen
     */
    private void setChosenGroupToTheFirstGroup() {
        try {
            String groups;
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            if(dh.getGroup(me.getCryptedLogin()) == null){
                groups = "";
            }else {
                groups = dh.getGroup(me.getCryptedLogin());
            }
            String groupsSplitted[] = groups.split(",");
            if(groupsSplitted[0].isEmpty()){
                Group gr = new Group();
                gr.addUserToGroup(me);
                chosenGroup = gr;
            }else {
                chosenGroup = chooseGroupByName(groupsSplitted[0]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Setter of a stage
     * @param stage is a stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        init();
    }

    /**
     * Method which handle date picking and overwriting shown day of week
     * @param event event for recognizing the action
     */
    public void pickDate(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        labelDate.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        update();
    }

    /**
     * Method which is called after creation of task for update of calendar
     */
    public void updateAfterTaskCreation() {
        if(chosenGroup.getName()!= null && !chosenGroup.getName().isEmpty()) {
            chosenGroup = chooseGroupByName(chosenGroup.getName());
        }
        update();
    }

    /**
     * Method which updates the contents of graphical display
     */
    public void update() {
        updateGroupChooser();
        updateUsers();
        gridCalendar.getColumnConstraints().get(0).setMinWidth(maxNameLenght*9);
    }

    /**
     * Method which updates list of groups that the user is in
     */
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
            groupChooser.getItems().clear();
            for(String s : groupsSplitted){
                MenuItem mi = new MenuItem(s);
                mi.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(groupsSplitted[0].isEmpty()){
                            Group gr = new Group();
                            gr.addUserToGroup(me);
                            chosenGroup = gr;
                        }else {
                            chosenGroup = chooseGroupByName(mi.getText());
                        }
                        update();
                    }
                });
                groupChooser.getItems().add(mi);
            }
            dh.endConnection();
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    /**
     * Method which choose group by its name
     * @param text name of chosen group
     * @return Group
     */
    private Group chooseGroupByName(String text) {
        Group rGroup = new Group();
        rGroup.setName(text);
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
            rGroup = dh.getUserWithGroup(rGroup);
        } catch (Exception e){
            e.printStackTrace();
        }
        return rGroup;
    }

    /**
     * Method which handle display of tasks
     * @param group group from which the tasks should be displayed
     */
    private void updateTasks(Group group) {
        int rowIndex = 1;
        int columnIndexStart;
        int columnIndexEnd;
        for (User user: group.getUserList()) {
            for (Task task: user.getTasks()) {
                if (isSelectedDay(task)) {
                    columnIndexStart = getQuarterIndex(task.getTimeFrom()) + 1;
                    columnIndexEnd = getQuarterIndex(task.getTimeTo()) + 1;
                    while(columnIndexEnd - columnIndexStart <1) columnIndexEnd += 1;
                    VBox box = new VBox();
                    Label label = new Label(task.getName());
                    label.setTooltip(new Tooltip(task.getStatus().toUpperCase()+": " + task.getName() +":\n   "+task.getDescription()));
                    box.getChildren().add(label);
                    box.setAlignment(Pos.CENTER);
                    box.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                        @Override
                        public void handle(ContextMenuEvent event) {
                            menu.hide();
                            Collection<MenuItem> menuList = new ArrayList<>();
                            menuList.add(createMenuItem("edit", new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        Main.taskEdit(user, task, event1 -> {
                                            updateAfterTaskCreation();});
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                            menuList.add(createMenuItem("close", new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    alertOnTaskClosing(user,task);
                                }
                            }));
                            menu.getItems().setAll(menuList);
                            menu.show(box, event.getScreenX(), event.getScreenY());
                        }
                    });
                    box.setBackground(new Background(new BackgroundFill(getColorByPriority(task.getPriority(),task.getStatus()), CornerRadii.EMPTY, Insets.EMPTY)));
                    gridCalendar.add(box, columnIndexStart, rowIndex, columnIndexEnd - columnIndexStart, 1);
                }
            }
            rowIndex++;
        }
    }

    /**
     * Method which change the status of task from opened to closed through alert if the task is opened
     * @param task task which should be altered
     * @param user user whose task it should be
     * @return boolean
     */
    private boolean alertOnTaskClosing(User user, Task task) {
        if(task.getStatus().equals("closed")){
            return false;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Closing task "+task.getName());
            alert.setHeaderText("Do you want to close this task?");
            alert.setContentText("By clicking on OK status of this task will be changed on *CLOSED*");
            alert.showAndWait().ifPresent(buttonType -> {
                if(buttonType == ButtonType.OK) {
                    task.setStatus("closed");
                    updateTask(user, task);
                    update();
                }
            });
        }
        return true;

    }

    /**
     * Method which update task on database server
     * @param task task which should be altered
     * @param user user whose task it should be
     */
    private void updateTask(User user, Task task) {
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            dh.editTask(user, task);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Method which set color by priority
     * @param priority priority which task have
     * @param status status which task have
     */
    private Paint getColorByPriority(int priority,String status) {
        if(status.equals("closed")){
            return Color.rgb(200,200,200);
        }
        switch (priority){
            case 0:
                return Color.rgb(153,255,255);
            case 1:
                return Color.rgb(48,102,200);
            case 2:
                return Color.rgb(102,255,100);
            case 3:
                return Color.rgb(255,255,30);
            case 4:
                return Color.rgb(255,128,0);
            case 5:
                return Color.rgb(255,0,0);
            default:
                return Color.rgb(125,125,125);
        }
    }

    /**
     * Method which update graphic display of range of hours
     * @param group group which is selected
     */
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
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setValignment(label, VPos.CENTER);
            box.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            gridCalendar.add(box, columnIndex, 0, 4, 1);
            columnIndex += 4;
        }
    }

    /**
     * Method which update users who are be displayed
     */
    private void updateUsers() {
        gridCalendar.getChildren().clear();
        gridCalendar.setGridLinesVisible(true);
        int rowIndex = 1;
        for (User user: chosenGroup.getUserList()) {
            String userName = user.getFirstName() + " " + user.getLastName();
            if(maxNameLenght < userName.length()) {
                maxNameLenght = userName.length();
            }
            gridCalendar.add(new Label(userName), 0, rowIndex);
            rowIndex++;
        }

        updateHours(chosenGroup);
        updateTasks(chosenGroup);
    }

    /**
     * Method which finds out minimum hour that needs to be displayed
     * @param group group which is chosen
     * @return int
     */
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

    /**
     * Method which finds out maximum hour that needs to be displayed
     * @param group group which is chosen
     * @return int
     */
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

//    /**
//     *
//     */
//    public void RegisterUser(ActionEvent actionEvent) {
//        try {
//            Main.user();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * Method which handle log out process
     * @param actionEvent event for recognizing the action
     */
    public void logOutUser(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are You Sure?");
        alert.setHeaderText("Do you want to log out?");
        alert.setContentText("Are you absolutely sure that you want to log out?\nBy clicking on OK you will be logged out");
        alert.setResult(ButtonType.CANCEL);
        alert.showAndWait().ifPresent(buttonType -> {
            if(buttonType == ButtonType.OK){
                try{
                    Main main = new Main();
                    main.start(stage);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                alert.close();
            }

        });

    }

    /**
     * Method which close all windows and exit application
     * @param actionEvent event for recognizing the action
     */
    public void exitApp(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * Method which finds out index of time given in timetable
     * @param time time which should be calculated
     * @return int
     */
    private int getQuarterIndex(LocalDateTime time) {
        int result;
        result = (time.getHour() - minHour) * 4;
        result += Math.floorDiv(time.getMinute(), 15);


        return result;
    }

    /**
     * Method which finds out if the task day is same as selected day
     * @param task task which should be checked
     * @return boolean
     */
    private boolean isSelectedDay(Task task) {
        return datePicker.getValue().compareTo(task.getTimeTo().toLocalDate()) >= 0 &&
                datePicker.getValue().compareTo(task.getTimeFrom().toLocalDate()) <= 0;
    }

    /**
     * Method which handle the process of creating task
     * @param actionEvent event for recognizing the action
     */
    public void TaskCreation(ActionEvent actionEvent) {
        try {
            Main.task(chosenGroup, event -> {
                updateAfterTaskCreation();
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method which handle leave of all groups
     * @param actionEvent event for recognizing the action
     */
    public void leaveAll(ActionEvent actionEvent) {
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            dh.clearGroup(me.getCryptedLogin());
            dh.endConnection();
            update();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method which handle display and addition user to group
     * @param actionEvent event for recognizing the action
     */
    @FXML
    public void addGroup(ActionEvent actionEvent) {
        Stage stage = new Stage();
        StackPane root = new StackPane();
        Label name = new Label("Name of group to add: ");
        name.setAlignment(Pos.TOP_CENTER);
        name.setPadding(new Insets(10));
        name.setStyle("    -fx-font-family: sans-serif;\n" +
                "    -fx-font-weight: bold italic;\n" +
                "    -fx-font-size: 19px;");
        TextField txtF = new TextField();
        txtF.promptTextProperty().set("name of the group");
        txtF.setAlignment(Pos.CENTER);
        txtF.setPadding(new Insets(10));
        Button btn = new Button();
        btn.setText("Add to groups");
        btn.setAlignment(Pos.BOTTOM_CENTER);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-font-family: sans-serif;\n" +
                "    -fx-font-size: 14px;");
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    stage.getScene().setCursor(Cursor.WAIT);
                    DatabaseHandeler dh = new DatabaseHandeler();
                    dh.connect();
                    dh.editGrop(txtF.getText(),me.getCryptedLogin());
                    dh.endConnection();
                    update();
                }catch (Exception e){
                    e.printStackTrace();
                }
                stage.close();
            }
        });
        VBox box = new VBox();
        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.getChildren().add(name);
        box.getChildren().add(hbox2);
        box.getChildren().add(txtF);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(btn);
        box.getChildren().add(hBox);

        root.getChildren().add(box);

        root.setStyle("-fx-background-color: linear-gradient(from 25px 25px to 30px 30px, reflect, #d4dbff 50%, #afb7c8 60%)");

        Scene addGroup = new Scene(root,300,130);
        stage.getIcons().add( new Image("icon.png"));
        stage.setResizable(false);

        stage.setScene(addGroup);
        stage.show();
    }

    /**
     * Method which handle choosing of group and then leaving the group
     * @param actionEvent event for recognizing the action
     */
    public void leaveGroup(ActionEvent actionEvent) {
        Stage stage = new Stage();
        StackPane root = new StackPane();
        Label name = new Label("Click on group you want to leave: ");
        name.setAlignment(Pos.TOP_CENTER);
        name.setPadding(new Insets(10));
        name.setStyle("    -fx-font-family: sans-serif;\n" +
                "    -fx-font-weight: bold italic;\n" +
                "    -fx-font-size: 19px;");

        ComboBox<String> comboBox = new ComboBox<>();
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            String groups = dh.getGroup(me.getCryptedLogin());
            comboBox.getItems().addAll(groups.split(","));


            dh.endConnection();

        } catch (Exception e){
            e.printStackTrace();
        }


        Button btn = new Button();
        btn.setText("Leave selected Group");
        btn.setAlignment(Pos.BOTTOM_CENTER);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-font-family: sans-serif;\n" +
                "    -fx-font-size: 14px;");
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selection = comboBox.getSelectionModel().getSelectedItem();
                try {
                    DatabaseHandeler dh = new DatabaseHandeler();
                    dh.connect();
                    String[] separatedGroups = dh.getGroup(me.getCryptedLogin()).split(",");
                    ArrayList<String> ars = new ArrayList<>();
                    ars.addAll(Arrays.asList(separatedGroups));
                    ars.remove(selection);
                    dh.clearGroup(me.getCryptedLogin());
                    for(String s : ars){
                        dh.editGrop(s,me.getCryptedLogin());
                    }
                    dh.endConnection();
                    stage.close();
                    update();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        VBox box = new VBox();
        HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        hBox3.setAlignment(Pos.CENTER);

        hBox.getChildren().add(name);
        hBox2.getChildren().add(comboBox);
        hBox3.getChildren().add(btn);

        box.getChildren().add(hBox);
        box.getChildren().add(hBox2);
        box.getChildren().add(hBox3);

        root.getChildren().add(box);

        root.setStyle("-fx-background-color: linear-gradient(from 25px 25px to 30px 30px, reflect, #d4dbff 50%, #afb7c8 60%)");

        Scene addGroup = new Scene(root);
        stage.getIcons().add( new Image("icon.png"));
        stage.setResizable(false);

        stage.setScene(addGroup);
        stage.show();
    }

    /**
     * Method which sets logged user
     * @param encryptedLogin login name
     * @param hashPass password
     */
    public void setLoggedUser(String encryptedLogin, String hashPass){
        try{
            DatabaseHandeler dh = new DatabaseHandeler();
            me = dh.setLoggedUser(encryptedLogin,hashPass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method which create MenuItem and return it.
     * @param name name of option which should be added to list of options
     * @param onAction handler of action preformed on MenuItem
     * @return MenuItem
     */
    private MenuItem createMenuItem(String name, EventHandler<ActionEvent> onAction) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(onAction);
        return menuItem;
    }

}
