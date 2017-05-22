package com.mrs.xmpp.im.commlib.multisupport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 */

public abstract class MultiTypeSupportAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    public Context mContext;
    public ArrayList<T> mDatas;
    //支持多布局的回调
    private MultiTypeSupport mTypeSupport;
    //如果不支持多布局,那么mLayoutRes就是 item布局
    private int mLayoutRes;

    public MultiTypeSupportAdapter(Context context, ArrayList<T> list, int layoutRes, MultiTypeSupport typeSupport) {
        this.mContext = context;
        this.mDatas = list;
        this.mTypeSupport = typeSupport;
        this.mLayoutRes = layoutRes;
    }

    @Override
    public int getItemViewType(int position) {
        //如果是应用了多布局，那么通过getItemViewType 把item的layout布局id传进来
        if (mTypeSupport != null)
            return mTypeSupport.getTypeLayoutRes(mDatas.get(position), position);
        return super.getItemViewType(position);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 多布局支持
        if (mTypeSupport != null) {
            mLayoutRes = viewType;
        }
        if (mLayoutRes == 0)
            throw new RuntimeException("filed mTypeSupport-&-mLayoutRes must be one is not null");
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutRes, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        onBindNormalHolder(holder, mDatas.get(position), position);
    }

    public abstract void onBindNormalHolder(ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }
}
