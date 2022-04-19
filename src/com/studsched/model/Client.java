package com.studsched.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final SimpleStringProperty studentId;
    private final SimpleStringProperty fullName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty password;

    private final Map<LocalDate, ObservableList<ClientSchedule>> clientSchedules;

    public Client(String studentId, String fullName, String email, String password) {
        this.studentId = new SimpleStringProperty(studentId);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.clientSchedules = new HashMap<>();
    }

    public String getStudentId() {
        return studentId.get();
    }

    public SimpleStringProperty studentIdProperty() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
    }

    public String getFullName() {
        return fullName.get();
    }

    public SimpleStringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public ObservableList<ClientSchedule> getClientSchedulesByDate(LocalDate localDate) {
        ObservableList<ClientSchedule> clientSchedules = this.clientSchedules.get(localDate);
        if (clientSchedules == null) {
            clientSchedules = FXCollections.observableArrayList();
            this.clientSchedules.put(localDate, clientSchedules);
        }
        return clientSchedules;
    }

    // this changes and returns are the observable list that will be bound so will be reflected in the list view
    public ClientSchedule getClientScheduleByDateTime(LocalDate localDate, LocalTime localTime) {
        ClientSchedule clientSchedule = null;
        ObservableList<ClientSchedule> targetSchedules = this.clientSchedules.get(localDate);
        if (targetSchedules == null) {
            this.clientSchedules.put(localDate, FXCollections.observableArrayList());
        } else {
            int targetIndex = findClientScheduleIndexByTime(targetSchedules, localTime);
            if (targetIndex >= 0) {
                clientSchedule = targetSchedules.get(targetIndex);
            }
        }
        return clientSchedule;
    }

    public void addSchedule(ClientSchedule clientSchedule) {
        if (!this.clientSchedules.containsKey(clientSchedule.getLocalDate())) {
            this.clientSchedules.put(clientSchedule.getLocalDate(), FXCollections.observableArrayList());
        }
        this.clientSchedules.get(clientSchedule.getLocalDate()).add(clientSchedule);
    }

    public ClientSchedule removeSchedule(LocalDate localDate, LocalTime localTime) {
        ClientSchedule removedSchedule = null;
        ObservableList<ClientSchedule> targetScheduleList = this.clientSchedules.get(localDate);
        if (targetScheduleList == null) {
            this.clientSchedules.put(localDate, FXCollections.observableArrayList());
        } else {
            int removeIndex = findClientScheduleIndexByTime(targetScheduleList, localTime);
            if (removeIndex >= 0) {
                removedSchedule = targetScheduleList.remove(removeIndex);
            }
        }
        return removedSchedule;
    }

    private int findClientScheduleIndexByTime(ObservableList<ClientSchedule> targetSchedulesList, LocalTime localTime) {
        int targetIndex = -1;
        for (int i = 0; i < targetSchedulesList.size(); i++) {
            if (targetSchedulesList.get(i).getLocalTime().equals(localTime)) {
                targetIndex = i;
                break;
            }
        }
        return targetIndex;
    }

    public List<ClientSchedule> getAllClientSchedules() {
        List<ClientSchedule> clientSchedules = new ArrayList<>();
        for (LocalDate localDate : this.clientSchedules.keySet()) {
            clientSchedules.addAll(this.clientSchedules.get(localDate));
        }
        return clientSchedules;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.studentId.get());
        stringBuilder.append(" ").append(this.password.get()).append(" ")
                .append(this.email.get()).append(" ").append(this.fullName.get()).append(" ");
        for (LocalDate localDate : this.clientSchedules.keySet()) {
            stringBuilder.append(localDate).append(" : ").append(this.clientSchedules.get(localDate)).append("\n");
        }
        return stringBuilder.toString();
    }
}
