package com.example.hasee.coursecard.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Notes",
        indices = {@Index(value = {"courseName"}, unique = true)})
public class Notes {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String courseName;
    private String notes;

    public Notes(String courseName, String notes) {
        this.courseName = courseName;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
