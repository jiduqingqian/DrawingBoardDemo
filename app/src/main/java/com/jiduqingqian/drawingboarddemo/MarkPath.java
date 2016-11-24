package com.jiduqingqian.drawingboarddemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * 用于记录绘制路径
 *
 * @author chaofun
 */
public class MarkPath {
    private Paint sPaint = null;
    private android.text.TextPaint textPaint = null;
    private PorterDuffXfermode sClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private float TOUCH_TOLERANCE = 4.0f;

    private Path mPath;
    private float mCurrentWidth;
    private int mCurrentColor;
    private PointF mPrevPoint;
    private int mCurrentMarkType = 0;//0:路径 1橡皮擦 2文字
    private TextType textType;

    private MarkPath() {
        mPath = new Path();
    }

    public static MarkPath newMarkPath(PointF point) {
        MarkPath newPath = new MarkPath();
        if (point != null) {
            newPath.mPath.moveTo(point.x, point.y);
            newPath.mPrevPoint = point;
        }
        return newPath;
    }

    /**
     * addMarkPointToPath 将坐标点添加到路径当中
     *
     * @param point， p2当前的点
     */
    public void addMarkPointToPath(PointF point) {
        float dx = Math.abs(point.x - mPrevPoint.x);
        float dy = Math.abs(point.y - mPrevPoint.y);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mPrevPoint.x, mPrevPoint.y, (point.x + mPrevPoint.x) / 2, (point.y + mPrevPoint.y) / 2);
        }
        mPrevPoint = point;
    }

    public void drawMarkPath(Canvas canvas) {
        if (mCurrentMarkType == 2) {
            drawText(canvas);
            return;
        }
        resetPaint();
        canvas.drawPath(mPath, sPaint);
    }

    public void drawBCResultPath(Canvas canvas) {
        if (mCurrentMarkType == 2) {
            drawText(canvas);
            return;
        }
        resetPaint();
        canvas.drawPath(mPath, sPaint);
    }

    public void drawText(Canvas canvas) {
        if (textPaint == null) {
            textPaint = new TextPaint();
        }
        if (textType == null) {
            return;
        }
        textPaint.setColor(textType.getPaintColor());
        textPaint.setTextSize(textType.getTextSize());
        StaticLayout layout = new StaticLayout(textType.getText(), textPaint, textType.getPaintWitdh(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        canvas.translate(textType.getX(), textType.getY());
        layout.draw(canvas);
        canvas.translate(-textType.getX(), -textType.getY());
    }

    public void setCurrentMarkType(int currentMarkType) {
        mCurrentMarkType = currentMarkType;
    }

    public void setPaintWidth(float width) {
        mCurrentWidth = width;
    }

    public void setPaintColor(int color) {
        mCurrentColor = color;
    }

    private void resetPaint() {
        if (sPaint == null) {
            sPaint = new Paint();
            sPaint.setAntiAlias(true);
            sPaint.setDither(true);
            sPaint.setStyle(Paint.Style.STROKE);
            sPaint.setStrokeJoin(Paint.Join.ROUND);
            sPaint.setStrokeCap(Paint.Cap.ROUND);
        }


        switch (mCurrentMarkType) {
            case 0://路径
                setNormalPaint();
                sPaint.setColor(mCurrentColor);
                break;
            case 1://橡皮擦
                sPaint.setAlpha(Color.TRANSPARENT);
                sPaint.setXfermode(sClearMode);
                sPaint.setStrokeWidth(mCurrentWidth);
                break;
            default:
                break;
        }

    }

    private void setNormalPaint() {
        sPaint.setXfermode(null);
        sPaint.setAntiAlias(true);
        sPaint.setDither(true);
        sPaint.setStrokeWidth(mCurrentWidth);
    }

    public void setTextType(TextType textType) {
        this.textType = textType;
    }
}
