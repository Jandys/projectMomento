package cz.momento;

import cz.momento.controllers.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.InputStream;

public class Main extends Application {

    final static Image ICON = new Image("icon.png");
    private ControllerCalendar calendarController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(ICON);

        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("login.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        ControllerLogin controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Method that creates new stage of register scene
     * @throws Exception
     */
    public void register() throws Exception{
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.getIcons().add(ICON);


        stage.setOnCloseRequest(event -> {
            try {
                Stage s2 = new Stage();
                start(s2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("register.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerRegister controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }

    public void calendar(String cryptedLogin, String hashPass) throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.getIcons().add(ICON);

        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("calendar.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        calendarController = loader.getController();
        calendarController.setLoggedUser(cryptedLogin,hashPass);
        calendarController.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }

    public static void task(Group chosenGroup, EventHandler<WindowEvent> onHidden) throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.getIcons().add(ICON);
        stage.setOnHidden(onHidden);


        FXMLLoader loader = new FXMLLoader();
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("task.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerTask controller = loader.getController();
        controller.setGroup(chosenGroup);
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }

    public void user() throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.getIcons().add(ICON);


        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("user.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerUser controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }

    public static void taskEdit(User user, Task task, EventHandler<WindowEvent> onHidden) throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.getIcons().add(ICON);
        stage.setOnHidden(onHidden);


        FXMLLoader loader = new FXMLLoader();
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("taskEdit.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerTaskEdit controller = loader.getController();
        controller.setUser(user);
        controller.setTask(task);
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }
}
