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
                chosenGroup = choseGroupByName(groupsSplitted[0]);
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

    public void pickDate(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        labelDate.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        update();
    }

    public void updateAfterTaskCreation() {
        if(chosenGroup.getName()!= null && !chosenGroup.getName().isEmpty()) {
            chosenGroup = choseGroupByName(chosenGroup.getName());
        }
        update();
    }

    public void update() {
        updateGroupChooser();
        updateUsers();
        gridCalendar.getColumnConstraints().get(0).setMinWidth(maxNameLenght*9);
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
                            chosenGroup = choseGroupByName(mi.getText());
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

    private Group choseGroupByName(String text) {
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
//                    box.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                        @Override
//                        public void handle(MouseEvent event) {
//                            alertOnTaskClosing(user,task);
//                        }
//                    });
                    box.setBackground(new Background(new BackgroundFill(getColorByPriority(task.getPriority(),task.getStatus()), CornerRadii.EMPTY, Insets.EMPTY)));
                    gridCalendar.add(box, columnIndexStart, rowIndex, columnIndexEnd - columnIndexStart, 1);
                }
            }
            rowIndex++;
        }
    }

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

    private void updateTask(User user, Task task) {
        try {
            DatabaseHandeler dh = new DatabaseHandeler();
            dh.connect();
            dh.editTask(user, task);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

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
            Main.task(chosenGroup, event -> {
                updateAfterTaskCreation();
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

    public void setLoggedUser(String cryptedLogin, String hashPass){
        try{
            DatabaseHandeler dh = new DatabaseHandeler();
            me = dh.setLoggedUser(cryptedLogin,hashPass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Metoda, která vytvoří MenuItem a vrátí ho, pro potřeby věcí nebo postav
     * @param name příkaz, který se má s danout věcí vykonat
     */
    private MenuItem createMenuItem(String name, EventHandler<ActionEvent> onAction) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(onAction);
        return menuItem;
    }

}
