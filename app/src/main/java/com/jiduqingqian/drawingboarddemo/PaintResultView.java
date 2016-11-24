package com.jiduqingqian.drawingboarddemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by qianhao on 16/10/24.
 */

public class PaintResultView extends View {
    private Paint paint;
    private ArrayList<Path> paths = new ArrayList<>();

    public PaintResultView(Context context) {
        super(context);
        initView();
    }

    public PaintResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PaintResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//| Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void setPaintSize(float size) {
        paint.setStrokeWidth(size);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paths.size() <= 1) {
            int width = getWidth();
            int height = getHeight();
            float lineY, previousY = height / 2.0f;
            for (int i = 0; i < width; ) {
                Path path = new Path();
                path.moveTo(i, previousY);
                lineY = (float) ((height / 6) * (Math.sin(2 * (i + 2) * Math.PI / width) + 3));
                // 设置操作点为线段x/y的一半
                path.quadTo(i + 1, (previousY + lineY) / 2.0f, i + 2, lineY);
                paths.add(path);
                i += 2;
                previousY = lineY;
            }
        }
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
    }
}
