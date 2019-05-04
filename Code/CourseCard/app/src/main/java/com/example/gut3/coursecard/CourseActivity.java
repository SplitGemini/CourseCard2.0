package com.example.gut3.coursecard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
    }

    /**
     *  单击跳转论坛页面
     * @param view
     */
    public void skipForum(View view) {
        Intent intent = new Intent(CourseActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     *  单击跳转个人信息页面
     * @param view
     */
    public void skipAboutMe(View view) {
        Intent intent = new Intent(CourseActivity.this, AboutMeActivity.class);
        startActivity(intent);
    }
}
