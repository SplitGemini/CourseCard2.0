package com.example.hasee.coursecard;

import java.io.Serializable;

public class Course implements Serializable {
  private boolean header;
  
  // weekday
  private String weekday;
  
  // course
  private String name;
  private String teacher;
  private String place;
  private int time;
  private String week;
  
  public Course(String weekday) {
    this.header = true;
    this.weekday = weekday;
    this.name = null;
    this.teacher = null;
    this.place = null;
    this.time = 0;
    this.week = null;
  }

  public Course(String weekday, String name, String teacher, String place, int time, String week) {
    this.header = false;
    this.weekday = weekday;
    this.name = name;
    this.teacher = teacher;
    this.place = place;
    this.time = time;
    this.week = week;
  }

  public String getWeekday() {
    return weekday;
  }
  
  public String getName() {
    return name;
  }
  
  public String getTeacher() {
    return teacher;
  }
  
  public String getPlace() {
    return place;
  }
  
  public int getTime() {
    return time;
  }
  
  public String getWeek() {
    return week;
  }
  
  public boolean isHeader() {
    return header;
  }
}
