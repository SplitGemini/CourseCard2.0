package com.example.hasee.coursecard.database;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Notes",
        indices = {@Index(value = {"courseName","notes"}, unique = false)},
        foreignKeys = @ForeignKey(entity = DBCourse.class,parentColumns = "id",childColumns = "id" ,onDelete = ForeignKey.CASCADE)
        )
public class Notes {

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String courseName;
    private String notes;

    public Notes(Integer id,String courseName, String notes) {
        this.id = id;
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
