package cz.momento;

import cz.momento.controllers.ControllerLogin;
import cz.momento.controllers.ControllerRegister;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(true);


        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("login.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

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
        FXMLLoader loader = new FXMLLoader();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("register.fxml");
        Parent root = loader.load(stream);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        ControllerRegister controller = loader.getController();
        controller.setStage(stage);
        stage.show();
    }


}
