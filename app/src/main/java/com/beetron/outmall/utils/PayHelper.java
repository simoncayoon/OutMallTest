package com.beetron.outmall.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.PayInfoModel;
import com.beetron.outmall.models.PayRequestModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luomaozhong on 16/3/6.
 */
public class PayHelper {

    private PayListenner listenner;
    private String TAG="PayHelper";

    public PayHelper(PayListenner listenner) {
        this.listenner = listenner;
    }

    /***
     * 获取微信支付接口信息
     * @param c
     * @param orderid
     * @throws JSONException
     */
    public void getPayInfo(Context c,String orderid) throws JSONException {

        String url = NetInterface.HOST + NetInterface.METHON_WEIXIN_PAY;
        PayRequestModel postEntity = new PayRequestModel();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setOrderid(orderid);

        String postString = new Gson().toJson(postEntity, new TypeToken<PayRequestModel>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                JSONObject object = jsonObject.getJSONObject("result").getJSONObject("data");
                                PayInfoModel payInfoModel = new PayInfoModel();
                                payInfoModel.setAppid(object.getString("appid"));
                                payInfoModel.setPartnerid(object.getString("partnerid"));
                                payInfoModel.setPrepayid(object.getString("prepayid"));
                                payInfoModel.setPackageName(object.getString("package"));
                                payInfoModel.setNoncestr(object.getString("noncestr"));
                                payInfoModel.setTimestamp(object.getString("timestamp"));
                                payInfoModel.setSign(object.getString("sign"));
                                listenner.payInfo(1, payInfoModel);
                            } else {
                                listenner.payInfo(0, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listenner.payInfo(0, null);
            }
        });
        NetController.getInstance(c).addToRequestQueue(getCategoryReq, TAG);
    }

    /***
     * 获取微信支付接口信息
     * @param c
     * @param orderid
     * @throws JSONException
     */
    public void upPayInfo(Context c,String orderid) throws JSONException {

        String url = NetInterface.HOST + NetInterface.METHON_WEIXIN_PAY_NOTICE;
        PayRequestModel postEntity = new PayRequestModel();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setOrderid(orderid);

        String postString = new Gson().toJson(postEntity, new TypeToken<PayRequestModel>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.getString("isSuccess").equals("1")) {
                                listenner.payInfo(1, null);
                            } else {
                                listenner.payInfo(0, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listenner.payInfo(0, null);
            }
        });
        NetController.getInstance(c).addToRequestQueue(getCategoryReq, TAG);
    }

    public interface PayListenner {
        public void payInfo(int status, PayInfoModel payInfoModel);
    }
}
