package com.example.attendance;

public class Timetable {

    private String name, code, lecturer, venue, time;

    public Timetable(String name, String code, String lecturer, String venue, String time) {

        this.name = name;
        this.code = code;
        this.lecturer = lecturer;
        this.time = time;
        this.venue = venue;

    }

    public String getName() {
        return name;
    }

    public String getCode(){
        return code;
    }

    public String getLecturer() {

        return lecturer;
    }

    public String getVenue(){
        return venue;
    }

    public String getTime() {
        return time;
    }

}
