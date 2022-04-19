package com.studsched.model;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ClientSchedule implements Comparable<ClientSchedule> {
    private SimpleStringProperty studentID;
    private LocalDate localDate;
    private LocalTime localTime;
    private SimpleStringProperty title;
    private SimpleStringProperty note;

    public ClientSchedule(String studentID, LocalDate localDate, LocalTime localTime, String title, String note) {
        this.studentID = new SimpleStringProperty(studentID);
        this.localDate = localDate;
        this.localTime = localTime;
        this.title = new SimpleStringProperty(title);
        this.note = new SimpleStringProperty(note);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = LocalDate.parse(localDate);
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = LocalTime.parse(localTime);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    @Override
    public int compareTo(ClientSchedule o) {
        int cmp = this.localDate.compareTo(o.localDate) * 3 + this.localTime.compareTo(o.localTime) * 2;
        int strCmp = this.title.get().compareTo(o.title.get());
        // localtime compare is never outweighed by string compare
        if (strCmp > 0) {
            cmp++;
        } else if (strCmp < 0) {
            cmp--;
        }
        return cmp;
    }

    @Override
    public String toString() {
        return this.localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + "\n" + this.title.get() + "\n" + this.note.get();
    }
}
