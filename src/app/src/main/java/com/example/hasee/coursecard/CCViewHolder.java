package com.example.hasee.coursecard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CCViewHolder extends RecyclerView.ViewHolder {
  private SparseArray<View> views;
  private View view;
  
  public CCViewHolder(Context context, View view, ViewGroup viewGroup) {
    super(view);
    this.view = view;
    views = new SparseArray<>();
  }
  
  public static CCViewHolder get(Context context, ViewGroup viewGroup, int layoutId) {
    View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
    CCViewHolder ccViewHolder = new CCViewHolder(context, view, viewGroup);
    return ccViewHolder;
  }
  
  public <T extends View> T getView(int viewId) {
    View view = views.get(viewId);
    if (view == null) {
      view = this.view.findViewById(viewId);
      views.put(viewId, view);
    }
    return (T) view;
  }
}
