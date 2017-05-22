package com.mrs.xmpp.im.commlib.recyclerview;

/**
 * Created by mrs on 2017/5/11.
 */


import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mrs.xmpp.im.commlib.R;


/**
 * Created by mrs on 2017/5/11.
 */

public class PullRecycler extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout refreshLayout;
    WrapRecyclerView wrapRecyclerView;
    LinearLayout mEmptyView;
    onLoadMoreListener listener;

    public PullRecycler(@NonNull Context context) {
        super(context);
        setUpView(context);
    }

    public PullRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUpView(context);
    }

    public PullRecycler(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);
    }

    private void setUpView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_widget_pullrecycler, this, true);
        mEmptyView = (LinearLayout) view.findViewById(R.id.recordEmptyView);
        wrapRecyclerView = (WrapRecyclerView) view.findViewById(R.id.recycleList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        refreshLayout.setOnRefreshListener(this);

    }

    public void showEmptyView() {
        refreshLayout.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
    }

    public void showContentView() {
        mEmptyView.setVisibility(GONE);
        refreshLayout.setVisibility(VISIBLE);
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        wrapRecyclerView.setAdapter(adapter);
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        wrapRecyclerView.setLoadMoreEnable(enableLoadMore);
    }

    public void setEnableRefresh(boolean enableRefresh) {
        refreshLayout.setEnabled(enableRefresh);
    }

    public void setOnRefreshCompeleted() {
        if (isRefreshing())
            refreshLayout.setRefreshing(false);
        wrapRecyclerView.setOnLoadCompleted();
    }

    public void setOnRefreshFaield() {
        if (isRefreshing())
            refreshLayout.setRefreshing(false);
        wrapRecyclerView.setOnLoadFailed();
    }

    public void setLayoutManger(RecyclerView.LayoutManager manger) {
        wrapRecyclerView.setLayoutManager(manger);
    }

    public void setHasFixedSize(boolean hasFix) {
        wrapRecyclerView.setHasFixedSize(hasFix);
    }

    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    public void addHeaderView(View view) {
        wrapRecyclerView.addHeaderView(view);
    }

    public void addFooterView(View view) {
        wrapRecyclerView.addFooterView(view);
    }

    public void removeHeaderView(View view) {
        wrapRecyclerView.removeHeaderView(view);
    }

    public void removeFooterView(View view) {
        wrapRecyclerView.removeFooterView(view);
    }

    @Override
    public void onRefresh() {
        if (listener != null)
            listener.onRefresh(true);
    }

    public void setOnLoadMoreListener(onLoadMoreListener listener) {
        this.listener = listener;
        wrapRecyclerView.setOnLoadMoreListener(listener);
    }

    public void setRefresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void setRefreshing(boolean refresh) {
        refreshLayout.setRefreshing(refresh);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration ItemDecoration) {
        wrapRecyclerView.addItemDecoration(ItemDecoration);
    }
    public WrapRecyclerView getRecyclerView() {
        return wrapRecyclerView;
    }

    public WrapRecyclerAdapter getAdapter() {
        return (WrapRecyclerAdapter) wrapRecyclerView.getAdapter();
    }

    public void scrollToPosition(int pos) {
        RecyclerView.Adapter adapter = wrapRecyclerView.getAdapter();
        if (adapter == null)
            return;
        //int count = adapter.getItemCount();
        wrapRecyclerView.scrollToPosition(pos);
    }

    public void keepPosition(int keepPos) {
        RecyclerView.Adapter adapter = wrapRecyclerView.getAdapter();
        if (adapter == null)
            return;
        int count = adapter.getItemCount();
        RecyclerView.LayoutManager manager = wrapRecyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) manager).scrollToPositionWithOffset(count - keepPos, 0);
        } else if (manager instanceof GridLayoutManager) {
            ((GridLayoutManager) manager).scrollToPositionWithOffset(count - keepPos, 0);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) manager).scrollToPositionWithOffset(count - keepPos, 0);
        }
    }

    public void smoothScrollToPosition(int position) {
        RecyclerView.Adapter adapter = wrapRecyclerView.getAdapter();
        if (adapter == null)
            return;
        wrapRecyclerView.smoothScrollToPosition(position);
    }
}
