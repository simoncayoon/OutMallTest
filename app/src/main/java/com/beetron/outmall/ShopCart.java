package com.beetron.outmall;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.ShopCartAdapter;
import com.beetron.outmall.adapter.ShopCartFragment;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.CustomDialog;
import com.beetron.outmall.customview.ProgressHUD;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.models.ShopCartResult;
import com.beetron.outmall.utils.BooleanSerializer;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.ShopCartChangReceiver;
import com.beetron.outmall.utils.TempDataManager;
import com.beetron.outmall.wxapi.WXPayEntryActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:32.
 */
public class ShopCart extends BaseFragment implements ShopCartFragment.ProCountChange, ShopCartChangReceiver.ShopCartChange {

    private static final String TAG = ShopCart.class.getSimpleName();
    private static final int FLAG_UPDATE_SELECT_ONE_KEY = -1;
    private static final String FLAG_UPDATE_SELECT_ITEM = "FLAG_UPDATE_SELECT_ITEM";
    private static final String FLAG_UPDATE_SELECT_ALL = "FLAG_UPDATE_SELECT_ALL";
    private static final String FLAG_UPDATE_SELECT_ITEM_COUNT = "FLAG_UPDATE_SELECT_ITEM_COUNT";
    private ListView lvShopcart;
    private CheckBox checkSelectAll;
    private TextView tvAmount;
    private Button btnAccount;
    private List<ProSummary> dataLocalList;
    private LinkedHashMap<String, Integer> selectCache;

