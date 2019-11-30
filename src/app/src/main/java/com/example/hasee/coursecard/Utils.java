package com.example.hasee.coursecard;

import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.example.hasee.coursecard.database.CourseDao;
import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.DBCourse;
import com.example.hasee.coursecard.database.NoteDao;
import com.example.hasee.coursecard.database.Notes;

import java.util.List;

// rx1
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;

//rx2
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Utils {

    static public void insert(final Context context, final List<DBCourse> courses) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();
        Observable<DBCourse> observable = Observable.create(new ObservableOnSubscribe<DBCourse>() {
            @Override
            public void subscribe(ObservableEmitter<DBCourse> emitter) throws Exception {
                Log.d("util","emitter insert 1");
                for(int i = 0;i < courses.size();i ++){
                    emitter.onNext(courses.get(i));
                }
                emitter.onComplete();
            }
        });

        Observer<DBCourse> subscriber = new Observer<DBCourse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onComplete() {
                Log.d("util","insert Oncompleted");
                for(int i = 0;i < courses.size();i++){
                    Log.d("course info",courses.get(i).getAcademicYear() + "," +
                            courses.get(i).getName() + "," +
                            courses.get(i).getTeacher() + "," +
                            courses.get(i).getPlace() + "," +
                            courses.get(i).getWeekday() + "," +
                            "第"+courses.get(i).getWeek() + "周," +  "第"+courses.get(i).getTime()+"节课");
                }
                if(courses.size() > 0)
                    if(courses.get(0).getWeek() == 11) {
                        CASActivity.webView.destroy();
                        Log.d("util", "insert jump");
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setClass(context, ListActivity.class);
                        context.startActivity(intent);
                    }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(DBCourse course) {
                Log.d("util next","insert next");
                courseDao.insertCourse(course);
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    static public void deletePage(final Context context, final String academic) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();
        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                Log.d("util","emitter deletePage 1");
                courseDao.deleteCourseByAcademicYear(academic);
                Log.d("util","emitter deletePage 2");
                emitter.onComplete();
            }
        });
        Observer<Long> subscriber = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("util","deletePage onSubscribe");
            }
            @Override
            public void onComplete() {
                Log.d("util","deletePage Oncompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("util","deletePage onNext");
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
    static public void deleteCourse(final Context context, final DBCourse course) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();
        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                Log.d("util","emitter deleteCourse 1");
                courseDao.deleteCourse(course);
                Log.d("util","emitter deleteCourse 2");
                emitter.onComplete();
            }
        });
        Observer<Long> subscriber = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("util","deleteCourse onSubscribe");
            }
            @Override
            public void onComplete() {
                Log.d("util","deleteCourse Oncompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("util","deleteCourse onNext");
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    static public void updateCourse(final Context context, final DBCourse course) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();
        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                Log.d("util","emitter updateCourse 1");
                courseDao.updateCourse(course);
                Log.d("util","emitter updateCourse 2");
                emitter.onComplete();
            }
        });
        Observer<Long> subscriber = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("util","updateCourse onSubscribe");
            }
            @Override
            public void onComplete() {
                Log.d("util","updateCourse Oncompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("util","updateCourse onNext");
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    static public void newCourse(final Context context, final DBCourse course) {
        final CourseDao courseDao = CourseDatabase.getInstance(context).getCourseDao();
        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                Log.d("util","emitter newCourse 1");
                courseDao.insertCourse(course);
                Log.d("util","emitter newCourse 2");
                emitter.onComplete();
            }
        });
        Observer<Long> subscriber = new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("util","newCourse onSubscribe");
            }
            @Override
            public void onComplete() {
                Log.d("util","newCourse Oncompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("util","newCourse onNext");
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
}
