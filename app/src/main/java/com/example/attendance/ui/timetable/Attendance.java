package com.example.attendance.ui.timetable;

public class Attendance {

    private String id;
    private String code, lecturer, date;

    public Attendance(String code, String id, String lecturer, String date) {

        this.id = id;
        this.code = code;
        this.lecturer = lecturer;
        this.date = date;

    }

    public String getId() {
        return id;
    }

    public String getCode(){
        return code;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getDate() {
        return date;
    }
}
