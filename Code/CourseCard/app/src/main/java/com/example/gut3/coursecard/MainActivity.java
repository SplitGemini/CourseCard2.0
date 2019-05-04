package com.example.gut3.coursecard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.login://监听菜单按钮

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  单击跳转到课程页面
     * @param view
     */
    public void skipCourse(View view) {
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        startActivity(intent);
    }

    /**
     *  单击跳转到个人信息页面
     * @param view
     */
    public void skipAboutMe(View view) {
        Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
        startActivity(intent);
    }

    /**
     *  点击跳转到编辑页面
     * @param view
     */
    public void skipEdit(View view) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        startActivity(intent);
    }

    /**
     *  点击跳转登录页面
     * @param item
     */
    public void skipLogin(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     *  点击跳转注册页面
     * @param item
     */
    public void skipRegister(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
