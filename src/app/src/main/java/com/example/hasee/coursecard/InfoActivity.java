package com.example.hasee.coursecard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.NoteDao;
import com.example.hasee.coursecard.database.Notes;

import java.util.Random;

import es.dmoral.toasty.Toasty;

public class InfoActivity extends AppCompatActivity {
  private Course course;
  private CardView cv;
  private TextView name;
  private TextView teacher;
  private TextView place;
  private TextView time;
  private TextView week;
  private EditText note;
  private ImageView gumball;

  private NoteDao noteDao;
  private Notes notes;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);
    
    // initialize
    Intent intent = getIntent();
    Bundle bundle = intent.getBundleExtra("Course");
    course = (Course) bundle.getSerializable("Course");

    // bg
    RelativeLayout layout = findViewById(R.id.activity_info_layout);
    layout.getBackground().setAlpha(50);
    
    // views
    cv = findViewById(R.id.activity_info_cv);
    name = findViewById(R.id.activity_info_tv_name);
    teacher = findViewById(R.id.activity_info_tv_teacher);
    place = findViewById(R.id.activity_info_tv_place);
    time = findViewById(R.id.activity_info_tv_time);
    week = findViewById(R.id.activity_info_tv_week);
    note = findViewById(R.id.activity_info_edt_note);
    gumball = findViewById(R.id.iv_gumball);

    // color
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
    
    // info
    name.setText(course.getName());
    teacher.setText(course.getTeacher());
    place.setText(course.getPlace());
    String time_text = course.getWeekday() + " " + getString(time_id);
    time.setText(time_text);
    week.setText(course.getWeek());
    note.bringToFront();
    
    // gumball
    int res_id, msg_id;
    double P;
    switch (course.getTime()) {
      case 1:
        res_id = R.drawable.jianshi;
        msg_id = R.string.gumball_jianshi;
        P = 0.5;  // 公正的剑士总是随机出现
        break;
      case 2:
        res_id = R.drawable.guaidao;
        msg_id = R.string.gumball_guaidao;
        P = 0.1;  // 被你找到那我还算什么怪盗嘛
        break;
      case 3:
        res_id = R.drawable.shangren;
        msg_id = R.string.gumball_shangren;
        P = 0.8;  // 友好的商人
        break;
      case 4:
        res_id = R.drawable.maoxianzhe;
        msg_id = R.string.gumball_maoxianzhe;
        P = 0.7;  // 热情的冒险者
        break;
      case 5:
        res_id = R.drawable.xiaomei;
        msg_id = R.string.gumball_xiaomei;
        P = 0.3;  // 其实小美才是迷路的那个
        break;
      case 6:
        res_id = R.drawable.xiaomei;
        msg_id = R.string.gumball_xiaomei;
        P = 1.0;  // 晚课三连，你总能遇到小美
        break;
      default:
        res_id = R.drawable.jianshi;
        msg_id = R.string.gumball_jianshi;
        P = 0.0;  // 根本没有这节课好吧
        break;
    }
    double p = new Random().nextDouble();
    if (p < P) {
      gumball.setImageResource(res_id);
      gumball.setVisibility(View.VISIBLE);
      String msg = getString(msg_id);
      Toasty.info(this, msg, Toast.LENGTH_SHORT, false).show();
    } else {
      gumball.setVisibility(View.INVISIBLE);
    }


    // get note by course
    Log.d("GET", "onCreate: Notedao get");
    noteDao = CourseDatabase.getInstance(this).getNoteDao();
    notes = noteDao.getNotesByName(course.getName());
    if (notes == null) {
      notes = new Notes(course.getName(), "");
    }
    note.setText(notes.getNotes());
  }

  /**
   * 点击空白区域隐藏键盘.
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent me) {
    if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
      View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
      if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
        hideKeyboard(v.getWindowToken());   //收起键盘
      }
    }
    note.clearFocus();
    return super.dispatchTouchEvent(me);
  }

  /**
   * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
   *
   * @param v
   * @param event
   * @return
   */
  private boolean isShouldHideKeyboard(View v, MotionEvent event) {
    if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
      int[] l = {0, 0};
      v.getLocationInWindow(l);
      int left = l[0],    //得到输入框在屏幕中上下左右的位置
              top = l[1],
              bottom = top + v.getHeight(),
              right = left + v.getWidth();
      if (event.getX() > left && event.getX() < right
              && event.getY() > top && event.getY() < bottom) {
        // 点击位置如果是EditText的区域，忽略它，不收起键盘。
        return false;
      } else {
        return true;
      }
    }
    // 如果焦点不是EditText则忽略
    return false;
  }

  /**
   * 获取InputMethodManager，隐藏软键盘
   * @param token
   */
  private void hideKeyboard(IBinder token) {
    if (token != null) {
      InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
      notes.setNotes(note.getText().toString());
      noteDao.insertNote(notes);
//      Toast.makeText(this, "备注更新完成！", Toast.LENGTH_LONG).show();
      Toasty.success(this, "备注更新完成！", Toast.LENGTH_LONG,true).show();
    }
  }
}
