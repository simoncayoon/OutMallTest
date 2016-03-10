package com.beetron.outmall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.AddrAdapter;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.PageEntity;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/3.
 * Time: 16:23.
 */
public class AddrConsole extends Activity {

    private static final String TAG = AddrManager.class.getSimpleName();
    List<AddrInfoModel> addrList;
    private ListView llAddrList;
    private boolean isEdit = false;
    private AddrAdapter addrAdapter;
    private CusNaviView cusNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_console_layout);
        initView();

    }

    private void initView() {

        initNavi();
        llAddrList = (ListView) findViewById(R.id.shop_cart_detail_list);

        llAddrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddrConsole.this, AddrEdit.class);
                AddrInfoModel addrInfoModel = addrList.get(position);
                intent.putExtra(AddrEdit.INTENT_KEY_ADDR_EDIT, addrInfoModel);
                startActivity(intent);
            }
        });

        llAddrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int tempPosition = position;
                final CustomDialog.Builder builder = new CustomDialog.Builder(AddrConsole.this);
                builder.setTitle(R.string.prompt);
                builder.setMessage(R.string.prompt_addr_info_delete_confirm);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            addrDelete(tempPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void initNavi() {
        cusNaviView = (CusNaviView) findViewById(R.id.general_navi_id);
        cusNaviView.setNaviTitle(getResources().getString(R.string.navi_title_addr_manage));
        cusNaviView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        ((Button) cusNaviView.getLeftBtn()).setText(getResources().getString(R.string.navi_title_addr_select_test));//设置返回标题

        cusNaviView.setBtnView(CusNaviView.PUT_RIGHT, new TextView(this), 50, 50);
        ((TextView) cusNaviView.getRightBtn()).setText(getResources().getString(R.string.symbol_add));//初始化
        ((TextView) cusNaviView.getRightBtn()).setGravity(Gravity.CENTER);
        ((TextView) cusNaviView.getRightBtn()).setTextColor(getResources().getColor(R.color.general_main_title_color));
        ((TextView) cusNaviView.getRightBtn()).setTextSize(TypedValue.COMPLEX_UNIT_SP, CusNaviView.NAVI_TEXT_SIZE);
        cusNaviView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //去地址编辑
                startActivity(new Intent(AddrConsole.this, AddrEdit.class));
            }
        });
    }

    private void addrDelete(final int position) throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(AddrConsole.this, getResources().getString(R.string.prompt_delete_ing), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_ADDR_DELETE;
        AddrInfoModel postEntity = new AddrInfoModel();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        postEntity.setId(addrList.get(position).getId());
        String postString = new Gson().toJson(postEntity, new TypeToken<AddrInfoModel>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        BooleanSerializer serializer = new BooleanSerializer();
                        gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                        Gson gson = gsonBuilder.create();
                        ResultEntity resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {
                            addrList.remove(position);
                            addrAdapter.notifyDataSetChanged();
                        } else {
                            try {
                                DebugFlags.logD(TAG, resultEntity.getError());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void reqAddr() throws Exception {
        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(AddrConsole.this, getResources().getString(R.string.prompt_progress_loading), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_GET_ADDR_LIST;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            DebugFlags.logD(TAG, jsonObject.toString());
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            BooleanSerializer serializer = new BooleanSerializer();
                            gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                            gsonBuilder.registerTypeAdapter(boolean.class, serializer);
                            Gson gson = gsonBuilder.create();
                            ResultEntity<PageEntity<AddrInfoModel>> resultEntity = gson.fromJson(jsonObject.toString(),
                                    new TypeToken<ResultEntity<PageEntity<AddrInfoModel>>>() {
                                    }.getType());
                            if (resultEntity.isSuccess()) {
                                addrList = resultEntity.getResult().getList();
                                initData();
                            } else {
                                try {
                                    DebugFlags.logD(TAG, resultEntity.getError());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        mProgressHUD.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                mProgressHUD.dismiss();
            }
        });
        NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
    }

    private void initData() {

        addrAdapter = new AddrAdapter(AddrConsole.this, addrList);
        llAddrList.setAdapter(addrAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            reqAddr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
