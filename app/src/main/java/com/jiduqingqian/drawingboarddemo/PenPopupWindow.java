package com.jiduqingqian.drawingboarddemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;

/**
 * Created by qianhao on 16/10/17.
 */

public class PenPopupWindow extends PopupWindow {
    private CallBackClickListener callBackClickListener;
    private View conentView;
    private PaintResultView paintResultView;
    private int paintColor = Color.BLACK;
    private SeekBar seekBar;

    public PenPopupWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window_pen, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new ColorDrawable());
        setAnimationStyle(R.style.PopWindowStyle);
        seekBar = (SeekBar) conentView.findViewById(R.id.seekBar);
        paintResultView = (PaintResultView) conentView.findViewById(R.id.paintResultView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintResultView.setPaintSize(5 + 25 * progress / 100.0f);
                paintResultView.setPaintColor(paintColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                callBackClickListener.penSizeCallBack(5 + 25 * seekBar.getProgress() / 100.0f);
            }
        });
    }

    /**
     * 在指定控件上方显示，默认x座标与指定控件的中点x座标相同
     *
     * @param anchor
     */
    public void showAsPullUp(View anchor) {
        //保存anchor在屏幕中的位置
        int[] location = new int[2];
        //读取位置anchor座标
        anchor.getLocationOnScreen(location);
        //计算anchor中点
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] - anchor.getContext().getResources().getDimensionPixelSize(R.dimen.pen_popup_upload_height));
    }

    public void setCallBackClickListener(CallBackClickListener callBackClickListener) {
        this.callBackClickListener = callBackClickListener;
    }

    public interface CallBackClickListener {
        void penSizeCallBack(float size);
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
        paintResultView.setPaintColor(paintColor);
    }
}
