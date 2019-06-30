package com.example.hasee.coursecard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public abstract class SortedAdapter extends RecyclerView.Adapter {
  private Context context;
  private int layoutId;
  private SortedList<Schedule> schedules;
  private OnItemClickListener onItemClickListener;
  
  public SortedAdapter(Context context, int layoutId, List<Schedule> schedules) {
    this.context = context;
    this.layoutId = layoutId;
    this.schedules = new SortedList<Schedule>(Schedule.class, new SortedList.Callback<Schedule>() {
      @Override
      public int compare(Schedule schedule, Schedule t21) {
        return schedule.getName().compareTo(t21.getName());
      }
  
      @Override
      public void onChanged(int i, int i1) {
    
      }


      @Override
      public boolean areContentsTheSame(Schedule schedule, Schedule t21) {
        return false;
      }
  
      @Override
      public boolean areItemsTheSame(Schedule schedule, Schedule t21) {
        return false;
      }
  
      @Override
      public void onInserted(int i, int i1) {
    
      }
  
      @Override
      public void onRemoved(int i, int i1) {
    
      }
  
      @Override
      public void onMoved(int i, int i1) {
    
      }
    });
    this.schedules.addAll(schedules);
  }


  public void add(Schedule schedule) {
    for (int i = 0 ; i < schedules.size() ; i++) {
        if (schedules.get(i).getTerm().equals(schedule.getTerm()))
          return;
    }
    this.schedules.add(schedule);
  }
  public void addAll(List<Schedule> schedules1) {
    this.schedules.addAll(schedules1);
  }
  public void clear() {
    this.schedules.clear();
  }

  public Schedule getItem(int position) {
    return schedules == null ? null : schedules.get(position);
  }
  
  @Override
  public int getItemCount() {
    return schedules == null ? 0 : schedules.size();
  }
  
  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return CCViewHolder.get(context, viewGroup, layoutId);
  }
  
  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
    convert((CCViewHolder) viewHolder, schedules.get(position));
  
    if (onItemClickListener != null) {
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onItemClickListener.onClick(viewHolder.getAdapterPosition());
        }
      });
    }
  }
  
  public abstract void convert(CCViewHolder viewHolder, Schedule schedule);
  
  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }
  
  public interface OnItemClickListener {
    void onClick(int position);
  }
}
