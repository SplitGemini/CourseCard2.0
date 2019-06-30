package com.example.hasee.coursecard;

public class Schedule {
  private String name;
  private String term = "null";

  
  Schedule(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }
}
