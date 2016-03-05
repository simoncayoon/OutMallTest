package com.beetron.outmall;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.IphoneDialogCreator;
import com.beetron.outmall.customview.IphoneDialogItem;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.PostUserInfo;
import com.beetron.outmall.models.UserInfoModel;
import com.beetron.outmall.utils.CommonHelper;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.FileDataHelper;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/27.
 * Time: 10:40.
 */
public class UserInfo extends Activity implements View.OnClickListener {

    private static final String TAG = UserInfo.class.getSimpleName();
    UserInfoModel userInfoSummary;
    private TextView tvNickName, tvGender, tvEmail, tvProvince, tvCity, tvArea, tvStreet;
    private LinearLayout llPortrait, llGender, llNickName, llEmail, llProvince, llCity, llArea, llStreet;
    private CusNaviView cusNaviView;
    public static final String FLAG_NAVI_ROOT = "FLAG_NAVI_ROOT";
    public static final int FLAG_HEAD_REFRESH = 1;
    private DBHelper dbHelper;
    private RoundedImageView headerImage;
    private ProgressHUD mProgressHUD;

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setBtn(CusNaviView.PUT_RIGHT, CusNaviView.NAVI_WRAP_CONTENT, 56);
        cusNaviView.setNaviTitle("修改资料");

        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.me));//设置返回标题
        ((Button) cusNaviView.getRightBtn()).setText(getResources().getString(R.string.save));//设置返回标题

        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                try {
                    upUserData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dbHelper.saveUserInfo(userInfoSummary);
                //finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        dbHelper = DBHelper.getInstance(this);
        userInfoSummary = dbHelper.getUserInfo();
        Log.d("UserInfo", "头像Url:" + userInfoSummary.getHeadimg());
        initNavi();
        initView();

    }


    /***
     * 更新用户信息
     *
     * @throws Exception
     */
    private void upUserData() throws Exception {
        mProgressHUD = ProgressHUD.show(this, "正在加载...", true, false,
                null);

        String url = NetInterface.HOST + NetInterface.METHON_EDIT_USER_INFO;
        PostUserInfo postEntity = new PostUserInfo();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(userInfoSummary.getUid());
        postEntity.setIsLogin("1");
        postEntity.setCity(tvCity.getText().toString());
        postEntity.setProvince(tvProvince.getText().toString());
        postEntity.setArea(tvArea.getText().toString());
        postEntity.setAddress(tvStreet.getText().toString());
        postEntity.setMail(tvEmail.getText().toString());
        postEntity.setNickname(tvNickName.getText().toString());
        String img = FileDataHelper.bitmapToBase64(headerImage.getDrawingCache());
        postEntity.setImg(img);
        if (tvGender.getText().equals("男")) {
            postEntity.setSex("1");
        } else {
            postEntity.setSex("0");
        }

        String postString = new Gson().toJson(postEntity, new TypeToken<PostUserInfo>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (mProgressHUD.isShowing()) {
                            mProgressHUD.dismiss();
                        }
                        DebugFlags.logD(TAG, jsonObject.toString());
//                        Toast.makeText(UserInfo.this, "" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                jsonObject = jsonObject.getJSONObject("result").getJSONObject("uid");
                                userInfoSummary.setHeadimg(jsonObject.getString("headimg"));
                                dbHelper.saveUserInfo(userInfoSummary);
                                Toast.makeText(UserInfo.this, "保存成功！", Toast.LENGTH_SHORT).show();
//                                Intent mIntent = new Intent();
//                                setResult(RESULT_OK, mIntent);
//                                finish();
                            } else {
                                Toast.makeText(UserInfo.this, "提交失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(UserInfo.this, "提交失败！", Toast.LENGTH_SHORT).show();
                if (mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();
                }
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }


    private void initView() {
        llPortrait = (LinearLayout) findViewById(R.id.ll_portrait_layout);
        llNickName = (LinearLayout) findViewById(R.id.ll_nick_name_layout);
        llEmail = (LinearLayout) findViewById(R.id.ll_email_layout);
        llGender = (LinearLayout) findViewById(R.id.ll_gender_layout);
        llProvince = (LinearLayout) findViewById(R.id.ll_province_layout);
        llCity = (LinearLayout) findViewById(R.id.ll_city_layout);
        llArea = (LinearLayout) findViewById(R.id.ll_area_layout);
        llStreet = (LinearLayout) findViewById(R.id.ll_street_layout);

        tvNickName = (TextView) findViewById(R.id.user_info_nick_name);
        tvEmail = (TextView) findViewById(R.id.user_info_email);
        tvGender = (TextView) findViewById(R.id.user_info_gender);
        tvProvince = (TextView) findViewById(R.id.user_info_province);
        tvCity = (TextView) findViewById(R.id.user_info_city);
        tvArea = (TextView) findViewById(R.id.user_info_area);
        tvStreet = (TextView) findViewById(R.id.user_info_street);
        headerImage = (RoundedImageView) findViewById(R.id.user_info_protrait_img);
        headerImage.setDrawingCacheEnabled(true);

        tvNickName.setText(userInfoSummary.getNickname());
        tvEmail.setText(userInfoSummary.getMail());
        try {
            if (userInfoSummary.getSex().equals("1")) {
                tvGender.setText("男");
            } else {
                tvGender.setText("女");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvProvince.setText(userInfoSummary.getProvince());
        tvCity.setText(userInfoSummary.getCity());
        tvArea.setText(userInfoSummary.getArea());
        tvStreet.setText(userInfoSummary.getAddress());

        Glide.with(this).load(userInfoSummary.getHeadimg()).placeholder(R.mipmap.default_avatar).into(headerImage);
        llNickName.setOnClickListener(this);
        llEmail.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llProvince.setOnClickListener(this);
        llCity.setOnClickListener(this);
        llArea.setOnClickListener(this);
        llStreet.setOnClickListener(this);
        llPortrait.setOnClickListener(this);
        llPortrait.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(UserInfo.this, UserInfoFix.class);
        if (v.getId() == R.id.ll_nick_name_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_NICKNAME_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvNickName.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_NICKNAME_REQ);
            return;
        }
        if (v.getId() == R.id.ll_gender_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_GENDER_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvGender.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_GENDER_REQ);
            return;
        }
        if (v.getId() == R.id.ll_email_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_EMAIL_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvEmail.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_EMAIL_REQ);
            return;
        }

        if (v.getId() == R.id.ll_province_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_PROVINCE_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvProvince.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_PROVINCE_REQ);
            return;
        }

        if (v.getId() == R.id.ll_city_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_CITY_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvCity.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_CITY_REQ);
            return;
        }

        if (v.getId() == R.id.ll_area_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_AREA_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvArea.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_AREA_REQ);
            return;
        }

        if (v.getId() == R.id.ll_street_layout) {
            intent.putExtra(UserInfoFix.INTENT_KEY, UserInfoFix.INTENT_FLAG_STREET_REQ);
            intent.putExtra(UserInfoFix.INTENT_VALUE, tvStreet.getText());
            startActivityForResult(intent, UserInfoFix.INTENT_FLAG_STREET_REQ);
            return;
        }

        if (v.getId() == R.id.ll_portrait_layout) {

            ArrayList<IphoneDialogItem> items = null;
            items = new ArrayList<IphoneDialogItem>();

            items.add(new IphoneDialogItem("拍照", R.layout.iphone_dialog_normal,
                    onSelectPhotographListener));
            items.add(new IphoneDialogItem("从相册选择",
                    R.layout.iphone_dialog_normal, onSelectAlbumsListener));
            items.add(new IphoneDialogItem("取消",
                    R.layout.iphone_dialog_special, null));
            IphoneDialogCreator.createCustomDialog(this, items,
                    R.style.CustomDialogOld);
        }

    }


    public static final int TAKE_PICTURE = 0;// 选择照相
    public static final int CHOOSE_PICTURE = 1;// 选择相册
    public static final int CROP_PICTURE = 2;//图片剪切
    // 拍照
    private View.OnClickListener onSelectPhotographListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent openCameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            Uri imageUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "image.jpg"));
            // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
            mProgressHUD = ProgressHUD.show(UserInfo.this, "正在加载...", true, false,
                    null);
        }

    };

    // 从相册选择
    private View.OnClickListener onSelectAlbumsListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            openAlbumIntent.setType("image/*");
            startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            mProgressHUD = ProgressHUD.show(UserInfo.this, "正在加载...", true, false,
                    null);
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        DebugFlags.logD(TAG, "onActivityResult,requestCode:" + requestCode + ",resultCode:" + resultCode);

        if (resultCode == RESULT_OK) {
            Log.d("UserInfo", "entert onActivityResult");

            if (requestCode == TAKE_PICTURE || requestCode == CHOOSE_PICTURE) {
                if (Constants.mBitmap!=null){
                    Constants.mBitmap.recycle();
                    System.gc();
                }
                //头像处理
                getPicData(requestCode, resultCode, data);
                if (mProgressHUD != null) {
                    if (mProgressHUD.isShowing()) {
                        mProgressHUD.hide();
                        mProgressHUD.dismiss();
                    }
                }
//                headerImage.setImageBitmap(Constants.mBitmap);
                Intent intent = new Intent(UserInfo.this,
                        CropHeaderImage.class);
                intent.putExtra("type", requestCode);
                startActivityForResult(intent, CROP_PICTURE);
            } else {
                if (requestCode == CROP_PICTURE) {
                    //图片剪切完成
                    headerImage.setImageBitmap(Constants.mBitmap);
                } else {
                    //个人信息修改
                    String backSring = data.getStringExtra(UserInfoFix.RETURN_BACK_STRING_KEY);
                    if (requestCode == UserInfoFix.INTENT_FLAG_NICKNAME_REQ) {
                        tvNickName.setText(backSring);
                        userInfoSummary.setNickname(backSring);
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_EMAIL_REQ) {
                        TempDataManager.getInstance(UserInfo.this).setUserSig(backSring);
                        tvEmail.setText(backSring);
                        userInfoSummary.setMail(backSring);
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_GENDER_REQ) {
                        tvGender.setText(backSring);
                        if (backSring.equals("男")) {
                            userInfoSummary.setSex("1");
                        } else {
                            userInfoSummary.setSex("0");
                        }
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_PROVINCE_REQ) {
                        tvProvince.setText(backSring);
                        userInfoSummary.setProvince(backSring);
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_CITY_REQ) {
                        tvCity.setText(backSring);
                        userInfoSummary.setCity(backSring);
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_AREA_REQ) {
                        tvArea.setText(backSring);
                        userInfoSummary.setArea(backSring);
                    }
                    if (requestCode == UserInfoFix.INTENT_FLAG_STREET_REQ) {
                        tvStreet.setText(backSring);
                        userInfoSummary.setAddress(backSring);
                    }
                }
            }
        } else {
            if (mProgressHUD != null) {
                mProgressHUD.hide();
            }
        }

    }


    /**
     * 获取图片数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void getPicData(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CHOOSE_PICTURE) {
                ContentResolver resolver = getContentResolver();
                // 照片的原始资源地址
                Uri originalUri = data.getData();
                try {
                    // 使用ContentProvider通过URI获取原始图片
                    Constants.mBitmap = MediaStore.Images.Media.getBitmap(
                            resolver, originalUri);
                    if (Constants.mBitmap != null) {
                        // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        Constants.mBitmap = CommonHelper
                                .compressImageByScale(Constants.mBitmap);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mProgressHUD.hide();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    protected void onDestroy() {
        // 回收bitmap
        if (Constants.mBitmap != null) {
            Constants.mBitmap.recycle();
        }
        if (mProgressHUD != null) {
            mProgressHUD.hide();
            mProgressHUD.dismiss();
        }
        super.onDestroy();
    }


}
