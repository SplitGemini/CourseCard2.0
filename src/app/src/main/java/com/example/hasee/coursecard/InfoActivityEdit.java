package com.example.hasee.coursecard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;

import com.example.hasee.coursecard.database.CourseDao;
import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.DBCourse;
import com.example.hasee.coursecard.database.NoteDao;
import com.example.hasee.coursecard.database.Notes;

import java.util.Random;
import java.util.List;
import es.dmoral.toasty.Toasty;

public class InfoActivityEdit extends AppCompatActivity {
    private Course course;
    private CardView cv;
    private EditText name;
    private EditText teacher;
    private EditText place;
    private EditText time;
    private EditText week;
    private EditText note;
    private ImageView gumball;
    private Button button;
    private NoteDao noteDao;
    private CourseDao courseDao;
    private Notes notes;
    private DBCourse dbCourse;
    private boolean isNew = false;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);

        // initialize
        final Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Course_edit");
        course = (Course) bundle.getSerializable("Course_edit");
        courseDao = CourseDatabase.getInstance(this).getCourseDao();
        dbCourse = new DBCourse(Common.academic,course.getWeekday(),course.getName(),course.getTeacher(),
                course.getPlace(),course.getTime(),1);
        if(course.getId() == 0){
            isNew = true;
        }else {
            dbCourse.setWeek(courseDao.getCourseById(course.getId()).getWeek());
            dbCourse.setId(course.getId());
        }

        // bg
        RelativeLayout layout = findViewById(R.id.activity_info_edit_layout);
        layout.getBackground().setAlpha(50);

        // views
        cv = findViewById(R.id.activity_info_edit_cv);
        name = findViewById(R.id.activity_info_edit_tv_name);
        teacher = findViewById(R.id.activity_info_edit_tv_teacher);
        place = findViewById(R.id.activity_info_edit_tv_place);
        time = findViewById(R.id.activity_info_edit_tv_time);
        week = findViewById(R.id.activity_info_edit_tv_week);
        note = findViewById(R.id.activity_info_edit_edt_note);
        gumball = findViewById(R.id.iv_gumball_edit);
        button = findViewById(R.id.activity_info_edit_btn);

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
        if(isNew) place.setText(getRandomString(8));
        else place.setText(course.getPlace());
        String time_text = course.getWeekday() + " " + getString(time_id);
        //time.setText(time_text);
        week.setText(course.getWeek());
        note.clearFocus();

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
                msg_id = R.string.gumball_sunwukong;
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
        notes = noteDao.getNotesById(dbCourse.getId());
        if (notes == null) {
            notes = new Notes(dbCourse.getId(),course.getName(), "");
        }
        note.setText(notes.getNotes());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
                //onBackPressed();
            }
        });
    }

    /**
     * 弹出对话框选择进入修改课程页面
     *
     */
    private void showAlertDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("删除课程").setIcon(R.drawable.icon)
                .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("delete course","delete course: " + course.getName());
                        Utils.deleteCourse(InfoActivityEdit.this,dbCourse);
                        Intent result = new Intent();
                        result.putExtra("result", 1);
                        setResult(RESULT_OK, result);
                        finish();
                        Toasty.success(InfoActivityEdit.this,"删除成功",Toast.LENGTH_LONG,true).show();
                    }
                }).setMessage("所有该名称的课程都会被删除").create();
        dialog.show();
    }


    private void save(){
        notes.setNotes(note.getText().toString());
        if(isNew){
            Utils.newCourse(this,dbCourse,notes);
        }else{
            dbCourse.setPlace(place.getText().toString());
            dbCourse.setName(name.getText().toString());
            dbCourse.setTeacher(teacher.getText().toString());
            Utils.updateCourse(this,dbCourse,notes);
        }
        Toasty.success(this, "备注更新完成！", Toast.LENGTH_LONG,true).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();//注销该方法，相当于重写父类这个方法
        if(isNew){
            Intent result = new Intent();
            result.putExtra("result", 1);
            setResult(RESULT_OK, result);
        }else{
            Intent result = new Intent();
            result.putExtra("result", 0);
            setResult(RESULT_OK, result);
        }
        save();
        finish();
    }

    private String getRandomString(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.\\[]{}!?@#$%^&*():;,";
        Random random = new Random();
        StringBuffer sb=new StringBuffer();
        for(int i = 0;i < length;i ++){
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
