package com.jiduqingqian.drawingboarddemo;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace;

    /**
     * @param space 传入的值，其单位视为dp
     */
    public SpaceItemDecoration(Context context, int space) {
        final float scale = context.getResources().getDisplayMetrics().density;
        this.mSpace = (int) (space * scale + 0.5f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int itemCount = mAdapter.getItemCount();
//        int pos = parent.getChildAdapterPosition(view);
//        outRect.left = 0;
//        outRect.top = 0;
//        outRect.bottom = 0;
//        if (pos != (itemCount - 1)) {
//            outRect.right = mSpace;
//        } else {
//            outRect.right = 0;
//        }

        outRect.left = mSpace;
        outRect.right = mSpace;
//        outRect.bottom = mSpace;
//        if (parent.getChildPosition(view) != 0)
//            outRect.top = mSpace;
    }
}