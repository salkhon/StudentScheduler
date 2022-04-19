package com.studsched.controller;

import com.studsched.model.Client;
import com.studsched.model.ClientSchedule;
import com.studsched.model.DataStore;
import com.studsched.model.DatabaseManager;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MainSceneController {
    private Stage primaryStage;
    private Client client;

    @FXML
    public Button newButton;
    @FXML
    public Button saveButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button nextWeekButton;
    @FXML
    public Button prevWeekButton;
    @FXML
    public Button currentWeekButton;
    @FXML
    public DatePicker weekDatePicker;

    @FXML
    public Label clientName;
    @FXML
    public Label thursdayDate;
    @FXML
    public Label fridayDate;
    @FXML
    public Label saturdayDate;
    @FXML
    public Label sundayDate;
    @FXML
    public Label mondayDate;
    @FXML
    public Label tuesdayDate;
    @FXML
    public Label wednesdayDate;

    @FXML
    public ListView<ClientSchedule> thursdayListView;
    @FXML
    public ListView<ClientSchedule> fridayListView;
    @FXML
    public ListView<ClientSchedule> saturdayListView;
    @FXML
    public ListView<ClientSchedule> sundayListView;
    @FXML
    public ListView<ClientSchedule> mondayListView;
    @FXML
    public ListView<ClientSchedule> tuesdayListView;
    @FXML
    public ListView<ClientSchedule> wednesdayListView;

    @FXML
    public TitledPane selectedScheduleTitlePane;
//    @FXML
//    public Label selectedScheduleDateLabel;
    @FXML
    public TextField selectedScheduleDateTextField;
//    @FXML
//    public Label selectedScheduleTimeLabel;
    @FXML
    public TextField selectedScheduleTimeTextField;
//    @FXML
//    public Label selectedScheduleNoteLabel;
    @FXML
    public TextArea selectedScheduleNoteTextField;

    private ClientSchedule selectedClientSchedule;

//    private SortedList<ClientSchedule> thursdaySortedList;
//    private SortedList<ClientSchedule> fridaySortedList;
//    private SortedList<ClientSchedule> saturdaySortedList;
//    private SortedList<ClientSchedule> sundaySortedList;
//    private SortedList<ClientSchedule> mondaySortedList;
//    private SortedList<ClientSchedule> tuesdaySortedList;
//    private SortedList<ClientSchedule> wednesdaySortedList;

    // reference now
    private LocalDate nowLocalDate;

    // current week on display
    private LocalDate thursdayLocalDate;
    private LocalDate fridayLocalDate;
    private LocalDate saturdayLocalDate;
    private LocalDate sundayLocalDate;
    private LocalDate mondayLocalDate;
    private LocalDate tuesdayLocalDate;
    private LocalDate wednesdayLocalDate;

    // listens for translate change
    private LongProperty weekTranslateProperty;

    public void initialize() {
        // controller is available to loaded once the fxml is loaded - so this.client and this.primaryStage can only be used after initialize() has run
        this.saveButton.setDisable(true);
        this.deleteButton.setDisable(true);
        this.selectedScheduleNoteTextField.setOnKeyTyped(keyEvent -> this.saveButton.setDisable(false));
        this.weekTranslateProperty = new SimpleLongProperty(Long.MAX_VALUE);
        this.weekTranslateProperty.addListener((observableValue, number, t1) -> fillScheduleContent(t1.intValue()));
        this.nowLocalDate = LocalDate.now();

        this.thursdayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.fridayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.saturdayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.sundayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.mondayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tuesdayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.wednesdayListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // display selected item
        this.thursdayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.thursdayListView.getSelectionModel().getSelectedItem()));

        this.fridayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.fridayListView.getSelectionModel().getSelectedItem()));

        this.saturdayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.saturdayListView.getSelectionModel().getSelectedItem()));

        this.sundayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.sundayListView.getSelectionModel().getSelectedItem()));

        this.mondayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.mondayListView.getSelectionModel().getSelectedItem()));

        this.tuesdayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.tuesdayListView.getSelectionModel().getSelectedItem()));

        this.wednesdayListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) ->
                displaySelectedSchedule(MainSceneController.this.wednesdayListView.getSelectionModel().getSelectedItem()));
    }

    public void setPageInformation(Client client, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.client = client;
        this.clientName.setText(this.client.getFullName());

        this.weekTranslateProperty.set(0);

        this.primaryStage.setOnCloseRequest(windowEvent -> saveData());
    }

    private void displaySelectedSchedule(ClientSchedule selectedClientSchedule) {
        if (selectedClientSchedule != null) {
            this.selectedClientSchedule = selectedClientSchedule;
            this.selectedScheduleTitlePane.setText(selectedClientSchedule.getTitle());
            this.selectedScheduleDateTextField.setText(selectedClientSchedule.getLocalDate().toString());
            this.selectedScheduleTimeTextField.setText(selectedClientSchedule.getLocalTime().toString());
            this.selectedScheduleNoteTextField.setText(selectedClientSchedule.getNote());
            this.selectedScheduleNoteTextField.setEditable(true);
            this.deleteButton.setDisable(false);
        }
    }

    private void fillScheduleContent(int weekFromNow) {
        fillDayDateSlots(weekFromNow);
        fillScheduleListWithCurrentWeek(); // might be handled by binding
    }

    private void fillDayDateSlots(int weekFromNow) {
        LocalDate[] weekLocalDates = getDatesOfWeek(weekFromNow);

        this.thursdayLocalDate = weekLocalDates[0];
        this.thursdayDate.setText(this.thursdayLocalDate.toString());

        this.fridayLocalDate = weekLocalDates[1];
        this.fridayDate.setText(this.fridayLocalDate.toString());

        this.saturdayLocalDate = weekLocalDates[2];
        this.saturdayDate.setText(this.saturdayLocalDate.toString());

        this.sundayLocalDate = weekLocalDates[3];
        this.sundayDate.setText(this.sundayLocalDate.toString());

        this.mondayLocalDate = weekLocalDates[4];
        this.mondayDate.setText(this.mondayLocalDate.toString());

        this.tuesdayLocalDate = weekLocalDates[5];
        this.tuesdayDate.setText(this.tuesdayLocalDate.toString());

        this.wednesdayLocalDate = weekLocalDates[6];
        this.wednesdayDate.setText(this.wednesdayLocalDate.toString());


        highlightTodayDate(weekFromNow);
    }

    private LocalDate[] getDatesOfWeek(int weekFromNow) {
        LocalDate nowTranslated = this.nowLocalDate.plusWeeks(weekFromNow);

        int indexOfDay = indexOfDay(nowTranslated.getDayOfWeek().getValue()); // to make thurs starting day

        LocalDate[] weekLocalDates = new LocalDate[7];
        for (int i = 0; indexOfDay - i >= 0; i++) {
            weekLocalDates[indexOfDay - i] = nowTranslated.minusDays(i);
        }
        for (int i = 1; indexOfDay + i < 7; i++) {
            weekLocalDates[indexOfDay + i] = nowTranslated.plusDays(i);
        }

        return weekLocalDates;
    }

    private void highlightTodayDate(int weekFromNow) {
        Color color;
        if (weekFromNow == 0) {
            color = Color.RED;
        } else {
            color = Color.BLACK;
        }
        switch (indexOfDay(LocalDate.now().getDayOfWeek().getValue())) {
            case 0:
                this.thursdayDate.setTextFill(color);
                break;
            case 1:
                this.fridayDate.setTextFill(color);
                break;
            case 2:
                this.saturdayDate.setTextFill(color);
                break;
            case 3:
                this.sundayDate.setTextFill(color);
                break;
            case 4:
                this.mondayDate.setTextFill(color);
                break;
            case 5:
                this.tuesdayDate.setTextFill(color);
                break;
            case 6:
                this.wednesdayDate.setTextFill(color);
                break;
        }
    }

    private int indexOfDay(int dayOfWeek) {
        // 1 monday - 2 tuesday - 3 wednesday - 4 thursday - 5 friday - 6 saturday - 7 sunday
        // 0 thurs - 1 fri - 2 sat - 3 sun - 4 mon - 5 tue - 6 wed
        final int indexTranslate = 4;
        dayOfWeek -= indexTranslate;
        if (dayOfWeek < 0) {
            dayOfWeek += 7;
        }
        return dayOfWeek;
    }

    private void fillScheduleListWithCurrentWeek() {
        this.thursdayListView.setItems(
                this.client.getClientSchedulesByDate(this.thursdayLocalDate).sorted());
        this.fridayListView.setItems(
                this.client.getClientSchedulesByDate(this.fridayLocalDate).sorted());
        this.saturdayListView.setItems(
                this.client.getClientSchedulesByDate(this.saturdayLocalDate).sorted());
        this.sundayListView.setItems(
                this.client.getClientSchedulesByDate(this.sundayLocalDate).sorted());
        this.mondayListView.setItems(
                this.client.getClientSchedulesByDate(this.mondayLocalDate).sorted());
        this.tuesdayListView.setItems(
                this.client.getClientSchedulesByDate(this.tuesdayLocalDate).sorted());
        this.wednesdayListView.setItems(
                this.client.getClientSchedulesByDate(this.wednesdayLocalDate).sorted());
    }

    @FXML
    public void handleWeekChange(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.nextWeekButton) {
            this.weekTranslateProperty.set(this.weekTranslateProperty.add(1).get());
        } else if (actionEvent.getSource() == this.prevWeekButton) {
            this.weekTranslateProperty.set(this.weekTranslateProperty.subtract(1).get());
        } else if (actionEvent.getSource() == this.currentWeekButton){
            this.weekTranslateProperty.set(0);
        } else if (actionEvent.getSource() == this.weekDatePicker) {
            this.weekTranslateProperty.set(weekToTranslateForDate(this.weekDatePicker.getValue()));
        }
    }

    private long weekToTranslateForDate(LocalDate date) {
        long weekToTranslate = 0;
        if (date != null) {
            LocalDate nowWeekStart = this.nowLocalDate.minusDays(indexOfDay(this.nowLocalDate.getDayOfWeek().getValue()));
            long daysToTranslate = nowWeekStart.until(date, ChronoUnit.DAYS);
            if (daysToTranslate >= 0) {
                weekToTranslate = daysToTranslate / 7;
            } else {
                // days -1 to -7 would mean -1 week, -8 to -14 would mean -2 weeks
                weekToTranslate = (daysToTranslate + 1) / 7;
                weekToTranslate--;
            }
        }
        return weekToTranslate;
    }

    @FXML
    public void insertNewSchedule(ActionEvent actionEvent) {
        FXMLLoader createScheduleLoader = new FXMLLoader(getClass().getResource("..//view//CreateSchedule.fxml"));
        Stage createNewScheduleStage = new Stage();
        createNewScheduleStage.initModality(Modality.APPLICATION_MODAL);
        try {
            createNewScheduleStage.setScene(new Scene(createScheduleLoader.load()));
        } catch (IOException ioException) {
            System.err.println("IO EXCEPTION IN insertNewSchedule()");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't create schedule", ButtonType.CLOSE);
            alert.showAndWait();
            return;
        }
        CreateScheduleController createScheduleController = createScheduleLoader.getController();
        createScheduleController.setClientId(this.client.getStudentId());
        createNewScheduleStage.showAndWait();
        ClientSchedule clientSchedule = createScheduleController.getCompletedClientSchedule();
        if (clientSchedule != null) {
            this.client.addSchedule(clientSchedule);
        }
    }

    @FXML
    public void saveEditedSchedule(ActionEvent actionEvent) {
        this.selectedClientSchedule.setNote(this.selectedScheduleNoteTextField.getText());
        this.saveButton.setDisable(true);
        fillScheduleListWithCurrentWeek();
    }

    public void deleteSelectedSchedule(ActionEvent actionEvent) {
        this.client.removeSchedule(this.selectedClientSchedule.getLocalDate(),
                this.selectedClientSchedule.getLocalTime());
        deselectItem();
        this.deleteButton.setDisable(true);
        fillScheduleListWithCurrentWeek();
    }

    private void deselectItem() {
        this.selectedClientSchedule = null;
        this.selectedScheduleTitlePane.setText("");
        this.selectedScheduleDateTextField.setText("");
        this.selectedScheduleTimeTextField.setText("");
        this.selectedScheduleNoteTextField.setText("");
        this.thursdayListView.getSelectionModel().clearSelection();
        this.fridayListView.getSelectionModel().clearSelection();
        this.saturdayListView.getSelectionModel().clearSelection();
        this.sundayListView.getSelectionModel().clearSelection();
        this.mondayListView.getSelectionModel().clearSelection();
        this.tuesdayListView.getSelectionModel().clearSelection();
        this.wednesdayListView.getSelectionModel().clearSelection();
    }

    private void saveData() {
        try {
            DataStore.saveClientSchedules(this.client);
        } catch (SQLException sqlException) {
            System.err.println("[FATAL] Could not save data. Data might be corrupt");
            sqlException.printStackTrace();
        }
    }

    @FXML
    public void handleLogOut(ActionEvent actionEvent) {
        this.primaryStage.hide();
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("..//view//LoginForm.fxml"));
            this.primaryStage.setScene(new Scene(loginLoader.load()));
            ((LoginFormController) loginLoader.getController()).setPrimaryStage(this.primaryStage);
            this.primaryStage.show();
        } catch (IOException ioException) {
            System.err.println("Error while logging out");
            ioException.printStackTrace();
            this.primaryStage.close();
        } finally {
            saveData();
        }
    }
}
