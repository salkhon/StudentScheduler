package com.studsched.controller;

import com.studsched.model.Client;
import com.studsched.model.DataFetch;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.xml.crypto.dsig.SignatureMethod;
import java.sql.SQLException;

public class CreateClientController {
    private Stage primaryStage;
    @FXML
    private TextField studentIdText;
    @FXML
    private TextField fullNameText;
    @FXML
    private TextField emailText;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordConfirmation;
    @FXML
    private Label studentIdInvalidText;
    @FXML
    private Button createButton;
    @FXML
    private Button goBackButton;
    @FXML
    public Label confirmPasswordMismatchLabel;

    private SimpleBooleanProperty isClientFormCompleteProperty;

    public void initialize() {
        this.studentIdInvalidText.setVisible(false);
        this.confirmPasswordMismatchLabel.setVisible(false);
        this.createButton.setDisable(true);

        this.isClientFormCompleteProperty = new SimpleBooleanProperty(false);
        this.isClientFormCompleteProperty.addListener((observableValue, aBoolean, t1) -> CreateClientController.this.createButton.setDisable(!t1));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleKeyReleased(KeyEvent keyEvent) {
        this.isClientFormCompleteProperty.set(clientFieldsComplete());
    }

    public Client getCompleteClient() {
        Client client = null;
        if (this.isClientFormCompleteProperty.get()) {
            client = new Client(this.studentIdText.getText(), this.fullNameText.getText(),
                    this.emailText.getText(), this.password.getText());
        }
        return client;
    }

    private boolean clientFieldsComplete() {
        return !this.studentIdText.getText().isEmpty() && !this.fullNameText.getText().isEmpty() &&
                !this.emailText.getText().isEmpty() && !this.password.getText().isEmpty() &&
                !this.passwordConfirmation.getText().isEmpty();
    }

    public boolean isClientFormComplete() {
        return this.isClientFormCompleteProperty.get();
    }

    @FXML
    public void closeClientCreation(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.createButton) {
            // client field has to be complete to enter here
            try {
                if (validateFields()) {
                    closeWindow(actionEvent);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                System.out.println("Could not validate client creation");
            }
        } else if (actionEvent.getSource() == this.goBackButton) {
            closeWindow(actionEvent);
        }
    }

    private boolean validateFields() throws SQLException {
        boolean isValidFields = false;
        if (!DataFetch.clientExists(this.studentIdText.getText())) {
            this.studentIdInvalidText.setVisible(false);
            if (passwordsMatch()) {
                this.confirmPasswordMismatchLabel.setVisible(false);
                isValidFields = true;
            } else {
                this.confirmPasswordMismatchLabel.setVisible(true);
            }
        } else {
            this.studentIdInvalidText.setVisible(true);
        }
        return isValidFields;
    }

    private void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource())
                .getScene().getWindow())
                .close();
    }

    private boolean passwordsMatch() {
        return this.passwordConfirmation.getText().equals(this.password.getText());
    }
}
