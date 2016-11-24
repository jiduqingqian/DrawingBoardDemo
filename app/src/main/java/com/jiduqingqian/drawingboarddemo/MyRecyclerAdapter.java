package com.jiduqingqian.drawingboarddemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private int[] colors = new int[]{
            R.drawable.shape_selector_color1, R.drawable.shape_selector_color2, R.drawable.shape_selector_color3,
            R.drawable.shape_selector_color4, R.drawable.shape_selector_color5, R.drawable.shape_selector_color6,
            R.drawable.shape_selector_color7, R.drawable.shape_selector_color8, R.drawable.shape_selector_color9,
            R.drawable.shape_selector_color10, R.drawable.shape_selector_color11, R.drawable.shape_selector_color12,
            R.drawable.shape_selector_color13, R.drawable.shape_selector_color14, R.drawable.shape_selector_color15,
            R.drawable.shape_selector_color16, R.drawable.shape_selector_color17, R.drawable.shape_selector_color18
    };
    private Context mContext;
    private ColorSelectorListenter colorSelectorListenter;
    private int selectorPosition;

    public MyRecyclerAdapter(Context context, ColorSelectorListenter colorSelectorListenter) {
        this.mContext = context;
        this.colorSelectorListenter = colorSelectorListenter;
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.imageView.setImageResource(colors[position]);
        if (this.selectorPosition == position) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorPosition = position;
                colorSelectorListenter.colorSelector(selectorPosition);
                notifyDataSetChanged();
            }
        });
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_color_selector, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public interface ColorSelectorListenter {
        void colorSelector(int item);
    }
}