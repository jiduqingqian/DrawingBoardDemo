package com.jiduqingqian.drawingboarddemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

/**
 * Created by qianhao on 16/10/17.
 */

public class EraserPopupWindow extends PopupWindow {
    private CallBackClickListener callBackClickListener;
    private View conentView;
    private int[] penSizes = new int[]{10, 20, 30};

    public EraserPopupWindow(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window_eraser, null);
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
        RadioGroup radioGroup = ((RadioGroup) conentView.findViewById(R.id.eraserRadioGroup));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < penSizes.length; i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        if (callBackClickListener != null) {
                            callBackClickListener.reaserSizeCallBack(penSizes[2 - i]);
                        }
                        return;
                    }
                }
            }
        });
        radioGroup.check(radioGroup.getChildAt(2).getId());
    }

    /**
     * 在指定控件上方显示，默认x座标与指定控件的中点x座标相同
     *
     * @param anchor
     * @param xoff
     */
    public void showAsPullUp(View anchor, int xoff, View view) {
        //保存anchor在屏幕中的位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        //保存anchor上部中点
        int[] anchorCenter = new int[2];
        //读取位置anchor座标
        anchor.getLocationOnScreen(anchorCenter);
        //计算anchor中点
        anchorCenter[0] += anchor.getWidth() / 2;
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorCenter[0] + xoff, location[1] - view.getContext().getResources().getDimensionPixelSize(R.dimen.eraser_popup_upload_height));
    }

    public void setCallBackClickListener(CallBackClickListener callBackClickListener) {
        this.callBackClickListener = callBackClickListener;
    }

    public interface CallBackClickListener {
        void reaserSizeCallBack(int size);
    }
}
