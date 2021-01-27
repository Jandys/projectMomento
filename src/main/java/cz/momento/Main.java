package cz.momento;

import cz.momento.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    final Image ICON = new Image("icon.png");


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
        ControllerCalendar controller = loader.getController();
        controller.setLoggedUser(cryptedLogin,hashPass);
        controller.setStage(stage);
        stage.setScene(scene);

        stage.show();
    }

    public void task() throws Exception{
        Stage stage = new Stage();
        stage.setFullScreen(false);
        stage.setResizable(true);
        stage.getIcons().add(ICON);


        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("task.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        ControllerTask controller = loader.getController();
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
}
