package com.example.hasee.coursecard;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.example.hasee.coursecard.database.CourseDao;
import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.DBCourse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CCRecyclerViewAdapter adapter;
    private CardView header;
    private TextView weekday;
    private Spinner spinner;
    private ImageView back;
    private ImageView add;
    private PopupWindow popupWindow;
    private GestureDetector gestureDetector;
    private boolean init;
    private NotificationManager notificationManager;
    private int hour, minute, dayOfWeek, mToPosition;
    private Boolean mShouldScroll = false;
    static public String academicYear = "2019-1";
    private List<Course> Mcourse;
    private Integer debugCount = 0;
    private int weekly;
    private boolean isEmptyCourses;
    private CourseDao courseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toast
        setAcademicYear();
        courseDao = CourseDatabase.getInstance(this).getCourseDao();
        Log.d("debug count", Integer.toString(debugCount++));
        // bg
        RelativeLayout layout = findViewById(R.id.activity_main_layout);
        layout.getBackground().setAlpha(50);
        // views
        recyclerView = findViewById(R.id.activity_main_recyclerView);
        header = findViewById(R.id.header);
        weekday = findViewById(R.id.header_weekday);
        spinner = findViewById(R.id.activity_main_spinner);
        back = findViewById(R.id.activity_main_back);
        add = findViewById(R.id.activity_main_add);
        Log.d("debug count", Integer.toString(debugCount++));
        // back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindowView();
                    popupWindow.showAsDropDown(v, 0, 5);
                }
            }
        });

        //add.setVisibility(View.INVISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查菜单是否为显示状态
                checkPopupWindows();
                spinner.performClick();
            }
        });
        // spinner
        init = true;
        List<String> data = new ArrayList<>();
        for (int i = 1; i <= 20; ++i) {
            data.add("第 " + i + " 周");
        }
        Log.d("debug count", Integer.toString(debugCount++));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, data);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setClickable(false);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (init) {
                    init = false;
                } else {
                    queryInfoFromDB4uiChange(DBWeekPosition(position), academicYear);
                    SharedPreferences userSettings = getSharedPreferences("setting", 0);
                    SharedPreferences.Editor editor = userSettings.edit();
                    //保持当前选择周数
                    editor.putString("weekly", Integer.toString(position + 1));
                    editor.apply();
                    //不是星期一和星期六才滚
                    if (dayOfWeek > 1 && dayOfWeek < 6)
                        smoothMoveToPosition(recyclerView, getItemPosition(dayOfWeek));
                    Log.d("Spinner recycle view", "onItemSelected: " + dayOfWeek);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d("debug count", Integer.toString(debugCount++));
        // header
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                CCRecyclerViewAdapter adapter = (CCRecyclerViewAdapter) parent.getAdapter();
                int position1 = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
                int position2 = ((LinearLayoutManager) parent.getLayoutManager()).findLastVisibleItemPosition();
                if (((LinearLayoutManager) parent.getLayoutManager()).getItemCount() == 0) return;
                // visible
                for (int i = position1; i <= position2; ++i) {
                    parent.findViewHolderForAdapterPosition(i).itemView.setVisibility(View.VISIBLE);
                }
                // move
                View view = parent.findViewHolderForAdapterPosition(position1).itemView;
                if (adapter.getItemCount() > 0 && adapter.getItem(position1 + 1).isHeader()) {
                    header.setTranslationY(view.getTop() - header.getTop());
                } else {
                    header.setTranslationY(0);
                }
                weekday.setText(adapter.getToday(position1 + 1));

                // invisible
                if (adapter.getItem(position1).isHeader()) {
                    view.setVisibility(View.INVISIBLE);
                }

            }
        });
        Log.d("debug count", Integer.toString(debugCount++));
        // default courses
        final List<Course> courses = new ArrayList<>();

        // adapter
        adapter = new CCRecyclerViewAdapter(this, R.layout.item_course, courses) {

            @Override
            public void convert(CCViewHolder viewHolder, Course course, int position) {
                // views
                final CardView cv = viewHolder.getView(R.id.item_course_cardView);
                final TextView tv_weekday = viewHolder.getView(R.id.item_course_weekday);
                final TextView tv_name = viewHolder.getView(R.id.item_course_name);
                final TextView tv_time = viewHolder.getView(R.id.item_course_time);
                final TextView tv_place = viewHolder.getView(R.id.item_course_place);

                // header
                if (course.isHeader()) {
                    tv_weekday.setText(course.getWeekday());
                    tv_weekday.setVisibility(View.VISIBLE);
                    tv_name.setVisibility(View.INVISIBLE);
                    tv_time.setVisibility(View.INVISIBLE);
                    tv_place.setVisibility(View.INVISIBLE);
                    cv.setCardBackgroundColor(getColor(R.color.header_bg));
                    return;
                }
                tv_weekday.setVisibility(View.INVISIBLE);
                tv_name.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.VISIBLE);
                tv_place.setVisibility(View.VISIBLE);

                // info
                tv_name.setText(course.getName());
                tv_place.setText(course.getPlace());

                int color_id, time_id;
                switch (course.getTime()) {
                    case 1:
                        color_id = R.color.item_course_cv_bg1;
                        time_id = R.string.item_course_time1;
                        break;
                    case 2:
                        color_id = R.color.item_course_cv_bg2;
                        time_id = R.string.item_course_time2;
                        break;
                    case 3:
                        color_id = R.color.item_course_cv_bg3;
                        time_id = R.string.item_course_time3;
                        break;
                    case 4:
                        color_id = R.color.item_course_cv_bg4;
                        time_id = R.string.item_course_time4;
                        break;
                    case 5:
                        color_id = R.color.item_course_cv_bg5;
                        time_id = R.string.item_course_time5;
                        break;
                    case 6:
                        color_id = R.color.item_course_cv_bg6;
                        time_id = R.string.item_course_time6;
                        break;
                    default:
                        color_id = R.color.item_course_cv_bg_default;
                        time_id = R.string.item_course_time_default;
                        break;
                }
                cv.setCardBackgroundColor(getColor(color_id));
                tv_time.setText(getString(time_id));

            }
        };
        Log.d("debug count", Integer.toString(debugCount++));
        adapter.setOnItemClickListener(new CCRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Course course = adapter.getItem(position);
                if (!course.isHeader()) {
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Course", course);
                    intent.putExtra("Course", bundle);
                    startActivity(intent);
                }
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

              /*
              //同样起作用，但是感觉浪费了adapter里写的setOnItemClickListener，
              View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
              if (childView != null) {
                  int position = recyclerView.getChildLayoutPosition(childView);
                    Log.d(getApplication().toString(), "single click:" + position);
                  checkPopupWindows();
                  Course course = adapter.getItem(position);
                  if (!course.isHeader()) {
                      Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                      Bundle bundle = new Bundle();
                      bundle.putSerializable("Course", course);
                      intent.putExtra("Course", bundle);
                      startActivity(intent);
                  }
                  return true;
              }
               */
                recyclerView.performClick();
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int position = recyclerView.getChildLayoutPosition(childView);
                    Log.d(getApplication().toString(), "long click:" + position);
                    showAlertDialog(position);
                }
            }
        });
        Log.d("debug count", Integer.toString(debugCount++));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return gestureDetector.onTouchEvent(e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //检查菜单是否为显示状态
                checkPopupWindows();
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    if (dayOfWeek > 1 && dayOfWeek <= 6)
                        smoothMoveToPosition(recyclerView, mToPosition);
                }
            }
        });

        Log.d("debug count", Integer.toString(debugCount++));
        getTime();
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        academicYear = userSettings.getString("academic", "2019-1");
        int weekly = Integer.parseInt(userSettings.getString("weekly", "1"));
        Log.d("now dayOfWeek number", "academic year: " + academicYear + " weekly: " + weekly);

        spinner.setSelection(weekly - 1);
        queryInfoFromDB4uiChange(DBWeekPosition(weekly - 1), academicYear);
        if (isEmptyCourses)
            NotificationInit(getItemPosition(dayOfWeek), getItemPosition(dayOfWeek + 1));

        else {
            Toasty.warning(MainActivity.this, "请先添加课表").show();
        }
        if (dayOfWeek > 1 && dayOfWeek < 6)
            smoothMoveToPosition(recyclerView, getItemPosition(dayOfWeek));
    }

    private int DBWeekPosition(int position) {
        int p;
        if (position >= 0 && position <= 8) p = 1;
        else if (position == 9) p = 10;
        else if (position >= 10 && position <= 18) p = 11;
        else p = 20;
        return p;
    }

    private void checkPopupWindows() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void setAcademicYear() {
        if (this.getIntent().getStringExtra("academic") != null) {
            academicYear = this.getIntent().getStringExtra("academic");
            SharedPreferences userSettings = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = userSettings.edit();
            editor.putString("academic", academicYear);
            editor.apply();
        }
    }

    private void initPopupWindowView() {
        //获取自定义布局文件back_pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.back_pop,null);
        // 创建PopupWindow实例,宽度和高度
        popupWindow = new PopupWindow(customView, 500, 500);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPopupWindows();
            }
        });
        /** 实现自定义视图的功能 */
        Button button2 = customView.findViewById(R.id.button2);
        Button button3 = customView.findViewById(R.id.button3);
        Button button4 = customView.findViewById(R.id.button4);
        button2.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent().setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                   .setClass(MainActivity.this, ListActivity.class);
                                           MainActivity.this.startActivity(intent);
                                           checkPopupWindows();
                                       }
                                   }

        );
        button3.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           checkPopupWindows();
                                           Intent intent = new Intent(MainActivity.this, InfoActivityEdit.class);
                                           Bundle bundle = new Bundle();
                                           Course course = new Course(0, "星期四", "计算机网络", "温武少", "东C203", 2, "1-18周");
                                           bundle.putSerializable("Course_edit", course);
                                           intent.putExtra("Course_edit", bundle);
                                           startActivityForResult(intent, RESULT_OK);
                                       }
                                   }

        );
        button4.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           checkPopupWindows();
                                           Common.cookie = "";
                                           CookieManager cookieManager = CookieManager.getInstance();
                                           cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                                               @Override
                                               public void onReceiveValue(Boolean value) {
                                                   Log.d("main activity", "remove cookie value:" + value);
                                                   Toasty.success(MainActivity.this, "删除cookie成功",Toast.LENGTH_LONG,true).show();
                                               }
                                           });

                                       }
                                   }

        );

    }


    private void NotificationInit(int start, int end) {
        String id = "my_channel";
        String name = "课程提醒";
        String message = "";
        int next_hour = 0, next_minute = 0;
        Log.d("notification ", "dayOfWeek: " + dayOfWeek + " start: " + start + " end: " + end);
        if (start > 0 && end <= 0)
            end = adapter.getItemCount();
        if (start == -1) {
            message = "周末不如去图书馆看看";
        } else {
            for (int i = start + 1; i < end; i++) {
                Course course = adapter.getItem(i);
                switch (course.getTime() * 2 - 1) {
                    case 1:
                        next_hour = 8 - hour;
                        next_minute = 0 - minute;
                        break;
                    case 2:
                        next_hour = 8 - hour;
                        next_minute = 55 - minute;
                        break;
                    case 3:
                        next_hour = 10 - hour;
                        next_minute = 0 - minute;
                        break;
                    case 4:
                        next_hour = 10 - hour;
                        next_minute = 55 - minute;
                        break;
                    case 5:
                        next_hour = 14 - hour;
                        next_minute = 20 - minute;
                        break;
                    case 6:
                        next_hour = 15 - hour;
                        next_minute = 20 - minute;
                        break;
                    case 7:
                        next_hour = 16 - hour;
                        next_minute = 20 - minute;
                        break;
                    case 8:
                        next_hour = 17 - hour;
                        next_minute = 10 - minute;
                        break;
                    case 9:
                        next_hour = 19 - hour;
                        next_minute = 0 - minute;
                        break;
                    case 10:
                        next_hour = 19 - hour;
                        next_minute = 55 - minute;
                        break;
                    default:
                        break;
                }
                Log.i("time", "NotificationInit: " + next_hour + next_minute);
                if (next_hour > 0 || (next_hour == 0 && next_minute > 0)) {
                    if (next_hour > 0 && next_minute > 0) {
                        message = "距" + course.getName() + "还有" + next_hour + "小时" + next_minute + "分钟";
                    } else if (next_hour > 1 && next_minute < 0) {
                        message = "距" + course.getName() + "还有" + (next_hour - 1) + "小时" + (60 + next_minute) + "分钟";
                    } else if (next_hour == 1 && next_minute < 0) {
                        message = "距" + course.getName() + "还有" + (60 + next_minute) + "分钟";
                    } else {
                        message = "距" + course.getName() + "还有" + next_minute + "分钟";
                    }
                    break;
                } else {
                    message = "今天无剩余课程";
                }
            }
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //安卓版本大于等于8.0
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            Log.i("channel", mChannel.toString());
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this, id)
                    .setContentTitle(name)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.icon_round_gray)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle("课程提醒")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(resultPendingIntent)
                    .setOngoing(true).build();
        }
        notificationManager.notify(1, notification);
    }

    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        //返回星期日-星期六分别为1 2 3 4 5 6 7
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }


    public void queryInfoFromDB4uiChange(int queryWeekly, String queryAcademicYear) {
        Log.d("debug count", Integer.toString(debugCount++));
        if (!courseDao.getAcademicYears().contains(queryAcademicYear)) {
            isEmptyCourses = true;
            Mcourse = new ArrayList<>();
            adapter.clear();
            adapter.notifyDataSetChanged();
            header.setVisibility(View.INVISIBLE);
            return;
        }else isEmptyCourses = false;
        Mcourse = new ArrayList<>();
//    String[] weekdayInCh = this.getResources().getStringArray(R.array.weekday_ch_zn);
        String[] weekdayInCh = {"", "星期一", "星期二", "星期三", "星期四", "星期五"};
        for (int i = 1; i <= 5; i++) {
            List<DBCourse> dbCourseList = courseDao.getCourseByWeekday(queryAcademicYear, queryWeekly, weekdayInCh[i]);
            class DbcourseComparetor implements Comparator<DBCourse> {
                @Override
                public int compare(DBCourse d1, DBCourse d2) {
                    return d1.getTime() - d2.getTime();
                }
            }
            Collections.sort(dbCourseList, new DbcourseComparetor());
            if (!dbCourseList.isEmpty()) {
                Mcourse.add(new Course(weekdayInCh[i]));
            }
            for (DBCourse dbCourse : dbCourseList) {
                String str_default = "1-20周";
                Mcourse.add(new Course(dbCourse.getId(), weekdayInCh[i], dbCourse.getName(), dbCourse.getTeacher(),
                        dbCourse.getPlace(), dbCourse.getTime(), str_default));
                Log.i("Course !=", dbCourse.getName());
            }
        }
        Log.d("debug count", Integer.toString(debugCount++));
        adapter.clear();
        adapter.addAll(Mcourse);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() == 0) {
            header.setVisibility(View.INVISIBLE);
        } else {
            header.setVisibility(View.VISIBLE);
        }
        Log.d("debug count", Integer.toString(debugCount++));
    }

    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        if (Mcourse.size() == 0) return;
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            if (dayOfWeek > 1 && dayOfWeek < 6)
                mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            if (dayOfWeek > 1 && dayOfWeek < 6)
                mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            //重置自动滑动到当天的flag
            mShouldScroll = true;
        }
    }

    private int getItemPosition(int week) {
        int position = -1;
        String weekday = "星期一";
        switch (week) {
            case 1:
                weekday = "星期日";
                break;
            case 2:
                weekday = "星期一";
                break;
            case 3:
                weekday = "星期二";
                break;
            case 4:
                weekday = "星期三";
                break;
            case 5:
                weekday = "星期四";
                break;
            case 6:
                weekday = "星期五";
                break;
            case 7:
                weekday = "星期六";
                break;
            default:
                weekday = "星期日";
                break;
        }
        for (int i = 0; i < Mcourse.size(); i++) {
            if (Mcourse.get(i).getWeekday().equals(weekday)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        setAcademicYear();
        int weekly = Integer.parseInt(userSettings.getString("weekly", "1"));
        Log.d("now dayOfWeek number", "academic year: " + academicYear + " weekly: " + weekly);
        spinner.setSelection(weekly - 1);
        Log.d("Resume from list view", Boolean.toString(this.getIntent().getStringExtra("academic") != null));
        //  Toast
        queryInfoFromDB4uiChange(DBWeekPosition(weekly - 1), academicYear);
        if (isEmptyCourses) {
            header.setVisibility(View.INVISIBLE);
            Toasty.warning(MainActivity.this, "请先添加课表").show();
        } else {
            header.setVisibility(View.VISIBLE);
        }
        if (dayOfWeek > 1 && dayOfWeek < 6)
            smoothMoveToPosition(recyclerView, getItemPosition(dayOfWeek));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 弹出对话框选择进入修改课程页面
     */
    private void showAlertDialog(final Integer position) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("修改课程").setIcon(R.drawable.icon)
                .setNegativeButton("取消", null).setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //处理确认按钮的点击事件
                        Course course = adapter.getItem(position);
                        if (!course.isHeader()) {
                            Intent intent = new Intent(MainActivity.this, InfoActivityEdit.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Course_edit", course);
                            intent.putExtra("Course_edit", bundle);
                            startActivity(intent);
                        }
                    }
                }).setMessage("将进入修改该课程界面").create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_OK) {
                    int result = data.getIntExtra("result", 0);
                    if (result == 1) {
                        SharedPreferences userSettings = getSharedPreferences("setting", 0);
                        setAcademicYear();
                        int weekly = Integer.parseInt(userSettings.getString("weekly", "1"));
                        queryInfoFromDB4uiChange(DBWeekPosition(weekly - 1), academicYear);
                    }
                }
                break;
            }
            case (2): {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO: Handle OK click.
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }


}
