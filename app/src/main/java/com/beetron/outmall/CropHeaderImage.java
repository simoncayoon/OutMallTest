package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.beetron.outmall.Crop.CropImageView4;
import com.beetron.outmall.constant.Constants;

/**
 * Created by luomaozhong on 16/3/2.
 */
public class CropHeaderImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_header_image);

        final CropImageView4 mCropImage = (CropImageView4) findViewById(R.id.cropImg);
        //设置要裁剪的图片和默认的裁剪区域
        Drawable drawable = new BitmapDrawable(Constants.mBitmap);
        mCropImage.setDrawable(drawable, 200, 200);


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {

            /* （非 Javadoc）
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             * 开启一个新线程来保存图片
             */
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //得到裁剪好的图片
                        Constants.mBitmap = mCropImage.getCropImage();
//                        FileUtil.writeImage(bitmap, FileUtil.SDCARD_PAHT+"/crop.png", 100);
                        Intent mIntent = new Intent();
                        //mIntent.putExtra("cropImagePath", FileUtil.SDCARD_PAHT+"/crop.png");
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }).start();
            }
        });
    }
}
