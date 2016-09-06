package com.xf.leaf.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class LeafView extends View {
    private int mWidth;
    private int mHeight;
    private int mDuration = 5000;
    private int mState = 0;//当前状态;
    private float mFraction;


    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private PathMeasure mPathMeasureLeft;
    private PathMeasure mPathMeasureRight;
    private ValueAnimator mAnimator;


    public LeafView(Context context) {
        this(context, null);
    }

    public LeafView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeafView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initPathAndMeasure();
        initAnimator();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
    }

    private void initPathAndMeasure() {
        Path mPathLine = new Path();
        Path mPathLeft = new Path();
        Path mPathRight = new Path();
        mPathLine.lineTo(0, 400);
        mPathMeasure = new PathMeasure(mPathLine, false);
        mPathLeft.quadTo(-200, 200, 0, 400);//画贝赛尔曲线
        mPathMeasureLeft = new PathMeasure(mPathLeft, false);
        mPathRight.quadTo(200, 200, 0, 400);
        mPathMeasureRight = new PathMeasure(mPathRight, false);
    }

    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState < 3) {//更改状态
                    mState++;
                } else {
                    mState = 0;
                }
                mAnimator = mAnimator.clone();//本来想直接复用mAnimator,但是执行到onAnimationEnd,mAnimator貌似没有立即结束,直接start会有问题,
                mAnimator.start();           //问题待研究,若有知道具体原因的望告知

            }
        });
        mAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);// 将画布坐标原点移动到中心位置
        canvas.scale(1, -1);//翻转y轴
        canvas.save();
        drawLine(canvas);
        drawLeaf(canvas);
        canvas.restore();
    }

    /**
     * 画叶子的边界
     *
     * @param canvas
     */
    private void drawLeaf(Canvas canvas) {
        Path dst = new Path();
        Path dstRight = new Path();
        switch (mState) {
            case 0:
                mPathMeasureLeft.getSegment(0, mPathMeasureLeft.getLength() * mFraction, dst, true);//取出要绘制部分的path
                mPathMeasureRight.getSegment(0, mPathMeasureRight.getLength() * mFraction, dstRight, true);
                break;
            case 1:
                mPathMeasureLeft.getSegment(mPathMeasureLeft.getLength() * mFraction, mPathMeasureLeft.getLength(), dst, true);//取出要绘制部分的path
                mPathMeasureRight.getSegment(mPathMeasureRight.getLength() * mFraction, mPathMeasureRight.getLength(), dstRight, true);
                break;
            case 2:
                mPathMeasureLeft.getSegment(mPathMeasureLeft.getLength() * (1 - mFraction), mPathMeasureLeft.getLength(), dst, true);//取出要绘制部分的path
                mPathMeasureRight.getSegment(mPathMeasureRight.getLength() * (1 - mFraction), mPathMeasureRight.getLength(), dstRight, true);
                break;
            case 3:
                mPathMeasureLeft.getSegment(0, mPathMeasureLeft.getLength() * (1 - mFraction), dst, true);//取出要绘制部分的path
                mPathMeasureRight.getSegment(0, mPathMeasureRight.getLength() * (1 - mFraction), dstRight, true);
                break;
            default:
                break;
        }
        canvas.drawPath(dst, mPaint);
        canvas.drawPath(dstRight, mPaint);
    }

    /**
     * 画主干
     */
    private void drawLine(Canvas canvas) {
        Path dst = new Path();
        switch (mState) {
            case 0:
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * mFraction, dst, true);
                break;
            case 1:
                mPathMeasure.getSegment(mPathMeasure.getLength() * mFraction, mPathMeasure.getLength(), dst, true);
                break;
            case 2:
                mPathMeasure.getSegment(mPathMeasure.getLength() * (1 - mFraction), mPathMeasure.getLength(), dst, true);
                break;
            case 3:
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * (1 - mFraction), dst, true);
                break;
            default:
                break;
        }
        canvas.drawPath(dst, mPaint);
    }


}
