package com.techidea.commonlibrary.widget.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.techidea.commonlibrary.R;

/**
 * Created by zchao on 2016/5/23.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private static final String TAG = ArcMenu.class.getSimpleName();

    private Position mPosition = Position.LEFT_BOTTOM;

    private int mRadius = 100;

    private View mButton;

    private Status mCurrentStatus = Status.CLOSE;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public enum Status {
        OPEN, CLOSE
    }

    public enum Position {
        LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM;
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mRadius, getResources().getDisplayMetrics());
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ArcMenu_position) {
                int val = a.getInt(attr, 0);
                switch (val) {
                    case 0:
                        mPosition = Position.LEFT_TOP;
                        break;
                    case 1:
                        mPosition = Position.RIGHT_TOP;
                        break;
                    case 2:
                        mPosition = Position.RIGHT_BOTTOM;
                        break;
                    case 3:
                        mPosition = Position.LEFT_BOTTOM;
                        break;
                }
            } else if (attr == R.styleable.ArcMenu_radius) {
                mRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 100f, getResources().getDisplayMetrics()
                ));
            }
        }
        a.recycle();
    }

    //计算子元素大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED,
                    MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    //确定子元素位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(View.GONE);

                int cleft = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ctop = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                //右上 右下
                if (mPosition == Position.LEFT_BOTTOM
                        || mPosition == Position.RIGHT_BOTTOM) {
                    ctop = getMeasuredHeight() - cHeight - ctop;
                }
                if (mPosition == Position.RIGHT_TOP ||
                        mPosition == Position.RIGHT_BOTTOM)
                    cleft = getMeasuredWidth() - cWidth - cleft;
//                left, top, right, bottom
                child.layout(cleft, ctop, cleft + cWidth, ctop + cHeight);
            }
        }
    }

    private void layoutButton() {
        View cButton = getChildAt(0);
        cButton.setOnClickListener(this);
        int l = 0;
        int t = 0;
        int width = cButton.getMeasuredWidth();
        int height = cButton.getMeasuredHeight();
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }
        cButton.layout(l, t, l + width, t + height);
    }

    @Override
    public void onClick(View v) {
        if (mButton == null) {
            mButton = getChildAt(0);
        }
        rotateView(mButton,0f,270f,300);
        toggleMenu(300);
    }

    public static void rotateView(View view, float fromDegress,
                                  float toDegress, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegress, toDegress,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public void toggleMenu(int durationMillis) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);

            int xfalg = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.LEFT_BOTTOM) {
                xfalg = -1;
            }
            if (mPosition == Position.LEFT_TOP ||
                    mPosition == Position.RIGHT_TOP)
                yflag = -1;
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            AnimationSet animset = new AnimationSet(true);
            Animation animation = null;
            if (mCurrentStatus == Status.CLOSE) {
                animset.setInterpolator(new OvershootInterpolator(2F));
                animation = new TranslateAnimation(xfalg * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {
                animation = new TranslateAnimation(0f, xfalg * cl, 0f, yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE)
                        childView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animation.setFillAfter(true);
            animation.setDuration(durationMillis);
            //动画设置延迟时间
            animation.setStartOffset((i * 100) / (count - 1));
            RotateAnimation rotate = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(durationMillis);
            rotate.setFillAfter(true);
            animset.addAnimation(rotate);
            animset.addAnimation(animation);
            childView.startAnimation(animset);
            final int index = i + 1;
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null)
                        mOnMenuItemClickListener.onClick(childView, index - 1);
                    menuItemAnin(index - 1);
                    changeStatus();
                }
            });
        }
        changeStatus();
    }

    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    //开始菜单动画
    private void menuItemAnin(int item) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            if (i == item) {
                childView.startAnimation(scaleBigAnim(300));
            } else {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }


    //缩小消失
    private Animation scaleSmallAnim(int durationMillis) {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }

    private Animation scaleBigAnim(int durationMillis) {
        AnimationSet animationset = new AnimationSet(true);
        Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }

    public Position getPosition() {
        return mPosition;
    }

    public void setPosition(Position position) {
        mPosition = position;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public Status getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(Status currentStatus) {
        mCurrentStatus = currentStatus;
    }

    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return mOnMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }
}
