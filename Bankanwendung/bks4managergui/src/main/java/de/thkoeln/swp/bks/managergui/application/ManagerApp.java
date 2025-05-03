package de.thkoeln.swp.bks.managergui.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage)
    {
        Parent content;
        try {

            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/manager.fxml"));
            content = viewLoader.load();

            stage = new Stage();
            stage.setTitle("SWP - BKS - Manager");
            stage.setScene(new Scene(content, 800, 800));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeManagerApp()
    {
        if(stage != null)
            stage.close();
    }

    public static void main(String[] args) { launch(args); }
}
