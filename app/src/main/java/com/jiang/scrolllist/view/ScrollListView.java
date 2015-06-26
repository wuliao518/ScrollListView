package com.jiang.scrolllist.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.jiang.scrolllist.R;


/**
 * Created by Wuliao on 2015/6/2.
 */
public class ScrollListView extends ListView implements AbsListView.OnScrollListener {
    private View currentView;
    private View nextView;
    private ListAdapter mAdapter;
    private boolean isFirst = true;
    private int lastVisibleItem;
    private boolean isCanScroll = true;
    private Scroller mScroller;
    private VelocityTracker velocityTracker;
    /**
     * 展开时与未展开高度比例
     */
    private float scale = 2.0f;
    private int itemHeight;

    public enum Vertical {
        UP, DOWN
    }

    private Vertical vertical = Vertical.UP;

    public ScrollListView(Context context) {
        this(context, null);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(this);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mScroller = new Scroller(getContext());
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollListView);
        scale = a.getFloat(R.styleable.ScrollListView_scale, 2.0f);
        a.recycle();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            this.lastVisibleItem = firstVisibleItem + visibleItemCount;
            currentView = getChildAt(0);
            nextView = getChildAt(1);
            if (totalItemCount == lastVisibleItem && getMeasuredHeight() >= getChildAt(visibleItemCount - 1).getBottom()) {
                isCanScroll = false;
            } else {
                isCanScroll = true;
            }
            if (isFirst) {
                isFirst = false;
                itemHeight = currentView.getLayoutParams().height;
                currentView.getLayoutParams().height = (int) (itemHeight * scale);
                currentView.requestLayout();
            }
        }

    }

    private float lastY;
    private float moveY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = ev.getY();
                int offsetY = (int) (lastY - moveY);
                if (currentView != null && isCanScroll && currentView.getTop() <= 0) {
                    int preHeight = currentView.getLayoutParams().height;
                    int nextHeight = nextView.getLayoutParams().height;
                    if ((preHeight - offsetY) <= itemHeight * scale && (preHeight - offsetY) >= itemHeight &&
                            (nextHeight + offsetY) >= itemHeight && (nextHeight + offsetY) <= itemHeight * scale) {
                        currentView.getLayoutParams().height = preHeight - offsetY;
                        currentView.requestLayout();
                        if (nextView != null) {
                            nextView.getLayoutParams().height = nextHeight + offsetY;
                            nextView.requestLayout();
                        }
                    }
                }
                if (moveY > lastY) {
                    vertical = Vertical.DOWN;
                } else {
                    vertical = Vertical.UP;
                }
                lastY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                if (isCanScroll) {
                    if (vertical.equals(Vertical.DOWN)) {
                        //使用动画来实现
                        ValueAnimator animator = ValueAnimator.ofInt(currentView.getLayoutParams().height, (int) (itemHeight * scale));
                        animator.setInterpolator(new LinearInterpolator());
                        animator.setDuration(200).start();
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                currentView.getLayoutParams().height = (int) animation.getAnimatedValue();
                                currentView.requestLayout();
                            }
                        });
                        ValueAnimator animatorNext = ValueAnimator.ofInt(nextView.getLayoutParams().height,
                                itemHeight);
                        animatorNext.setInterpolator(new LinearInterpolator());
                        animatorNext.setDuration(200).start();
                        animatorNext.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                nextView.getLayoutParams().height = (int) animation.getAnimatedValue();
                                nextView.requestLayout();
                            }

                        });
                        animatorNext.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                int top = currentView.getTop();
                                if (top < 0) {
                                    smoothScrollBy(top, 200);
                                }
                            }
                        });

//                        currentView.getLayoutParams().height = (int) (itemHeight*scale);
//                        currentView.requestLayout();
//                        nextView.getLayoutParams().height = itemHeight;
//                        nextView.requestLayout();
//                        int top = currentView.getTop();
//                    startY=-top;
//                        ImageView nextImageView = (ImageView) nextView.findViewById(R.id.yurisa);
//                        nextImageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
//
//                        ImageView currImageView = (ImageView) currentView.findViewById(R.id.yurisa);
//                        currImageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.XOR);
//                        if (top < 0) {
//                            smoothScrollBy(top, 500);
//                        }
                        //阴影显示

                    } else {
                        ValueAnimator animator = ValueAnimator.ofInt(currentView.getLayoutParams().height, itemHeight);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(200).start();
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                currentView.getLayoutParams().height = (int) animation.getAnimatedValue();
                                currentView.requestLayout();
                            }
                        });
                        ValueAnimator animatorNext = ValueAnimator.ofInt(nextView.getLayoutParams().height,
                                (int) (itemHeight * scale));
                        animatorNext.setInterpolator(new AccelerateDecelerateInterpolator());
                        animatorNext.setDuration(200).start();
                        animatorNext.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                nextView.getLayoutParams().height = (int) animation.getAnimatedValue();
                                nextView.requestLayout();
                            }

                        });
                        animatorNext.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                int top = currentView.getTop();
                                if (top < 0) {
                                    smoothScrollBy(itemHeight + top, 200);
                                }
                            }
                        });

//                    startY=-top;
                        //阴影显示
//                        ImageView nextImageView = (ImageView) nextView.findViewById(R.id.yurisa);
//                        nextImageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.XOR);
//                        ImageView currImageView = (ImageView) currentView.findViewById(R.id.yurisa);
//                        currImageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        //滚动

                    }
                }

//                mScroller.startScroll(0,startY,0, preHeight, 400);


//                mScroller.startScroll(0,startY,0, preHeight, 400);
//                currentView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(300);
//                            currentView.getLayoutParams().height = dp2px(getContext(),240);
//                            currentView.requestLayout();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

                    return true;
                }
        return super.onTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(adapter);
    }

    //    int startY;
    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
//        if (mScroller.computeScrollOffset()) {
//            //这里调用View的scrollTo()完成实际的滚动
//            if(currentView.getTop()!=0){
//                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//                int offsetY = mScroller.getCurrY()-startY;
//                int preHeight=currentView.getLayoutParams().height;
//                int nextHeight=nextView.getLayoutParams().height;
//                currentView.getLayoutParams().height = preHeight-offsetY;
//                currentView.requestLayout();
//                if(nextView!=null){
//                    nextView.getLayoutParams().height = nextHeight+offsetY;
//                    nextView.requestLayout();
//                }
//                startY=mScroller.getCurrY();
//            }
//            //必须调用该方法，否则不一定能看到滚动效果
//            postInvalidate();
//        }
        super.computeScroll();
    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }


    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     *
     * @return
     */
    private int getScrollVelocity() {
        //一秒内移动了多少像素
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }


}
