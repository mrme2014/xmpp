package com.mrs.xmpp.im.commlib.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import com.mrs.xmpp.im.commlib.widget.DragLinearLayout;

import com.mrs.xmpp.im.commlib.R;



/**
 * Created by mrs on 2017/4/7.
 */

public class WrapRecyclerView extends RecyclerView {
    // 包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private RecyclerView.Adapter mAdapter;
    private boolean loadMoreEnable = true;//默认可以加载更多
    private boolean isLoadingMore = false;//是否正在加载更多中，避免多次出发上拉加载更多
    private View loadMoreView;
    private onLoadMoreListener listener;

    private boolean enableSwipDimiss;//是否支持配合DragLinearLayout实现侧滑删除

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && enableSwipDimiss)
            closeMenuIfNeeded();

        return super.onInterceptTouchEvent(e);
    }

    private void closeMenuIfNeeded() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof DragLinearLayout) {
                DragLinearLayout dragMenu = (DragLinearLayout) childAt;
                if (dragMenu.isOpen()) {
                    dragMenu.close();
                    break;
                }
            }
        }
    }

    public void setSupportSwipDiMiss(boolean enableSwipDimiss) {
        this.enableSwipDimiss = enableSwipDimiss;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE) {
            if (isLoadingMore || !isLoadMoreEnable() || listener == null)
                return;
            LayoutManager manager = getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                int lastPos = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                loadMoreIfNeeded(lastPos);
            } else if (manager instanceof GridLayoutManager) {
                int lastPos = ((GridLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                loadMoreIfNeeded(lastPos);
            } else if (manager instanceof StaggeredGridLayoutManager) {
                int[] position = null;
                ((StaggeredGridLayoutManager) manager).findLastCompletelyVisibleItemPositions(position);
                loadMoreIfNeeded(position[0]);
            }
        }
    }

    private void loadMoreIfNeeded(int lastPos) {
        int itemCount = mWrapRecyclerAdapter.getItemCount();
        //如果不是滑动到列表最底部
        if (lastPos != itemCount - 1)
            return;
        listener.onRefresh(false);
    }


    @Override
    public void setAdapter(Adapter adapter) {
        // 为了防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }

        this.mAdapter = adapter;

        if (adapter instanceof WrapRecyclerAdapter) {
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        } else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(getLoadMoreView(), adapter);
        }

        super.setAdapter(mWrapRecyclerAdapter);

        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);
        // 解决GridLayout添加头部和底部也要占据一行
        mWrapRecyclerAdapter.adjustSpanSize(this);
    }

    public void addHeaderView(View view) {
        // 先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.addHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.addFooterView(view);
        }
    }

    public void removeHeaderView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.removeHeaderView(view);
        }
    }

    public void removeFooterView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.removeFooterView(view);
        }
    }

    public SparseArray<View> getHeaders() {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.getHeaderViews();
        }
        return null;
    }

    public SparseArray<View> getFooters() {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.getFooterViews();
        }
        return null;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        mWrapRecyclerAdapter.setShowLoadMore(loadMoreEnable);
    }

    public boolean isLoadMoreEnable() {
        return this.loadMoreEnable;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        mWrapRecyclerAdapter.setLoadMoreView(loadMoreView);
    }

    public View getLoadMoreView() {
        if (loadMoreView == null) {
            loadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.base_widget_load_more, this, false);
        }
        return loadMoreView;
    }

    @Deprecated
    public void setOnLoadFailed() {
        if (mAdapter == null)
            return;
        isLoadingMore = false;
        if (loadMoreEnable) {
            mWrapRecyclerAdapter.notifyItemRemoved(mWrapRecyclerAdapter.getItemCount() - 1);
        } else {
            mWrapRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void setOnLoadCompleted() {
        if (mAdapter == null)
            return;
        if (isLoadingMore) {
            isLoadingMore = false;
            if (loadMoreEnable) {
                mWrapRecyclerAdapter.notifyItemRemoved(mWrapRecyclerAdapter.getItemCount() - 1);
            } else {
                mWrapRecyclerAdapter.notifyDataSetChanged();
            }
        } else
            mWrapRecyclerAdapter.notifyDataSetChanged();
    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemInserted(positionStart);
        }
    };

    public void setOnLoadMoreListener(onLoadMoreListener listener) {
        this.listener = listener;
    }
}