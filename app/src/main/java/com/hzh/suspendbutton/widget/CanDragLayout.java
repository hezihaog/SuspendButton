package com.hzh.suspendbutton.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hzh.suspendbutton.R;

public class CanDragLayout extends RelativeLayout {
    /**
     * helper类
     */
    private ViewDragHelper draggerHelper;

    /**
     * 可滑动的内容view
     */
    private View contentView;

    /**
     * 原始位置
     */
    private Point autoSlidePoint = new Point();
    private long downTime;
    private float downX;
    private float downY;
    private int touchSlop;
    private OnDragLayoutClickListener listener;

    public CanDragLayout(Context context) {
        super(context);
        init();
    }

    public CanDragLayout(Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        draggerHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                // 只可以滑这个view
                if (child == contentView) {
                    return true;
                }
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                // 左右滑动的距离
                int leftSide = getPaddingLeft();
                int bottomSide = getWidth() - getPaddingRight() - contentView.getWidth();
                return Math.min(Math.max(left, leftSide), bottomSide);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                // 上下可滑动的距离
                int topSide = getPaddingTop();
                int bottomSide = getHeight() - getPaddingBottom() - contentView.getHeight();
                return Math.min(Math.max(top, topSide), bottomSide);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //contentView手指释放时可以自动回去
                if (releasedChild == contentView) {
                    if (releasedChild.getLeft() + releasedChild.getWidth() / 2 > getWidth() / 2) {
                        autoSlidePoint.x = getWidth() - getPaddingRight() - releasedChild.getWidth();
                    } else {
                        autoSlidePoint.x = getPaddingLeft();
                    }
                    autoSlidePoint.y = releasedChild.getTop();
                    draggerHelper.settleCapturedViewAt(autoSlidePoint.x, autoSlidePoint.y);
                    postInvalidate();
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = findViewById(R.id.can_drag_content);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean isTouchView = inTouchInView(contentView, event);
                if (isTouchView) {
                    return draggerHelper.shouldInterceptTouchEvent(event);
                } else {
                    return super.onInterceptTouchEvent(event);
                }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                long upTime = System.currentTimeMillis();
                float upX = event.getX();
                float upY = event.getY();
                long touchDuration = upTime - downTime;
                float touched = getDistanceBetween2Points(new PointF(downX, downY), new PointF(upX, upY));
                //模拟点击事件
                if (touchDuration < 400 && touched <= touchSlop) {
                    if (this.listener != null) {
                        listener.onClick();
                    }
                }
                break;
        }
        draggerHelper.processTouchEvent(event);
        boolean isTouchView = inTouchInView(contentView, event);
        if (isTouchView) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() {
        if (draggerHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public interface OnDragLayoutClickListener {
        /**
         * 点击回调
         */
        void onClick();
    }

    public void setOnDragLayoutClickListener(OnDragLayoutClickListener listener) {
        this.listener = listener;
    }

    /**
     * 判断是否点击到悬浮View上
     *
     * @param view
     * @param ev
     * @return
     */
    public static boolean inTouchInView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x) {
            return false;
        }
        if (ev.getRawX() > (x + view.getWidth())) {
            return false;
        }
        if (ev.getRawY() < y) {
            return false;
        }
        if (ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    /**
     * 获得两点之间的距离
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        return (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
    }
}