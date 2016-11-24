package com.jiduqingqian.drawingboarddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingBoardActivity extends FragmentActivity implements View.OnClickListener {
    private TextView painting_guidance_tv;
    private ImageButton painting_guidance, painting_color_button;
    private EraserPopupWindow eraserPopupWindow;
    private PenPopupWindow penPopupWindow;
    private TextPopupWindow textPopupWindow;
    private BorderView borderView;
    private String fileName;
    private String path;
    private String prompt, backgroundImage;
    private View ll_split_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prompt = getIntent().getStringExtra("prompt");
        backgroundImage = getIntent().getStringExtra("backgroundImage");
        fileName = "hua" + System.currentTimeMillis() + ".jpeg";
        setContentView(R.layout.activity_drawing_board);
        initView();
    }

    private void initView() {
        borderView = (BorderView) findViewById(R.id.borderView);
        ll_split_line = findViewById(R.id.ll_split_line);
        painting_guidance_tv = (TextView) findViewById(R.id.painting_guidance_tv);
        painting_guidance = (ImageButton) findViewById(R.id.painting_guidance);
        painting_color_button = (ImageButton) findViewById(R.id.painting_color_button);
        eraserPopupWindow = new EraserPopupWindow(this);
        penPopupWindow = new PenPopupWindow(this);
        textPopupWindow = new TextPopupWindow(this);

        textPopupWindow.setCallBackClickListener(new TextPopupWindow.CallBackClickListener() {
            @Override
            public void textColorCallBack(int colorId) {
                textPopupWindow.dismiss();
                painting_color_button.setBackgroundColor(getResources().getColor(colorId));
                penPopupWindow.setPaintColor(getResources().getColor(colorId));
                borderView.setTextColor(getResources().getColor(colorId));
                borderView.setPaintColor(getResources().getColor(colorId));
            }
        });

        penPopupWindow.setCallBackClickListener(new PenPopupWindow.CallBackClickListener() {
            @Override
            public void penSizeCallBack(float size) {
                penPopupWindow.dismiss();
                borderView.setPaintWidth(size);
            }
        });

        eraserPopupWindow.setCallBackClickListener(new EraserPopupWindow.CallBackClickListener() {
            @Override
            public void reaserSizeCallBack(int size) {
                borderView.setEraserSize(size);
                eraserPopupWindow.dismiss();
            }
        });
        if (!TextUtils.isEmpty(prompt)) {
            painting_guidance_tv.setText(prompt);
            findViewById(R.id.ll_prompt).setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(backgroundImage)) {
            ImageLoader.getInstance().loadImage(backgroundImage, new DisplayImageOptions.Builder().build(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    borderView.setCutoutImage(loadedImage);
                }
            });
        } else {
            borderView.setCutoutImage(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImage://返回
                finish();
                break;
            case R.id.painting_guidance://提示语收缩
                if (painting_guidance_tv.getVisibility() == View.GONE) {
                    painting_guidance.setImageResource(R.mipmap.painting_guidance_pack);
                    painting_guidance_tv.setVisibility(View.VISIBLE);
                } else {
                    painting_guidance.setImageResource(R.mipmap.painting_guidance_expand);
                    painting_guidance_tv.setVisibility(View.GONE);
                }
                break;
            case R.id.sending_button://发送
            case R.id.painting_save_button://保存
                borderView.sureEdittext();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + getResources().getString(R.string.app_name);
                } else {
                    path = StorageUtils.getOwnCacheDirectory(this, "jiduqingqian/image/userSaveImage").getPath();//保存的确切位置  }
                }
                File saveImageFile = saveFile(borderView.getResultBitmap(), fileName, path);
                if (saveImageFile != null) {
                    if (v.getId() != R.id.sending_button) {
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(saveImageFile);
                    intent.setData(uri);
                    sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
                    if (v.getId() == R.id.sending_button) {
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            case R.id.painting_color_button:
                textPopupWindow.showAsPullUp(ll_split_line);
                break;
            case R.id.painting_t_button://文字
                borderView.setPenType(2);
                borderView.showEditText();
                break;
            case R.id.painting_eraser_button://橡皮擦
                borderView.sureEdittext();
                borderView.setPenType(1);
                eraserPopupWindow.showAsPullUp(v, getResources().getDimensionPixelSize(R.dimen.radio_group_bottom_width) - ScreenUtils.getScreenWidth(this) / 8, ll_split_line);
                break;
            case R.id.painting_pen_button://画笔
                borderView.sureEdittext();
                borderView.setPenType(0);
                penPopupWindow.showAsPullUp(ll_split_line);
                break;
        }

    }

    public File saveFile(Bitmap bm, String fileName, String path) {

        File myCaptureFile = null;
        try {
            String subForder = path;
            File foder = new File(subForder);
            if (!foder.exists()) {
                foder.mkdirs();
            }
            myCaptureFile = new File(subForder, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return myCaptureFile;
    }

}
