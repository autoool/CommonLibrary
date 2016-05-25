package com.techidea.commonlibrary.widget.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by zchao on 2016/5/23.
 */
public class RayMenu extends ViewGroup {

    private static final String TAG = RayMenu.class.getSimpleName();

    private Context mContext;

    //布局的宽
    private int mMeasuredWidth;
    //布局的高
    private int mMeasuredHeight;
    //    实际布局的宽高
    private int mActualWidth;
    private int mPaddingLeft;
    private int mPaddingRight;

    //    menu 水平间隔
    private int mHorizontalSpace;
    //    垂直间隔
    private int mVerticalSpace;

    private View mToggleBtn;

    private boolean mIsOpen;

    private int mLine;

    private int mCWidth;

    private int mCHeight;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    private View mClickView;

    private int mPos;

    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public RayMenu(Context context) {
        super(context);
    }

    public RayMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RayMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

    //计算子元素大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();

        mActualWidth = mMeasuredWidth - mPaddingLeft - mPaddingRight;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    //确定子元素位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            View firstChildView = getChildAt(0);

            mCWidth = firstChildView.getMeasuredWidth();
            mCHeight = firstChildView.getMeasuredHeight();

            mHorizontalSpace = (mActualWidth - 3 * mCWidth) / 2;
            mVerticalSpace = dip2px(mContext, 10);

            layoutFirstChildView(firstChildView);

            int childCount = getChildCount();
            mLine = (int) Math.ceil(childCount / 3.0f);

            for (int i = 1; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.setVisibility(GONE);
                childView.setClickable(false);
                childView.setFocusable(false);
                //1,2,3 : 4 5 6
                int lineTag = (i - 1) / 3;
                int horizontalOffset = (i - 1 - lineTag * 3) * (mHorizontalSpace + mCWidth);

                int left = horizontalOffset + mPaddingLeft;
                int right = left + mCWidth;
                //比按钮高
                int top = mMeasuredHeight - (2 - lineTag) * mCHeight - mVerticalSpace;
                int bottom = mMeasuredHeight - (1 - lineTag) * mCHeight - mVerticalSpace;

                childView.layout(left, top, right, bottom);
            }
        }
    }

    //第一个按钮居中
    private void layoutFirstChildView(View firstChildView) {
        int cWidth = firstChildView.getMeasuredWidth();
        int cHeight = firstChildView.getMeasuredHeight();

        int left = mPaddingLeft + 1 * mHorizontalSpace + 1 * cWidth;
        int top = mMeasuredHeight - cHeight - mVerticalSpace;
        int right = left + cWidth;
        int bottom = mMeasuredHeight - mVerticalSpace;

        firstChildView.layout(left, top, right, bottom);

        firstChildView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mToggleBtn = getChildAt(0);
                toggleMenu(300);
            }
        });
    }

    public void toggleMenu(int durationMillis) {
        int childCount = getChildCount();
        if (!mIsOpen) {
            for (int i = 1; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.setVisibility(View.VISIBLE);
                childView.setClickable(true);
                childView.setFocusable(true);

                int lineTag = (i - 1) / 3;

                int verticalOffset = (2 - lineTag) * mVerticalSpace;
                int top = mMeasuredHeight - (2 - lineTag) * mCHeight - verticalOffset;

                createBindMenuAnim(childView, childCount, i, top, durationMillis);

                childView.setTag(i);

                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindMenuItemAnim(v, (Integer) v.getTag());
                    }
                });
            }
        } else {
            for (int i = 1; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.setVisibility(GONE);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
        }
        changeStatus();
    }


    private void createBindMenuAnim(final View childView, int childCount, int i,
                                    int top, int durationMillis) {
        AnimationSet animset = new AnimationSet(true);
        Animation animation = null;
        if (!mIsOpen) {
            animset.setInterpolator(new OvershootInterpolator(1.5f));
            animation = new TranslateAnimation(0, 0, top, 0);
            childView.setClickable(true);
            childView.setFocusable(true);
        } else {
            animation = new TranslateAnimation(0, 0, 0, top);
            childView.setClickable(false);
            childView.setFocusable(false);
        }

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (!mIsOpen)
                    childView.setVisibility(View.GONE);
            }
        });

        animation.setFillAfter(true);
        animation.setDuration(durationMillis);
        animation.setStartOffset((i * 100) / (childCount - 1));
        animset.addAnimation(animation);
        childView.startAnimation(animset);
    }

    private void bindMenuItemAnim(View clickView, int pos) {
        mClickView = clickView;
        mPos = pos;
        Animation aniation = null;
        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            final View childView = getChildAt(i);
            if (pos == i) {
                aniation = createChildViewAnim(true, 300);
            } else {
                aniation = createChildViewAnim(false, 300);
            }
            childView.startAnimation(aniation);
            childView.setClickable(false);
            childView.setFocusable(false);
        }
        mIsOpen = false;
        Animation anim = new ScaleAnimation(0f, 1.0f, 0, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mOnMenuItemClickListener != null)
                    mOnMenuItemClickListener.onClick(mClickView, mPos);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private Animation createChildViewAnim(boolean isClick, int durationMills) {
        Animation anim = isClick ? new AlphaAnimation(1, 0) :
                new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(durationMills);
        anim.setFillAfter(true);
        return anim;
    }

    private void changeStatus() {
        mIsOpen = !mIsOpen ? true : false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (mIsOpen && action == MotionEvent.ACTION_DOWN) {
            toggleMenu(300);
        }
        return super.onTouchEvent(event);
    }


    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return mOnMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
