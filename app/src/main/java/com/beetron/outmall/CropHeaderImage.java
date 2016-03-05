package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.customview.ClipView;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.utils.CommonHelper;

/**
 * Created by luomaozhong on 16/3/2.
 */
public class CropHeaderImage extends Activity implements View.OnTouchListener {
    private ImageView srcPic;
    private ClipView clipview;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;
    private CusNaviView cusNaviView;
    private ProgressHUD mProgressHUD;

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setNaviTitle("个人头像");

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.me));//设置返回标题
        ((Button) cusNaviView.getRightBtn()).setText(getResources().getString(R.string.save));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //Bitmap clipBitmap = getBitmap();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] bitmapByte = baos.toByteArray();
//
//                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), CropHeaderImage.class);
//                intent.putExtra("bitmap", bitmapByte);
//                startActivity(intent);

                Constants.mBitmap = getBitmap();
                Intent mIntent = new Intent();
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_header_image);
        initNavi();
        srcPic = (ImageView) this.findViewById(R.id.src_pic);
        mProgressHUD = ProgressHUD.show(this, "正在加载...", true, false,
                null);
        final int type = getIntent().getExtras().getInt("type");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                if (UserInfo.TAKE_PICTURE == type) {
                    Log.d("CropHeaderImage","enter CropHeaderImage take_picture");
                    int degree = CommonHelper.getBitmapDegree(Environment
                            .getExternalStorageDirectory()
                            + "/image.jpg");
                    if (degree != 0) {
                        Constants.mBitmap = CommonHelper.rotateBitmapByDegree(CommonHelper.getDiskBitmap(Environment
                                .getExternalStorageDirectory()
                                + "/image.jpg"), degree);
                    } else {
                        Constants.mBitmap = CommonHelper.getDiskBitmap(Environment
                                .getExternalStorageDirectory()
                                + "/image.jpg");
                    }

                }
                Message message = mhandler.obtainMessage();
                message.sendToTarget();
//            }
//        }).start();

    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressHUD.dismiss();
            srcPic.setOnTouchListener(CropHeaderImage.this);
            ViewTreeObserver observer = srcPic.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                public void onGlobalLayout() {
                    srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    initClipView(srcPic.getTop());
                }
            });
        }
    };

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        clipview = new ClipView(CropHeaderImage.this);
        clipview.setCustomTopBarHeight(top);
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = Constants.mBitmap.getWidth();
                int imageHeight = Constants.mBitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(Constants.mBitmap);
            }
        });

        this.addContentView(clipview, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()
                        + statusBarHeight, clipview.getClipWidth(),
                clipview.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

}