    private ShopCartResult shopcartResult;
    private ShopCartFragment shopCartAdapter;
    private Double currentAmount = 0.00;
    private ProgressHUD mProgressHUD;
    private View viewEmpty;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.shop_cart_layout);
        initView();
        selectCache = new LinkedHashMap<>();
        dataLocalList = new ArrayList<ProSummary>();
        shopCartAdapter = new ShopCartFragment(ShopCart.this, dataLocalList);
        lvShopcart.setAdapter(shopCartAdapter);
        lvShopcart.setEmptyView(viewEmpty);
    }

    private void initData() {

        //通知更新
        try {
            ((MainActivity) getActivity()).notifyCountChange();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            updateAmount(FLAG_UPDATE_SELECT_ALL, true, FLAG_UPDATE_SELECT_ONE_KEY);//初始化总价，默认全部选择
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取服务端或者本地数据库购物车信息
     *
     * @param reqRemote 是否网络访问
     * @throws Exception
     */
    public void reqShopcart(Boolean reqRemote) throws Exception {

        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
            if (isFirstReq) {
                mProgressHUD = ProgressHUD.show(getActivity(), getResources().getString(R.string.prompt_progress_loading), true, false,
                        null);
            }
            String url = NetInterface.HOST + NetInterface.METHON_GET_SHOPCART;
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
                            DebugFlags.logD(TAG, jsonObject.toString());
                            Gson gson = new Gson();
                            ResultEntity<ShopCartResult> resultEntity = gson.fromJson(jsonObject.toString(),
                                    new TypeToken<ResultEntity<ShopCartResult>>() {
                                    }.getType());
                            shopcartResult = resultEntity.getResult();
                            convertProSummary();
                            DBHelper.getInstance(getApplicationContext()).saveShopLocal(dataLocalList);
//                                isUpdate = true;//状态更新为已更新内容
                            initData();
                            if (mProgressHUD != null) {
                                mProgressHUD.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (mProgressHUD != null) {
                        mProgressHUD.dismiss();
                    }
                }
            }) {
                @Override
                public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                    return super.setRetryPolicy(retryPolicy);
                }
            };
            NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
        }
    }

    /**
     * 将远程数据结构转换成本地数据库数据结构
     */
    private void convertProSummary() {

        dataLocalList.clear();
        for (int index = 0; index < shopcartResult.getList().size(); index++) {

            ShopCartModel shopCartModel = shopcartResult.getList().get(index);
            ProSummary proSummary = new ProSummary();
            proSummary.setFid(shopCartModel.getGs().getFid());
            proSummary.setSid(shopCartModel.getSid());
            proSummary.setXl(0);
            proSummary.setPrice1(shopCartModel.getGs().getPrice1());
            proSummary.setPrice2(shopCartModel.getGs().getPrice2());
            proSummary.setTitle(shopCartModel.getGs().getTitle());
            proSummary.setCount(shopCartModel.getNum());
            proSummary.setImg(shopCartModel.getGs().getImg());
            proSummary.setJianshu(shopCartModel.getGs().getJianshu());
            dataLocalList.add(proSummary);
        }
    }

    private void initView() {
        lvShopcart = (ListView) findViewById(R.id.shop_cart_detail_list);
        lvShopcart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //有加减操作、更新当前总价、更新选择缓存

                if (dataLocalList.get(position).getIsSelect()) {//减操作
                    try {
                        updateAmount(FLAG_UPDATE_SELECT_ITEM, false, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//加操作
                    try {
                        updateAmount(FLAG_UPDATE_SELECT_ITEM, true, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                shopCartAdapter.notifyDataSetChanged();
            }
        });
        checkSelectAll = (CheckBox) findViewById(R.id.cb_shop_cart_select_all);
        checkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) return;
                try {
                    updateAmount(FLAG_UPDATE_SELECT_ALL, isChecked, FLAG_UPDATE_SELECT_ONE_KEY);
                    shopCartAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tvAmount = (TextView) findViewById(R.id.tv_shop_cart_amount);
        btnAccount = (Button) findViewById(R.id.btn_to_account_shop_cart);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectCache.size() == 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.prompt_pro_select_none),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), OrderFixActivity.class);
                try {
                    OrderInfoModel intentData = new OrderInfoModel();

                    List<ProSummary> commitProList = new ArrayList<ProSummary>();
                    for (Map.Entry<String, Integer> entry : selectCache.entrySet()) {
                        DBHelper.getInstance(getActivity()).deleteShopById(entry.getKey());
                        ProSummary proSummary = dataLocalList.get(entry.getValue());//获取列表中对应位置的对象
                        commitProList.add(proSummary);
                    }
                    selectCache.clear();//清除选择状态
                    intentData.setProDetail(commitProList);
                    intent.putExtra(WXPayEntryActivity.INTENT_KEY_ORDER_MODEL, intentData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        viewEmpty = findViewById(R.id.shop_cart_empty);
        Button goShopping = (Button) viewEmpty.findViewById(R.id.btn_shopping_add);
        goShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((MainActivity)getActivity()).getIndicatorView().setCurrentItem(0, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void proChange(final String flag, final int position) throws Exception {

        ProSummary itemPro = dataLocalList.get(position);

        if (flag.equals(ShopCartAdapter.ProCountChange.FLAG_MINUS)) {
            if (itemPro.getCount() == 1) {//当前商品数量为最后一件
                deleteConfirm(flag, position);
                return;
            }
        }
        itemCountChange(flag, position);
    }

    private void deleteConfirm(final String flag, final int position) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(R.string.prompt);
        builder.setMessage(R.string.prompt_shop_cart_product_delete);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    itemCountChange(flag, position);
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
    }

    void itemCountChange(final String flag, final int position) throws Exception {
        String url = "";
        if (flag.equals(ShopCartAdapter.ProCountChange.FLAG_ADD)) {
            url = NetInterface.HOST + NetInterface.METHON_ADD_SHOPCART;
        } else if (flag.equals(ShopCartAdapter.ProCountChange.FLAG_MINUS)) {
            url = NetInterface.HOST + NetInterface.METHON_MINUS_SHOPCART;
        }
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        postJson.put("sid", dataLocalList.get(position).getSid());//添加sid
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());

                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)) {
                                JSONObject countJSON = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                int resultCount = countJSON.getInt("count");

                                ProSummary proSummary = dataLocalList.get(position);
                                //更新数据库信息
                                if (flag == FLAG_ADD) {
                                    DBHelper.getInstance(getApplicationContext()).addShopCart(dataLocalList.get(position));
                                    dataLocalList.get(position).setCount(resultCount);
                                    updateAmount(FLAG_UPDATE_SELECT_ITEM_COUNT, true, position);
                                } else if (flag == FLAG_MINUS) {
                                    DBHelper.getInstance(getApplicationContext()).deleteProByOne(dataLocalList.get(position));
                                    dataLocalList.get(position).setCount(resultCount);
                                    updateAmount(FLAG_UPDATE_SELECT_ITEM_COUNT, false, position);
                                    if (resultCount == 0) {
                                        try {
                                            selectCache.remove(dataLocalList.get(position).getSid());
                                            dataLocalList.remove(position);
                                            if (shopCartAdapter.getCount() > 0){
                                                viewEmpty.setVisibility(View.GONE);
                                            } else {
                                                viewEmpty.setVisibility(View.VISIBLE);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                //通知更新
                                try {
                                    ((MainActivity) getActivity()).notifyCountChange();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString(Constants.RESULT_ERROR_FIELD), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getActivity()).addToRequestQueue(getCategoryReq, TAG);
    }

    /**
     * @param flag
     * @param status
     * @param position
     * @throws Exception
     */
    void updateAmount(String flag, Boolean status, int position) throws Exception {


        if (flag.equals(FLAG_UPDATE_SELECT_ALL)) {//点击了全选按钮
            if (status) {
                currentAmount = 0.00;
                for (int index = 0; index < dataLocalList.size(); index++) {
                    dataLocalList.get(index).setIsSelect(true);
                    currentAmount += Integer.valueOf(dataLocalList.get(index).getCount()) *
                            Double.valueOf(dataLocalList.get(index).getPrice2());
                    selectCache.put(dataLocalList.get(index).getSid(), index);
                }
            } else {
                currentAmount = 0.00;
                for (int index = 0; index < dataLocalList.size(); index++) {
                    dataLocalList.get(index).setIsSelect(false);
                }
                selectCache.clear();
            }
        } else if (flag.equals(FLAG_UPDATE_SELECT_ITEM)) {//点击了列表项
            ProSummary selectItem = dataLocalList.get(position);
            if (status) {//价操作
                currentAmount += Double.valueOf(selectItem.getPrice2()) *
                        Integer.valueOf(selectItem.getCount());
                selectCache.put(selectItem.getSid(), position);
            } else {
                currentAmount -= Double.valueOf(dataLocalList.get(position).getPrice2()) *
                        Integer.valueOf(dataLocalList.get(position).getCount());
                selectCache.remove(selectItem.getSid());
            }

            dataLocalList.get(position).setIsSelect(dataLocalList.get(position).getIsSelect() ? false : true);
        } else if (flag.equals(FLAG_UPDATE_SELECT_ITEM_COUNT)) {//点击了列表项内容
            ProSummary selectItem = dataLocalList.get(position);
            if (selectItem.getIsSelect()) {
                if (status) {
                    currentAmount += selectItem.getPrice2();
                } else {
                    currentAmount -= selectItem.getPrice2();
                }
            }
        }

        if (selectCache.size() == dataLocalList.size() && selectCache.size() != 0) {//判断是否全选
            checkSelectAll.setChecked(true);
        } else {
            checkSelectAll.setChecked(false);
        }

        shopCartAdapter.notifyDataSetChanged();

        if (shopCartAdapter.getCount() > 0){
            viewEmpty.setVisibility(View.GONE);
        } else {
            viewEmpty.setVisibility(View.VISIBLE);
        }
        SpannableString spannableString = new SpannableString("总价：￥" + currentAmount);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.home_page_general_red)),
                3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置颜色
        spannableString.setSpan(new AbsoluteSizeSpan(11, true), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置“￥”字体
        spannableString.setSpan(new AbsoluteSizeSpan(22, true), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置价格字体
        tvAmount.setText(spannableString);
    }


    public void notifyDelete() throws Exception {
        if (generateDot().equals("")) {//没有选择内容
            Toast.makeText(getActivity(), getResources().getString(R.string.prompt_at_least_one), Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(R.string.prompt);
        builder.setMessage(R.string.prompt_shop_cart_product_delete);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteShopCart();
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
    }

    public void deleteShopCart() throws Exception {

        final ProgressHUD mProgressHUD;
        mProgressHUD = ProgressHUD.show(getActivity(), getResources().getString(R.string.prompt_delete_ing), true, false,
                null);
        String url = NetInterface.HOST + NetInterface.METHON_SHOP_CART_PRO_DELETE_BY_IDS;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        postJson.put("sid", generateDot());
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        BooleanSerializer serializer = new BooleanSerializer();
                        gsonBuilder.registerTypeAdapter(Boolean.class, serializer);
                        Gson gson = gsonBuilder.create();
                        ResultEntity<ShopCartResult> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<ShopCartResult>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {
                            DebugFlags.logD(TAG, "成功");
                            //本地数据库删除
                            try {
                                List<ProSummary> cacheList = new ArrayList<>();
                                for (Map.Entry<String, Integer> entry : selectCache.entrySet()) {
                                    DBHelper.getInstance(getActivity()).deleteShopById(entry.getKey());
                                    ProSummary proSummary = dataLocalList.get(entry.getValue());//获取列表中对应位置的对象
                                    cacheList.add(proSummary);
                                }
                                dataLocalList.removeAll(cacheList);
                                selectCache.clear();//清空当前选择的内容
                                updateAmount(FLAG_UPDATE_SELECT_ALL, false, FLAG_UPDATE_SELECT_ONE_KEY);

                                ((MainActivity) getActivity()).notifyCountChange();
                                //通知更新
                                try {
                                    ((MainActivity) getActivity()).notifyCountChange();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getActivity(), getResources().getString(R.string.prompt_delete_succeed),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            DebugFlags.logD(TAG, "失败");
                            Toast.makeText(getActivity(), getResources().getString(R.string.prompt_commit_faild),
                                    Toast.LENGTH_SHORT).show();
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


    /**
     * 生成逗号分隔的字符串
     *
     * @return
     */
    String generateDot() {

        String generateStr = "";
        StringBuilder sb = new StringBuilder(generateStr);

        if (selectCache.size() == 0) {
            return generateStr;
        }

        int count = 0;
        for (Map.Entry<String, Integer> entry : selectCache.entrySet()) {
            sb.append(entry.getKey());//sid
            count++;
            if (count == selectCache.size()) {
                break;
            }
            sb.append(",");
        }
        DebugFlags.logD(TAG, "生成的逗号字符串 ：" + sb.toString());
        return sb.toString();
    }

    boolean isFromBroadcast = false;
    boolean isFirstReq = true;

    @Override
    public void shopCartDataChange() {
        try {
            isFromBroadcast = true;
            reqShopcart(false);
            isFirstReq = false;
            isFromBroadcast = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFromBroadcast) {
            return;
        }
        try {
            reqShopcart(true);
            isFirstReq = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearList() {
        dataLocalList.clear();
        selectCache.clear();
        try {
            updateAmount(FLAG_UPDATE_SELECT_ALL, false, FLAG_UPDATE_SELECT_ONE_KEY);//初始化总价，默认全部选择
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
