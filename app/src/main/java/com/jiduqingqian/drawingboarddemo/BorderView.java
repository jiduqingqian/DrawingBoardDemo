package com.jiduqingqian.drawingboarddemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * create by chaofun 2016/7/16
 */
public class BorderView extends RelativeLayout {

    private static final float MAX_SCALE = 3.0F;
    private static final float MIN_SCALE = 0.5f;
    private float[] mMatrixValus = new float[9];
    private float mBorderX, mBorderY;
    private float mOldDistance;
    private boolean mIsDrag = false;
    private RelativeLayout mShowView;
    private ImageView mImageView;
    private LineView mLineView;
    private Bitmap mCutoutImage = null;
    private PointF mOldPointer = null;
    private float initImageWidth;
    private float initImageHeight;
    //    private boolean startScale = false;
    private int textColor = Color.BLACK;

    @SuppressLint("ClickableViewAccessibility")
    public BorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderView(Context context) {
        this(context, null, 0);
    }

    public void setCutoutImage(Bitmap bitmap) {
        mCutoutImage = bitmap;
        if (mImageView == null)
            return;
        mImageView.setImageBitmap(mCutoutImage);
    }

    /**
     * 设置画笔大小
     *
     * @param width
     */
    public void setPaintWidth(float width) {
        if (mLineView != null) {
            mLineView.setPaintWidth(width);
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {
        if (mLineView != null) {
            mLineView.setPaintColor(color);
            textColor = color;
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        textColor = color;
        if (paintingWordView != null) {
            paintingWordView.setTextColor(textColor);
        }
    }


    /**
     * 橡皮擦大小
     *
     * @param eraserSize
     */
    public void setEraserSize(float eraserSize) {
        if (mLineView != null) {
            mLineView.setEraserSize(eraserSize);
        }
    }


    /**
     * 设置画笔类型
     *
     * @param type 0、路径 1、橡皮擦 2、文字
     */
    public void setPenType(int type) {
        if (type == 2) {
            renew();
        }
        mLineView.setPenType(type);
    }

    public void undo() {
        mLineView.undo();
    }

    public void redo() {
        mLineView.redo();
    }

    public Bitmap getResultBitmap() {

        RectF clipRect = new RectF();
        clipRect.top = 0;//mImageView.getY();
        clipRect.left = 0;// mImageView.getX();
        clipRect.bottom = mImageView.getHeight();
        clipRect.right = mImageView.getWidth();

        PointF srcSize = new PointF();
        srcSize.x = mCutoutImage.getWidth();
        srcSize.y = mCutoutImage.getHeight();

        Bitmap bitmap = mLineView.getBCResutlImage(clipRect, srcSize);


        Bitmap resultBitmap = Bitmap.createBitmap(mCutoutImage.getWidth(), mCutoutImage.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(mCutoutImage, 0, 0, null);
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
        return resultBitmap;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (mLineView.getPenType() == 2) {
            if (paintingWordView != null && paintingWordView.getVisibility() == VISIBLE) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !inRangeOfView(paintingWordView, event)) {
                    sureEdittext();
                    return true;
                }
                return super.dispatchTouchEvent(event);
            } else {
                return true;
            }
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                return super.dispatchTouchEvent(event);//mLineView.onTouchEvent(event);

            case MotionEvent.ACTION_POINTER_DOWN:
                mIsDrag = true;
                mOldDistance = TouchEventUtil.spacingOfTwoFinger(event);
                mOldPointer = TouchEventUtil.middleOfTwoFinger(event);
                //设置放大和旋转的中心
//                mShowView.setPivotX((event.getX(0) + event.getX(1)) / 2);
//                mShowView.setPivotY((event.getY(0) + event.getY(1)) / 2);
//                startScale = false;
                break;

            case MotionEvent.ACTION_MOVE:

                if (!mIsDrag)
                    return super.dispatchTouchEvent(event);//mLineView.onTouchEvent(event);
                if (event.getPointerCount() != 2) break;
                float newDistance = TouchEventUtil.spacingOfTwoFinger(event);
                if (newDistance > 10) {
                    float scaleFactor = newDistance / mOldDistance;
                    scaleFactor = checkingScale(mShowView.getScaleX(), scaleFactor);
//                if (startScale) {
                    mShowView.setScaleX(mShowView.getScaleX() * scaleFactor);
                    mShowView.setScaleY(mShowView.getScaleY() * scaleFactor);
//                }
                    mOldDistance = newDistance;
                }
                PointF newPointer = TouchEventUtil.middleOfTwoFinger(event);
                mShowView.setX(mShowView.getX() + newPointer.x - mOldPointer.x);
                mShowView.setY(mShowView.getY() + newPointer.y - mOldPointer.y);
                Log.i("yuhui", "ACTION_MOVE x distance" + (newPointer.x - mOldPointer.x));
                mOldPointer = newPointer;
                checkingBorder();
//                startScale = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                if (!mIsDrag)
                    return super.dispatchTouchEvent(event);//mLineView.onTouchEvent(event);
                mShowView.getMatrix().getValues(mMatrixValus);
                mLineView.setScaleAndOffset(mShowView.getScaleX(), mMatrixValus[2], mMatrixValus[5]);
                mIsDrag = false;
                break;
        }
        return true;

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float bitmapWidth, bitmapHeight;
        if (mCutoutImage == null) {
            mCutoutImage = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_4444);
            mCutoutImage.eraseColor(Color.WHITE);//填充颜色
        }
        bitmapWidth = (float) mCutoutImage.getWidth();
        bitmapHeight = (float) mCutoutImage.getHeight();
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
        //RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setImageBitmap(mCutoutImage);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mLineView = new LineView(getContext());
        RelativeLayout.LayoutParams lineViewParams = new RelativeLayout.LayoutParams((int) initImageWidth, (int) initImageHeight);
        //RelativeLayout.LayoutParams lineViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        //      RelativeLayout.LayoutParams.MATCH_PARENT);

        lineViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mShowView.addView(mImageView, imageViewParams);
        mShowView.addView(mLineView, lineViewParams);

        addView(mShowView, showViewParams);
        mBorderX = (getWidth() - initImageWidth) / 2;
        mBorderY = (getHeight() - initImageHeight) / 2;
    }

    private float checkingScale(float scale, float scaleFactor) {
        if ((scale <= MAX_SCALE && scaleFactor > 1.0) || (scale >= MIN_SCALE && scaleFactor < 1.0)) {
            if (scale * scaleFactor < MIN_SCALE) {
                scaleFactor = MIN_SCALE / scale;
            }

            if (scale * scaleFactor > MAX_SCALE) {
                scaleFactor = MAX_SCALE / scale;
            }

        }

        return scaleFactor;
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

    /**
     * 恢复原来大小
     */
    public void renew() {
        mShowView.setScaleX(getWidth() / mShowView.getWidth());
        mShowView.setScaleY(getHeight() / mShowView.getHeight());
        mShowView.setX(getX());
        mShowView.setY(getY());
        mShowView.getMatrix().getValues(mMatrixValus);
        mLineView.setScaleAndOffset(mShowView.getScaleX(), mMatrixValus[2], mMatrixValus[5]);
    }

    public void drawText(TextType textType) {
        mLineView.drawText(textType);
    }

    PaintingWordView paintingWordView;

    public void showEditText() {
        if (paintingWordView == null) {
            paintingWordView = new PaintingWordView(getContext());
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) paintingWordView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            paintingWordView.setLayoutParams(layoutParams);
        } else {
            paintingWordView.setX(mShowView.getWidth() / 2 - paintingWordView.getWidth() / 2);
            paintingWordView.setY(mShowView.getHeight() / 2 - paintingWordView.getHeight() / 2);
        }
        paintingWordView.setTextColor(textColor);
        if (paintingWordView != null) {
            mShowView.removeView(paintingWordView);
        }
        mShowView.addView(paintingWordView);
        paintingWordView.setDellickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowView.removeView(paintingWordView);
            }
        });
        setPenType(2);
        paintingWordView.setditTextFocusable(true);
    }


    public void sureEdittext() {
        if (mLineView.getPenType() == 2 && paintingWordView != null && paintingWordView.getVisibility() == VISIBLE) {
            float[] location = new float[2];
            paintingWordView.getLoacatin(location);
            if (!TextUtils.isEmpty(paintingWordView.getText())) {
                TextType textType = new TextType();
                textType.setText(paintingWordView.getText());
                textType.setX(location[0] + paintingWordView.getX() - mLineView.getX());
                textType.setY(location[1] + paintingWordView.getY() - mLineView.getY());
                textType.setTextSize(paintingWordView.getTextSize());
                textType.setPaintWitdh((int) (paintingWordView.getTextWidth()));
                textType.setPaintColor(textColor);
                drawText(textType);
            }
            paintingWordView.clearFocus();
            mShowView.removeView(paintingWordView);
        }
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        float x = mShowView.getX() + view.getX();
        float y = mShowView.getY() + view.getY();
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }
}
