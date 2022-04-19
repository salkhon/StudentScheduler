package com.studsched.model;

import com.studsched.controller.LoginFormController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginFormLoader = new FXMLLoader(getClass().getResource("..//view//LoginForm.fxml"));
        try {
            DatabaseManager.openConnection();
        } catch (SQLException sqlException) {
            System.out.println("Error opening database");
            Platform.exit();
        }
        stage.setScene(new Scene(loginFormLoader.load()));
        ((LoginFormController) loginFormLoader.getController()).setPrimaryStage(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        try {
            DatabaseManager.closeConnection();
        } catch (SQLException sqlException) {
            System.out.println("Error closing database");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
