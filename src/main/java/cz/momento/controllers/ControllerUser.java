package cz.momento.controllers;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerUser {
    public Button openFile;
    final FileChooser fileChooser = new FileChooser();
    private Desktop desktop = Desktop.getDesktop();
    private Stage stage = null;

    public ControllerUser(){
        //setOpenFile();
    }

    public void setOpenFile(){
        openFile.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                openFile(file);
            }
        });
    }

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    ControllerUser.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
}
