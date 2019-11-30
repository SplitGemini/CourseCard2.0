package com.example.hasee.coursecard;

import java.io.Serializable;

public class Course implements Serializable {
  private boolean header;
  private Integer id;
  // weekday
  private String weekday;
  
  // course
  private String name;
  private String teacher;
  private String place;
  private int time;
  private String week;
  public Course(String weekday) {
    this.id = 0;
    this.header = true;
    this.weekday = weekday;
    this.name = null;
    this.teacher = null;
    this.place = null;
    this.time = 0;
    this.week = null;
  }

  public Course(Integer id,String weekday, String name, String teacher, String place, int time, String week) {
    this.id = id;
    this.header = false;
    this.weekday = weekday;
    this.name = name;
    this.teacher = teacher;
    this.place = place;
    this.time = time;
    this.week = week;
  }
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
