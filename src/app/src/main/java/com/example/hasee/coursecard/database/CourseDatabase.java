package com.example.hasee.coursecard.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DBCourse.class, Notes.class}, version = 4, exportSchema = false)
public abstract class CourseDatabase extends RoomDatabase {
    private static final String DB_NAME = "scheduler6.db";
    private static volatile CourseDatabase instance = null;

    static synchronized public CourseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static CourseDatabase create(final Context context) {
        return Room.databaseBuilder(context, CourseDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public abstract CourseDao getCourseDao();
    public abstract NoteDao getNoteDao();
}
