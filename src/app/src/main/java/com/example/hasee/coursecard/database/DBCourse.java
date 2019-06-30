package com.example.hasee.coursecard.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;

@Entity(tableName = "COURSE",
        indices = @Index(value = {"academicYear", "name", "teacher", "place", "weekday", "time", "week"}, unique = true))
public class DBCourse {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String academicYear;
    private String name;
    private String teacher;
    private String place;
    // weekday
    private String weekday;
    private int time;
    private int week;

    public DBCourse(String academicYear, String name, String teacher, String place) {
        this.academicYear = academicYear;
        this.name = name;
        this.teacher = teacher;
        this.place = place;
    }

    @Ignore
    public DBCourse(String weekday) {
        this.weekday = weekday;
        this.academicYear = null;
        this.name = null;
        this.teacher = null;
        this.place = null;
        this.time = 0;
        this.week = 1;
    }

    @Ignore
    public DBCourse(String academicYear, String weekday, String name, String teacher, String place, int time, int week) {
        this.academicYear = academicYear;
        this.weekday = weekday;
        this.name = name;
        this.teacher = teacher;
        this.place = place;
        this.time = time;
        this.week = week;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
