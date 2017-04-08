package com.zgs.parallax.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zgs.parallax.view.adapter.BaseParallaxAdapter;

/**
 * User: zgsHighwin
 * Email: 799174081@qq.com Or 799174081@gmail.com
 * Description:
 * Create-Time: 2017/4/8 14:22
 */
public class ParallaxRecyclerView extends RecyclerView implements ViewTreeObserver.OnGlobalLayoutListener {

    private int mHeight;
    private ParallaxAnimatorUpdateListener mAnimator;
    private ValueAnimator mValueAnimator;
    private float mDownY;
    protected View mHeaderView;
    private int mMoveSpeed = 3;
    protected View mFootView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected int mLastViewOffsetBottom;
    //0下拉，1 上拉
    private int type = 0;
    protected int mFootHeight = 0;
    private boolean isDown;
    private boolean isUp;
    protected BaseParallaxAdapter mRecyclerAdapter;

    public ParallaxRecyclerView(Context context) {
        this(context, null);
    }

    public ParallaxRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOverScrollMode(OVER_SCROLL_NEVER);
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 设置最大移动距离
     *
     * @param height
     */
    public void setViewHeight(int height) {
        this.mHeight = height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (MotionEventCompat.getActionMasked(e)) {
            case MotionEvent.ACTION_DOWN:
                mDownY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                viewMove(e);
                break;
            case MotionEvent.ACTION_UP:
                resetAnimator();
                break;
        }
        return super.onTouchEvent(e);
    }

    private void viewMove(MotionEvent e) {
        int headViewTop = mHeaderView.getHeight();
        int fooViewTop = 0;
        fooViewTop = mFootView.getHeight();
        if (mValueAnimator != null) {
            mValueAnimator.removeAllUpdateListeners();
        }
        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition >= 1) {
            headViewTop = 0;
        }
        float moveY = e.getY();
        float deltaY = moveY - mDownY;
        int newHeight = 0;
        float moveDistance;
        if (headViewTop < mHeight * 2 / 3) {
            moveDistance = (deltaY / mMoveSpeed);
        } else {
            moveDistance = evaluateFloat(headViewTop / mHeight, deltaY / 8, 0);
        }
        if (deltaY > 0) {
            //下拉
            type = 0;
            newHeight = (int) (headViewTop + moveDistance);
        } else {
            //上拉
            type = 1;
            newHeight = (int) (fooViewTop + Math.abs(moveDistance));

        }
        refreshUI(type == 0 ? mHeaderView : mFootView, newHeight);
        mDownY = moveY;
    }

    /**
     * 回弹动画
     */
    private void resetAnimator() {
        if (mValueAnimator != null) {
            mValueAnimator.removeAllUpdateListeners();
        }
        mValueAnimator = ValueAnimator.ofFloat(1f);
        //   mAnimator = new ParallaxAnimatorUpdateListener(type == 0 ? 0 : mFootHeight);
        mAnimator = new ParallaxAnimatorUpdateListener(0, mFootHeight);
        mValueAnimator.addUpdateListener(mAnimator);
        ///mValueAnimator.setInterpolator(new OvershootInterpolator(8));
        mValueAnimator.setDuration(500);
        mValueAnimator.start();
    }

    /**
     * 刷新头部View的UI
     */
    private synchronized void refreshUI(View refreshView, int refreshHeight) {
        Log.d("ParallaxRecyclerView", "refreshHeight:" + refreshHeight);
        refreshView.getLayoutParams().height = refreshHeight;
        refreshView.requestLayout();
    }

    private synchronized void refreshUI(int headerHeight, int footerHeight) {
        mHeaderView.getLayoutParams().height = headerHeight;
        mHeaderView.requestLayout();
        mFootView.getLayoutParams().height = footerHeight;
        mFootView.requestLayout();
    }


    @Override
    public void onGlobalLayout() {
        mHeaderView = getChildAt(0);
        mRecyclerAdapter = (BaseParallaxAdapter) getAdapter();
        mFootView = mRecyclerAdapter.getFooterView();
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            mLinearLayoutManager = ((LinearLayoutManager) layoutManager);
            int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition == (getAdapter().getItemCount() - 1)) {
                View mLastView = mLinearLayoutManager.getChildAt(lastVisibleItemPosition);
                int[] ints = new int[2];
                mLastView.getLocationOnScreen(ints);
                mLastViewOffsetBottom = ints[1];
                mFootHeight = mHeight - mLastViewOffsetBottom;
                mFootView = getChildAt(mRecyclerAdapter.getItemCount() - 1);
                mFootView.getLayoutParams().height = mFootHeight;
                mFootView.requestLayout();
            }

        }
        Log.d("ParallaxRecyclerView", "mLinearLayoutManager.getChildCount():" + mLinearLayoutManager.getChildCount());
        this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 初始化属性动画监听
     */
    private class ParallaxAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private int mHeaderEndValue;
        private Integer mFootEndValue;

        public ParallaxAnimatorUpdateListener(int headerEndValue, Integer footEndValue) {
            mHeaderEndValue = headerEndValue;
            mFootEndValue = footEndValue;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int footViewHeight = 0;
            float animatedFraction = animation.getAnimatedFraction(); //分度值
            int headViewHeight = mHeaderView.getHeight();
            footViewHeight = mFootView.getHeight();
            Integer footValue = evaluateInt(animatedFraction, footViewHeight, mFootEndValue);
            Integer headValue = evaluateInt(animatedFraction, headViewHeight, mHeaderEndValue);
            refreshUI(headValue, footValue);
        }
    }

    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    public float evaluateFloat(float fraction, float startValue, float endValue) {
        float startInt = startValue;
        return (startInt + fraction * (endValue - startInt));
    }
}
