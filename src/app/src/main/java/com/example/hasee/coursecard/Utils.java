package com.example.hasee.coursecard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.hasee.coursecard.database.CourseDao;
import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.DBCourse;
import com.example.hasee.coursecard.database.NoteDao;
import com.example.hasee.coursecard.database.Notes;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Utils {
    public static void insertInitData(Context context) {
        final DBCourse dbCourse1 = new DBCourse("2018-1", "星期一", "计网", "温武少", "C203", 1, 1);
        final DBCourse dbCourse2 = new DBCourse("2018-1", "星期一", "计网", "温武少", "C201", 1, 1);
        final DBCourse dbCourse3 = new DBCourse("2018-1", "星期二", "计网", "温武少", "C205", 1, 1);
        final DBCourse dbCourse4 = new DBCourse("2018-1", "星期三", "Android", "郑贵锋", "D501", 1, 1);
        final DBCourse dbCourse5 = new DBCourse("2018-1", "星期四", "算法设计与分析", "林翰", "C201", 1, 1);
        final DBCourse dbCourse6 = new DBCourse("2018-1", "星期五", "算法设计与分析", "林翰", "C201", 1, 1);
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();

        rx.Observable<Long> observable = rx.Observable.create(new rx.Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                long id1 = courseDao.insertCourse(dbCourse1);
                long id2 = courseDao.insertCourse(dbCourse2);
                long id3 = courseDao.insertCourse(dbCourse3);
                long id4 = courseDao.insertCourse(dbCourse4);
                long id5 = courseDao.insertCourse(dbCourse5);
                long id6 = courseDao.insertCourse(dbCourse6);

                subscriber.onCompleted();
            }
        });

        Subscriber<Long> subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);

        final Notes notes = new Notes("计网", "作业真他么的多");

        final NoteDao noteDao = CourseDatabase.getInstance(context).getNoteDao();

        observable = rx.Observable.create(new rx.Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                noteDao.insertNote(notes);
                subscriber.onCompleted();
            }
        });

        subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
    static public void insert(final Context context, final List<DBCourse> courses) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();

        rx.Observable<Long> observable = rx.Observable.create(new rx.Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                Log.d("Intent","Oncompleted 1");
                courseDao.insertCourses(courses);
                Log.d("Intent","Oncompleted 2");
                subscriber.onCompleted();
            }
        });

        Subscriber<Long> subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.d("Intent","Oncompleted");
                if (Common.flag) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setClass(context, ListActivity.class);
                    Common.flag = false;
                    context.startActivity(intent);
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {

            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
}
