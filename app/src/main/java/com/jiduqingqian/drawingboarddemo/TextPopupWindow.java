package com.jiduqingqian.drawingboarddemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;

/**
 * Created by qianhao on 16/10/17.
 */

public class TextPopupWindow extends PopupWindow implements View.OnClickListener {
    private CallBackClickListener callBackClickListener;
    private View conentView;
    private RecyclerView recyclerView;
    private Activity context;
    private ImageButton painting_color_previous_button, painting_color_next_button;
    private int[] colors = new int[]{R.color.selector_color1, R.color.selector_color2, R.color.selector_color3,
            R.color.selector_color4, R.color.selector_color5, R.color.selector_color6, R.color.selector_color7,
            R.color.selector_color8, R.color.selector_color9, R.color.selector_color10, R.color.selector_color11,
            R.color.selector_color12, R.color.selector_color13, R.color.selector_color14, R.color.selector_color15,
            R.color.selector_color16, R.color.selector_color17, R.color.selector_color18};

    public TextPopupWindow(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window_text, null);
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
        painting_color_previous_button = (ImageButton) conentView.findViewById(R.id.painting_color_previous_button);
        painting_color_next_button = (ImageButton) conentView.findViewById(R.id.painting_color_next_button);
        painting_color_previous_button.setOnClickListener(this);
        painting_color_next_button.setOnClickListener(this);
        recyclerView = (RecyclerView) conentView.findViewById(R.id.recyclerView);
        initView();
    }

    LinearLayoutManager layoutManager;

    private void initView() {
        layoutManager = new LinearLayoutManager(context);
//设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
//设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
//设置Adapter
        recyclerView.setAdapter(new MyRecyclerAdapter(context, new MyRecyclerAdapter.ColorSelectorListenter() {
            @Override
            public void colorSelector(int item) {
                callBackClickListener.textColorCallBack(colors[item]);
            }
        }));
//设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpaceItemDecoration(context,16));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {//最左
                    painting_color_previous_button.setVisibility(View.GONE);
                    painting_color_next_button.setVisibility(View.VISIBLE);
                } else if (layoutManager.findLastCompletelyVisibleItemPosition() == colors.length - 1) {//最右
                    painting_color_previous_button.setVisibility(View.VISIBLE);
                    painting_color_next_button.setVisibility(View.GONE);
                } else {
                    painting_color_previous_button.setVisibility(View.VISIBLE);
                    painting_color_next_button.setVisibility(View.VISIBLE);
                }
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
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] - anchor.getContext().getResources().getDimensionPixelSize(R.dimen.popup_text_height));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.painting_color_next_button:
                int lastVisibleItemPosition1 = layoutManager.findLastVisibleItemPosition();
                int firstVisibleItemPosition1 = layoutManager.findFirstVisibleItemPosition();
                recyclerView.smoothScrollToPosition(lastVisibleItemPosition1 + (lastVisibleItemPosition1 - firstVisibleItemPosition1 + 1));
                break;
            case R.id.painting_color_previous_button:
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int VisibleItemPosition = layoutManager.findLastVisibleItemPosition() - firstVisibleItemPosition + 1;
                if (firstVisibleItemPosition < VisibleItemPosition) {
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    recyclerView.smoothScrollToPosition(firstVisibleItemPosition - VisibleItemPosition);
                }
                break;
        }
    }

    public void setCallBackClickListener(CallBackClickListener callBackClickListener) {
        this.callBackClickListener = callBackClickListener;
    }

    public interface CallBackClickListener {
        void textColorCallBack(int colorId);
    }
}
