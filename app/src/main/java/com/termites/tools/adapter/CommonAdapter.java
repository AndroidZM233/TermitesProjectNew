package com.termites.tools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * 通用适配器
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas = new ArrayList<T>();
    protected Context mContext;
    protected LayoutInflater mInflater;
    private int mLayoutId;

    public CommonAdapter(Context context, int layoutId) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<T> datas) {
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder vh = CommonViewHolder.get(mContext, convertView, parent, position, mLayoutId);
        convert(vh, getItem(position), position);
        return vh.getConvertView();
    }

    public abstract void convert(CommonViewHolder vh, T t, int position);
}
