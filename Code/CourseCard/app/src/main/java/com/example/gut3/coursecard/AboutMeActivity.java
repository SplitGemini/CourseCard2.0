package com.example.gut3.coursecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AboutMeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
    }

    /**
     *  单击跳转论坛页面
     * @param view
     */
    public void shipForum(View view) {
        Intent intent = new Intent(AboutMeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     *  单击跳转课程页面
     * @param view
     */
    public void skipCourse(View view) {
        Intent intent = new Intent(AboutMeActivity.this, CourseActivity.class);
        startActivity(intent);
    }
}
