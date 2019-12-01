package com.example.hasee.coursecard;

public class Data {

    private String tuesday = "null" , thursday= "null"  , monday= "null"  , wednesday= "null"  , friday= "null"  ;
    private int section;
    private int weekly;
    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }
    public String getTuesday() {
        return tuesday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }
    public String getThursday() {
        return thursday;
    }

    public void setSection(int section) {
        this.section = section;
    }
    public int getSection() {
        return section;
    }

    public void setWeekly(int weekly) {
        this.weekly = weekly;
    }
    public int getWeekly() {
        return weekly;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }
}