package com.example.hasee.coursecard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Update
    public void updateCourseNote(Notes note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertNote(Notes notes);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertNote(Notes... notes);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertNote(List<Notes> notes);

    @Query("select * from Notes where courseName = :courseName")
    public Notes getNotesByName(String courseName);
}
