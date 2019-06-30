package com.example.hasee.coursecard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class CCRecyclerViewAdapter extends RecyclerView.Adapter {
  private Context context;
  private int layoutId;
  protected ArrayList<Course> courses;
  private OnItemClickListener onItemClickListener;
  
  public CCRecyclerViewAdapter(Context context, int layoutId, List<Course> courses) {
    this.context = context;
    this.layoutId = layoutId;
    this.courses = new ArrayList<>();
    this.courses.addAll(courses);
  }

  void clear() {
    courses.clear();
  }

  void addAll(List<Course> courses) {
    this.courses.addAll(courses);
  }

  void add(Course course) {
    this.courses.add(course);
  }

  public Course getItem(int position) {
    return courses == null ? null : courses.get(position);
  }
  
  @Override
  public int getItemCount() {
    return courses == null ? 0 : courses.size();
  }
  
  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return CCViewHolder.get(context, viewGroup, layoutId);
  }
  
  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
    convert((CCViewHolder) viewHolder, courses.get(position), position);
    
    if (onItemClickListener != null) {
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onItemClickListener.onClick(viewHolder.getAdapterPosition());
        }
      });
    }
  }
  
  public abstract void convert(CCViewHolder viewHolder, Course course, int position);
  
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }
  
  public interface OnItemClickListener {
    void onClick(int position);
  }
  
  public String getToday(int position) {
    String today = null;
    for (int i = position-1; i >= 0; --i) {
      if (courses.get(i).isHeader()) {
        today = courses.get(i).getWeekday();
        break;
      }
    }
    return today;
  }
}
