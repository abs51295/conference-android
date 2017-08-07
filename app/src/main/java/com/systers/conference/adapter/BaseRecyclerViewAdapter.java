package com.systers.conference.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements Filterable {
    private List<T> dataList;

    protected BaseRecyclerViewAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    protected T getItem(int position) {
        return dataList.get(position);
    }

    private void addItem(int position, T data) {
        if (position < dataList.size()) {
            dataList.add(position, data);
            notifyItemInserted(position);
        } else {
            dataList.add(data);
            notifyItemInserted(dataList.size());
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    private void moveItem(int fromPosition, int toPosition) {
        final T data = dataList.remove(fromPosition);
        dataList.add(toPosition, data);
        notifyItemMoved(fromPosition, toPosition);
    }

    protected void animateTo(List<T> newData) {
        applyAndAnimateRemovals(newData);
        applyAndAnimateAdditions(newData);
        applyAndAnimateMovedItems(newData);
    }

    protected void removeItem(T item) {
        int position = dataList.indexOf(item);
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    protected List<T> getDataList() {
        return dataList;
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void reset(List<T> newData) {
        dataList.clear();
        dataList = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(List<T> newSessions) {
        List<T> dataList = new ArrayList<>(this.dataList);
        for (int i = dataList.size() - 1; i >= 0; i--) {
            final T data = dataList.get(i);
            if (!newSessions.contains(data)) {
                removeItem(this.dataList.indexOf(data));
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newSessions) {
        List<T> dataList = new ArrayList<>(this.dataList);
        int count = newSessions.size();
        for (int i = 0; i < count; i++) {
            final T data = newSessions.get(i);
            if (!dataList.contains(data)) {
                addItem(i, data);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newDataList) {
        List<T> dataList = new ArrayList<>(this.dataList);
        for (int toPosition = newDataList.size() - 1; toPosition >= 0; toPosition--) {
            final T data = newDataList.get(toPosition);
            final int fromPosition = dataList.indexOf(data);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    /**
     * Removes an item from position
     */
    private T removeItem(int position) {
        final T speaker = dataList.remove(position);
        notifyItemRemoved(position);
        return speaker;
    }
}
