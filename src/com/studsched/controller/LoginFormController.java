package com.studsched.controller;

import com.studsched.model.Client;
import com.studsched.model.DataFetch;
import com.studsched.model.DataStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class LoginFormController {
    private Stage primaryStage;
    @FXML
    public TextField studentIDText;
    @FXML
    public PasswordField passwordText;
    @FXML
    public Button loginButton;
    @FXML
    public Label wrongCredLabel;
    @FXML
    public ProgressIndicator authProgress;


    public void initialize() {
        this.wrongCredLabel.setVisible(false);
        this.authProgress.setVisible(false);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        try {
            Client client = DataFetch.fetchClient(this.studentIDText.getText(), this.passwordText.getText());
            if (client != null) {
                System.out.println("SUCCESSFUL LOGIN");
                this.wrongCredLabel.setVisible(false);
                loadClientPage(client);
            } else {
                this.wrongCredLabel.setVisible(true);
            }
        } catch (SQLException sqlException) {
            this.wrongCredLabel.setText("Database error. Could not fetch data");
            System.out.println("Database error. Could not fetch data: ");
            sqlException.printStackTrace();
        } catch (IOException ioException) {
            this.wrongCredLabel.setText("Could not transition scene");
            System.out.println("Could not load main screen or login screen");
            ioException.printStackTrace();
        }
    }

    // loads main scene on primary stage
    private void loadClientPage(Client client) throws IOException {
        this.primaryStage.hide();
        FXMLLoader mainSceneLoader = new FXMLLoader(getClass().getResource("..//view//MainScene.fxml"));

        this.primaryStage.setScene(new Scene(mainSceneLoader.load()));
        MainSceneController mainSceneController = mainSceneLoader.getController();
        mainSceneController.setPageInformation(client, this.primaryStage);

        this.primaryStage.show();
    }

    @FXML
    public void handleCreateClient() {
        FXMLLoader createClientLoader = new FXMLLoader(getClass().getResource("..//view//CreateClient.fxml"));
        Stage newClientStage = new Stage();
        newClientStage.initOwner(this.primaryStage);
        newClientStage.initModality(Modality.WINDOW_MODAL);
        try {
            newClientStage.setScene(new Scene(createClientLoader.load()));
        } catch (IOException ioException) {
            this.wrongCredLabel.setText("Could not open form");
            return;
        }
        CreateClientController createClientController = createClientLoader.getController();
        createClientController.setPrimaryStage(this.primaryStage);

        newClientStage.showAndWait();

        try {
            manageNewClientCreation(createClientController.getCompleteClient());
        } catch (IOException ioException) {
            this.wrongCredLabel.setText("Could not load new Client");
        }
    }

    private void manageNewClientCreation(Client newClient) throws IOException {
        if (newClient != null) {
            try {
                DataStore.storeNewClient(newClient);
            } catch (SQLException sqlException) {
                this.wrongCredLabel.setText("Could not create new client");
                return;
            }
            loadClientPage(newClient);
        }
    }
}
