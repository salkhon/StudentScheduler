package com.studsched.controller;

import com.studsched.model.ClientSchedule;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CreateScheduleController {
    public DatePicker datePicker;

    @FXML
    private Spinner<Integer> timeHourSpinner;
    @FXML
    private Spinner<Integer> timeMinuteSpinner;
    @FXML
    private ChoiceBox<String> timeAmPmChoiceBox;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField titleTextField;

    private String clientId;

    private BooleanProperty areAllFieldsFilled;

    private ClientSchedule completedClientSchedule;

    public void initialize() {
        this.completedClientSchedule = null;
        this.areAllFieldsFilled = new SimpleBooleanProperty(false);
        this.createButton.setDisable(true);
        this.createButton.disableProperty().bind(this.areAllFieldsFilled.not());
        this.timeAmPmChoiceBox.getItems().addAll("AM", "PM");
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @FXML
    public void checkFieldsFilled() {
        this.areAllFieldsFilled.set(!this.titleTextField.getText().isEmpty() && this.datePicker.getValue() != null &&
                this.timeHourSpinner.getValue() != null && this.timeMinuteSpinner.getValue() != null &&
                this.timeAmPmChoiceBox.getValue() != null && !this.noteTextArea.getText().isEmpty());
    }

    public void createClientSchedule(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.createButton) {
            this.completedClientSchedule = new ClientSchedule(this.clientId, this.datePicker.getValue(),
                    getLocalTimeFromSpinners(), this.titleTextField.getText(), this.noteTextArea.getText());
        } else if (actionEvent.getSource() == this.cancelButton) {
            this.completedClientSchedule = null;
        }
        closeWindow(actionEvent);
    }

    private LocalTime getLocalTimeFromSpinners() {
        String hour = this.timeHourSpinner.getValue().toString();
        if (this.timeHourSpinner.getValue() < 10) {
            hour = '0' + hour;
        }
        String minute = this.timeMinuteSpinner.getValue().toString();
        if (this.timeMinuteSpinner.getValue() < 10) {
            minute = '0' + minute;
        }
        return LocalTime.parse("" + hour + ":" + minute + " " + this.timeAmPmChoiceBox.getValue(),
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }

    private void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource())
                .getScene().getWindow())
                .close();
    }

    public ClientSchedule getCompletedClientSchedule() {
        return this.completedClientSchedule;
    }
}
