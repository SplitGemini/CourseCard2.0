package com.example.hasee.coursecard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CourseDao {
    @Update
    public void updateCourseNote(DBCourse course);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Long insertCourse(DBCourse course);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public List<Long> insertCourses(List<DBCourse> courses);

    @Query("select * from COURSE")
    public List<DBCourse> getAll();

    @Query("select * from COURSE where name = :name")
    public List<DBCourse> getCoursesByName(String name);

    @Query("select * from COURSE where place = :place")
    public List<DBCourse> getCoursesByPlace(String place);

    @Query("select * from COURSE where teacher = :teacher")
    public List<DBCourse> getCoursesByTeacher(String teacher);

    @Query("select * from COURSE where id = :id")
    public DBCourse getCourseById(Integer id);

    @Query("select * from COURSE where academicYear = :academicYear and week = :week")
    public List<DBCourse> getCourseByWeek(String academicYear, int week);

    @Query("select * from COURSE where academicYear = :academicYear and week = :week and weekday = :weekday")
    public List<DBCourse> getCourseByWeekday(String academicYear, int week, String weekday);

    @Query("select * from COURSE where academicYear = :academicYear")
    public List<DBCourse> getCourseByAcademicYear(String academicYear);

    @Query("select * from COURSE")
    public List<DBCourse> getAllCourses();

    @Query("select distinct academicYear from COURSE")
    public List<String> getAcademicYears();
}
