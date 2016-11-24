package com.jiduqingqian.drawingboarddemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by qianhao on 16/10/20.
 */

public class PaintingWordView extends RelativeLayout implements View.OnTouchListener {
    private EditText editText;
    private int inputType;
    private Context context;
    int width = 0, height = 0;
    private final float MAX_SCALE = 3.0F;
    private final float MIN_SCALE = 0.5f;
    private float INITIALSIZE;

    public PaintingWordView(Context context) {
        super(context);
        initView(context);
    }


    public PaintingWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintingWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        this.context = context;
        View.inflate(context, R.layout.view_painting_word_box, this);
        findViewById(R.id.painting_word_stretching).setOnTouchListener(this);
        findViewById(R.id.editText).setOnTouchListener(this);
        editText = (EditText) findViewById(R.id.editText);
        INITIALSIZE = size = editText.getTextSize() / 2;
        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        inputType = editText.getInputType();
    }

    public void setDellickListener(OnClickListener onClickListener) {
        findViewById(R.id.painting_word_del).setOnClickListener(onClickListener);
    }

    int startX, startY;
    float beforX, beforY;
    float beforDistance, lastDistance, textSize, size;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.painting_word_stretching) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    textSize = size;
                    // 获取手指按下的坐标
                    beforX = event.getRawX();
                    beforY = event.getRawY();
                    beforDistance = (float) Math.sqrt(beforX * beforX + beforY * beforY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取手指移动到了哪个点的坐标
                    float movingX = event.getRawX();
                    float movingY = event.getRawY();
                    lastDistance = (float) Math.sqrt(movingX * movingX + movingY * movingY);
                    float scale = lastDistance / beforDistance;
                    if (textSize * scale > MIN_SCALE * INITIALSIZE && textSize * scale < MAX_SCALE * INITIALSIZE) {
                        size = textSize * scale;
                        editText.setTextSize(size);
                    }
                    // 相对于上一个点，手指在X和Y方向分别移动的距离
                    break;
            }
        } else {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    width = getWidth();
                    height = getHeight();
                    // editText.setInputType(inputType);
                    // 获取手指按下的坐标
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取手指移动到了哪个点的坐标
                    //  editText.setInputType(InputType.TYPE_NULL);
                    // 相对于上一个点，手指在X和Y方向分别移动的距离
                    setX(getX() + (int) event.getRawX() - startX);
                    setY(getY() + (int) event.getRawY() - startY);
                    // 本次移动的结尾作为下一次移动的开始
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
            }
        }
        invalidate();
        return false;
    }

    public float getTextSize() {
        return editText.getTextSize();
    }

    public String getText() {
        if (editText.getText() != null) {
            return editText.getText().toString();
        } else {
            return null;
        }
    }

    public void getLoacatin(float[] locaton) {
//        editText.getLocationOnScreen(locaton);
//        locaton[0] += ViewUtils.Dp2Px(10);
//        locaton[1] += ViewUtils.Dp2Px(10);
        locaton[0] = editText.getX() + ViewUtils.Dp2Px(context, 10);
        locaton[1] = editText.getY() + ViewUtils.Dp2Px(context, 10);

    }

    public float getTextWidth() {
        return editText.getWidth() - ViewUtils.Dp2Px(context, 20);
    }

    public void setTextColor(int color) {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
    }

    public void clearFocus() {
        editText.setText(null);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setditTextFocusable(boolean focusable) {
        editText.requestFocus();
        editText.setFocusable(focusable);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
