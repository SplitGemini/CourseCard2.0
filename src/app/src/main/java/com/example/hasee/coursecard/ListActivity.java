package com.example.hasee.coursecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.hasee.coursecard.database.CourseDatabase;
import com.example.hasee.coursecard.database.DBCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


public class ListActivity extends AppCompatActivity {
  private RecyclerView recyclerView;
  private SortedAdapter adapter;
  private Button btn;
  private OptionsPickerView pvOptions;
  private GestureDetector gestureDetector;
  private String name;
  private String academicYear = "2019-1";
  private Schedule temp;
  
  @Override
  @SuppressWarnings("unchecked")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
  
    // bg
    RelativeLayout layout = findViewById(R.id.activity_list_layout);
    layout.getBackground().setAlpha(50);
    
    // views
    recyclerView = findViewById(R.id.activity_list_recyclerView);
    btn = findViewById(R.id.activity_list_btn);

    // Toasty
      Toasty.Config.getInstance()
              .setInfoColor(getColor(R.color.toasty_err)).apply();


    // default schedules
    final List<Schedule> schedules = new ArrayList<>();
    Schedule tempx;
      List<DBCourse> all1 = CourseDatabase.getInstance(this).getCourseDao().getAll();
      Log.i("All year size = ", "" + all1.size());

      List<String> all = CourseDatabase.getInstance(ListActivity.this).getCourseDao().getAcademicYears();
    for (String str : all) {
        String[] str1 = str.split("-");
        if (str1[1].equals("1")) {
            tempx = new Schedule(str1[0] + "第一学期");
            tempx.setTerm(str);
        } else if (str1[1].equals("2")) {
            tempx = new Schedule(str1[0] + "第二学期");
            tempx.setTerm(str);
        } else {
            tempx = new Schedule(str1[0] + "第三学期");
            tempx.setTerm(str);
        }
        Log.i("Academic year -=-=- ", tempx.getTerm());
        schedules.add(tempx);
    }

    // adapter
    adapter = new SortedAdapter(this, R.layout.item_schedule, schedules) {
      @Override
      public void convert(CCViewHolder viewHolder, Schedule schedule) {
        final CardView cv = viewHolder.getView(R.id.item_schedule_cv);
        final TextView name = viewHolder.getView(R.id.item_schedule_name);
        
        cv.setCardBackgroundColor(getColor(R.color.item_schedule_cv));
        name.setText(schedule.getName());
      }
    };

      gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
          @Override
          public boolean onSingleTapUp(MotionEvent e){
              View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
              if (childView != null) {
                  int position = recyclerView.getChildLayoutPosition(childView);
                  Log.d(getApplication().toString(), "single click:" + position);
                  List<String> all = CourseDatabase.getInstance(ListActivity.this).getCourseDao().getAcademicYears();
                  Intent intent = new Intent();
                  intent.putExtra("academic", adapter.getItem(position).getTerm());
                  Common.academic = adapter.getItem(position).getTerm();
                  Log.d("all:size ", all.size() + "");
                  Log.d("academic: ",adapter.getItem(position).getTerm());
                  for (String str : all) {
                      if (adapter.getItem(position).getTerm().equals(str)) {
                          intent.setClass(ListActivity.this,MainActivity.class);
                          ListActivity.this.startActivity(intent);
                          return true;
                      }
                  }
                  intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                  intent.setClass(ListActivity.this,CASActivity.class);
                  ListActivity.this.startActivity(intent);
                  return true;
              }
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

      recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
          @Override
          public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
              if (gestureDetector.onTouchEvent(e)) {
                  return true;
              }
              return false;
          }
          @Override
          public void onTouchEvent(RecyclerView rv, MotionEvent e) {

          }
          @Override
          public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
          }
      });

    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    recyclerView.setAdapter(adapter);
    
    // default options
    final ArrayList<String> options1Items = new ArrayList<>();
    for (int i = 2016; i < 2030; ++i) {
      options1Items.add("" + i);
    }
    
    final ArrayList<String> options2Items = new ArrayList<>();
    options2Items.add("第一学期");
    options2Items.add("第二学期");
    options2Items.add("第三学期");
  
    // pvOptions
    pvOptions = new OptionsPickerBuilder(ListActivity.this, new OnOptionsSelectListener() {
      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View v) {
        String item1 = options1Items.get(options1);
        String item2 = options2Items.get(options2);

        if (item2.equals("第一学期")) {
            temp = new Schedule(item1 + " " + item2);
            temp.setTerm(item1 + "-" + "1");
            academicYear = item1 + "-" + "1";
        } else if (item2.equals("第二学期")) {
            temp = new Schedule(item1 + " " + item2);
            temp.setTerm(item1 + "-" + "2");
            academicYear = item1 + "-" + "2";
        } else {
            temp = new Schedule(item1 + " " + item2);
            temp.setTerm(item1 + "-" + "3");
            academicYear = item1 + "-" + "3";
        }
        /*
        if (academicYear.compareTo("2017-2") < 0){
            Toasty.warning(ListActivity.this,"无法查询"+item1+item2+"课表").show();
            return;
        }

         */
//          Log.d("onOptionsSelect", "onOptionsSelect: " + academicYear);
//        String[] weeklys = {"1,18"};
//          Log.d("HTTPS",temp.getTerm());
//        for (String i: weeklys)
//            Onclick4Data(i,temp.getTerm());
//        Toast.makeText(ListActivity.this, academicYear, Toast.LENGTH_SHORT).show();
          List<String> all = CourseDatabase.getInstance(ListActivity.this).getCourseDao().getAcademicYears();
          for (String str : all) {
              if (temp.getTerm().equals(str)) {
                  Toasty.warning(ListActivity.this, temp.getTerm()).show();
                  return;
              }
          }
          adapter.add(temp);
          adapter.notifyDataSetChanged();
      }
    })
            .setContentTextSize(20)
            .setSelectOptions(3, 0) //设置19年第一学期为默认
            .build();
    pvOptions.setNPicker(options1Items, options2Items, null);
    
    // btn
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pvOptions.show();
      }
    });
      Log.i("onCreate", ": notify data set changed()");
    adapter.notifyDataSetChanged();
  }

  private void showAlertDialog(final Integer position){
      AlertDialog dialog = new AlertDialog.Builder(this).setTitle("删除课程库").setIcon(R.drawable.icon)
              .setNegativeButton("取消", null).setPositiveButton("确定", new OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      //处理确认按钮的点击事件
                      String deleteAcademic = adapter.getItem(position).getTerm();
                      Log.d("delete academic: ",deleteAcademic);
                      Toasty.success(ListActivity.this,"删除成功").show();
                      //CourseDatabase.getInstance(ListActivity.this).getCourseDao().deleteCourseByAcademicYears(deleteAcademic);
                      //adapter.notifyDataSetChanged();
                  }
              }).setMessage("将删除该学期所有课程，确认删除？").create();
      dialog.show();
  }
}
