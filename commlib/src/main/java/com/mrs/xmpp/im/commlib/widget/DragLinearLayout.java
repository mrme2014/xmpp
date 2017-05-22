package com.mrs.xmpp.im.commlib.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * xml使用
 * <p>
 * <DragLinearLayout
 * <p>
 * <LinearLayout
 * id="@+id/contentLayout">
 *  .......
 * </LinearLayout>
 * <p>
 * <LinearLayoutid="@+id/menuLayout"
 *   ......
 * </LinearLayout>
 * <p>
 * DragLinearLayout/>
 *
 *
 * adpter中维护 列表的打开 关闭
 */

/**
 * Created by mrs
 */
public class DragLinearLayout extends LinearLayout {
    private ViewGroup mContentLayout;
    private ViewGroup mMenuLayout;

    private ViewDragHelper mViewDragHelper;
    private GestureDetectorCompat mGestureDetectorCompat;

    private int mMenuLayoutWidth;
    private int mContentLayoutWidth;

    private ViewDragListener mViewDragListener;

    private boolean isOpen = false;

    public DragLinearLayout(Context context) {
        super(context);
        init();
    }

    public DragLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragLinearLayout(Context context, AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    @Override
    protected void onFinishInflate() {
        mContentLayout = (ViewGroup) getChildAt(0);
        mMenuLayout = (ViewGroup) getChildAt(1);
        mMenuLayout.setClickable(true);
        mContentLayout.setClickable(true);
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mContentLayoutWidth = mContentLayout.getMeasuredWidth();
        mMenuLayoutWidth = mMenuLayout.getMeasuredWidth();

    }

    /**
     * 设置监听
     */
    public void setOnViewDragListener(ViewDragListener viewDragListener) {
        mViewDragListener = viewDragListener;
    }


    public interface ViewDragListener {
        void onOpen();

        void onClose();

        void onDrag(float percent);

    }


    /**
     * 滑动时松手后以一定速率继续自动滑动下去并逐渐停止，
     * 类似于扔东西或者松手后自动滑动到指定位置
     */
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        //创建ViewDragHelper的实例，第一个参数是ViewGroup，传自己，
        // 第二个参数就是滑动灵敏度的意思,可以随意设置，第三个是回调
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        //手势操作，第二参数什么意思看下面
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), new XScrollDetector());

    }

    private class XScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) <= Math.abs(distanceX);
        }
    }

    class DragHelperCallback extends ViewDragHelper.Callback {

        /**
         * 根据返回结果决定当前child是否可以拖拽
         *
         * @param child     当前被拖拽的view
         * @param pointerId 区分多点触摸的id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;//mContentLayout == child;
        }

        /**
         * 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定了动画执行速度
         *
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mContentLayoutWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mContentLayout) {
                if (left >= 0) return 0;
                if (left < -mMenuLayoutWidth)
                    return -mMenuLayoutWidth;
            } else if (child == mMenuLayout) {
                if (left >= mContentLayoutWidth)
                    return mContentLayoutWidth;
                else if (left < mContentLayoutWidth - mMenuLayoutWidth)
                    return mContentLayoutWidth - mMenuLayoutWidth;
            }
            return left;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float percent = Math.abs((float) left / (float) mContentLayoutWidth);
            if (null != mViewDragListener)
                mViewDragListener.onDrag(percent);
            if (changedView == mContentLayout) {
                mMenuLayout.offsetLeftAndRight(dx);
            } else {
                mContentLayout.offsetLeftAndRight(dx);
            }
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == mContentLayout) {
                if (xvel <= 0) {//向左滑动
                    if (-mMenuLayoutWidth >= mMenuLayoutWidth / 2
                            && -mMenuLayoutWidth <= mMenuLayoutWidth) {
                        open();
                    } else {
                        close();
                    }
                } else {//向右滑动
                    close();
                }
            } else close();
        }
    }

    /**
     * 打开
     */
    public void open() {
        if (mViewDragHelper.smoothSlideViewTo(mContentLayout, -mMenuLayoutWidth, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        if (null != mViewDragListener)
            mViewDragListener.onOpen();
        isOpen = true;
    }

    /**
     * 关闭
     */
    public void close() {
        if (mViewDragHelper.smoothSlideViewTo(mContentLayout, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        if (null != mViewDragListener)
            mViewDragListener.onClose();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP)
            close();
        return mViewDragHelper.shouldInterceptTouchEvent(ev) && mGestureDetectorCompat.onTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        try {
            mViewDragHelper.processTouchEvent(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
