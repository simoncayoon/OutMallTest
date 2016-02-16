package com.beetron.outmall.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Eteng with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com
 * Date: 15/7/10
 * Time: 下午2:35
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private static final String TAG = BitmapCache.class.getSimpleName();

    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    private Bitmap resizeBitmap(Bitmap bitmap, float scale) {
        if (bitmap.getWidth() > 720) {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale); //长和宽放大缩小的比例
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizeBmp;
        }
        return bitmap;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = resizeBitmap(bitmap, 0.25f);
        }
        if (bitmap != null) {
            mCache.put(url, bitmap);
        }
    }
}
