package com.jiduqingqian.drawingboarddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by qianhao on 2016/11/18.
 * 画板控件
 */

public class DrawingBoardView extends RelativeLayout {
    private float MAX_SCALE = 3.0F;
    private float MIN_SCALE = 0.5f;
    private float[] mMatrixValus = new float[9];
    private float mBorderX, mBorderY;
    private ImageView mImageView;
    private PaintView paintView;
    private RelativeLayout mShowView;
    private Bitmap mCutoutImage;
    private float mOldDistance;
    private PointF mOldPointer = null;
    private float initImageWidth = 0, initImageHeight = 0;
    private boolean isDoubleFinger;

    public DrawingBoardView(Context context) {
        super(context);
        initView();
    }

    public DrawingBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DrawingBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float bitmapWidth, bitmapHeight;
        if (mCutoutImage == null) {
            mCutoutImage = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_4444);
            mCutoutImage.eraseColor(Color.YELLOW);//填充颜色
        }
        bitmapWidth = mCutoutImage.getWidth();
        bitmapHeight = mCutoutImage.getHeight();
        initImageWidth = 0;
        initImageHeight = 0;
        if (bitmapWidth > bitmapHeight) {
            initImageWidth = getWidth();
            initImageHeight = (bitmapHeight / bitmapWidth) * initImageWidth;
        } else {
            initImageHeight = getHeight();
            initImageWidth = (bitmapWidth / bitmapHeight) * initImageHeight;
        }

        mShowView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams showViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        mImageView = new ImageView(getContext());
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams((int) initImageWidth, (int) initImageHeight);
        mImageView.setImageBitmap(mCutoutImage);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams paintViewParams = new RelativeLayout.LayoutParams((int) initImageWidth, (int) initImageHeight);
        paintViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        paintView = new PaintView(getContext());
        mShowView.addView(mImageView, imageViewParams);
        mShowView.addView(paintView, paintViewParams);
        paintView.setBackgroundColor(Color.BLACK);
        addView(mShowView, showViewParams);
        mBorderX = (getWidth() - initImageWidth) / 2;
        mBorderY = (getHeight() - initImageHeight) / 2;
    }

    public void setBackground(Bitmap bitmap) {
        mCutoutImage = bitmap;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                return super.dispatchTouchEvent(ev);
            case MotionEvent.ACTION_POINTER_DOWN:
                isDoubleFinger = true;
                mOldDistance = TouchEventUtil.spacingOfTwoFinger(ev);
                mOldPointer = TouchEventUtil.middleOfTwoFinger(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isDoubleFinger) return super.dispatchTouchEvent(ev);
                //处理两根手指
                if (ev.getPointerCount() != 2) break;
                float newDistance = TouchEventUtil.spacingOfTwoFinger(ev);
                float scaleFactor = newDistance / mOldDistance;
                mShowView.setScaleX(mShowView.getScaleX() * scaleFactor);
                mShowView.setScaleY(mShowView.getScaleY() * scaleFactor);
                mOldDistance = newDistance;

                PointF newPointer = TouchEventUtil.middleOfTwoFinger(ev);
                mShowView.setX(mShowView.getX() + newPointer.x - mOldPointer.x);
                mShowView.setY(mShowView.getY() + newPointer.y - mOldPointer.y);
                mOldPointer = newPointer;
                checkingBorder();
                break;
            case MotionEvent.ACTION_UP:
                if (!isDoubleFinger) return super.dispatchTouchEvent(ev);
                isDoubleFinger = false;
                break;
        }

        return true;
    }

    private void checkingBorder() {
        PointF offset = offsetBorder();
        mShowView.setX(mShowView.getX() + offset.x);
        mShowView.setY(mShowView.getY() + offset.y);
        if (mShowView.getScaleX() == 1) {
            mShowView.setX(0);
            mShowView.setY(0);
        }
    }

    private PointF offsetBorder() {
        PointF offset = new PointF(0, 0);
        if (mShowView.getScaleX() > 1) {
            mShowView.getMatrix().getValues(mMatrixValus);
            if (mMatrixValus[2] > -(mBorderX * (mShowView.getScaleX() - 1))) {
                offset.x = -(mMatrixValus[2] + mBorderX * (mShowView.getScaleX() - 1));
            }

            if (mMatrixValus[2] + mShowView.getWidth() * mShowView.getScaleX() - mBorderX * (mShowView.getScaleX() - 1) < getWidth()) {
                offset.x = getWidth() - (mMatrixValus[2] + mShowView.getWidth() * mShowView.getScaleX() - mBorderX * (mShowView.getScaleX() - 1));
            }

            if (mMatrixValus[5] > -(mBorderY * (mShowView.getScaleY() - 1))) {
                System.out.println("offsetY:" + mMatrixValus[5] + " borderY:" + mBorderY + " scale:" + getScaleY() + " scaleOffset:" + mBorderY * (getScaleY() - 1));
                offset.y = -(mMatrixValus[5] + mBorderY * (mShowView.getScaleY() - 1));
            }

            if (mMatrixValus[5] + mShowView.getHeight() * mShowView.getScaleY() - mBorderY * (mShowView.getScaleY() - 1) < getHeight()) {
                offset.y = getHeight() - (mMatrixValus[5] + mShowView.getHeight() * mShowView.getScaleY() - mBorderY * (mShowView.getScaleY() - 1));
            }
        }

        return offset;
    }

}
