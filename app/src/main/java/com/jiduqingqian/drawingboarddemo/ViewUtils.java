package com.jiduqingqian.drawingboarddemo;

import android.content.Context;


//import com.gracepsy.knarfytrebil.view.DialogShareContent;


public class ViewUtils {




    /*

    public static void shareContent(Activity context,String str){

      //  ShareContentDialog sc=new ShareContentDialog(context);

        DialogShareContent sc=new DialogShareContent(context);


         sc.setStrcontent(str);
         sc.show();

    }
*/


    public static int Dp2Px(Context context,float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context,float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }




}
