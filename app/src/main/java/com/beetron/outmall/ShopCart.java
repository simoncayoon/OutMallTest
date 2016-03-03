package com.beetron.outmall;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beetron.outmall.adapter.ShopCartAdapter;
import com.beetron.outmall.adapter.ShopCartFragment;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
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
import com.beetron.outmall.utils.TempDataManager;
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
public class ShopCart extends BaseFragment implements ShopCartFragment.ProCountChange {

    private static final String TAG = ShopCart.class.getSimpleName();
    private static final int FLAG_UPDATE_SELECT_ONE_KEY = -1;
    private ListView lvShopcart;
    private CheckBox checkSelectAll;
    private TextView tvAmount;
    private Button btnAccount;
    private List<ProSummary> dataLocalList;
    private LinkedHashMap<String, Integer> selectCache;

    private ShopCartResult shopcartResult;
    private ShopCartFragment shopCartAdapter;
    private Double currentAmount = 0.00;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.shop_cart_layout);
        initView();
        try {
            selectCache = new LinkedHashMap<>();
//            updateAmount(false, FLAG_UPDATE_SELECT_ONE_KEY);//初始化总价
            try {
                reqShopcart(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {

        //通知更新
        try {
            ((MainActivity) getActivity()).notifyCountChange();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            updateAmount(true, FLAG_UPDATE_SELECT_ONE_KEY);//初始化总价
        } catch (Exception e) {
            e.printStackTrace();
        }
        shopCartAdapter = new ShopCartFragment(ShopCart.this, dataLocalList);
        lvShopcart.setAdapter(shopCartAdapter);
    }

    /**
     * 获取服务端或者本地数据库购物车信息
     *
     * @param reqRemote 是否网络访问
     * @throws Exception
     */
    public void reqShopcart(Boolean reqRemote) throws Exception {

        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
            if (reqRemote) {
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
                                dataLocalList = new ArrayList<ProSummary>();
                                shopcartResult = resultEntity.getResult();

                                convertProSummary();

                                DBHelper.getInstance(getApplicationContext()).saveShopLocal(dataLocalList);
                                initData();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                    }
                });
                NetController.getInstance(getApplicationContext()).addToRequestQueue(getCategoryReq, TAG);
            } else {
                dataLocalList = DBHelper.getInstance(getApplicationContext()).getShopCartList();
                initData();

            }
        }
    }

    /**
     * 将远程数据结构转换成本地数据库数据结构
     */
    private void convertProSummary() {
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
            proSummary.setIsSelect(true);//默认全选
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
                        updateAmount(false, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//加操作
                    try {
                        updateAmount(true, position);
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
                    updateAmount(isChecked, FLAG_UPDATE_SELECT_ONE_KEY);
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

                Intent intent = new Intent(getActivity(), OrderFixActivity.class);
                try {
                    OrderInfoModel intentData = OrderInfoModel.getInstance();
                    intentData.setProDetail(dataLocalList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void proChange(final String flag, final int position) throws Exception {
        String url = "";
        if (flag.equals(ShopCartAdapter.ProCountChange.FLAG_ADD)) {
            url = NetInterface.HOST + NetInterface.METHON_ADD_SHOPCART;
        } else if (flag.equals(ShopCartAdapter.ProCountChange.FLAG_MINUS)) {
            url = NetInterface.HOST + NetInterface.METHON_MINUS_SHOPCART;
        }
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
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

                                //更新数据库信息
                                if (flag == FLAG_ADD) {
                                    DBHelper.getInstance(getApplicationContext()).addShopCart(dataLocalList.get(position));
                                } else if (flag == FLAG_MINUS) {
                                    DBHelper.getInstance(getApplicationContext()).deleteProByOne(dataLocalList.get(position));
                                }
                                //更新当前列表信息
                                if (resultCount == 0) {
                                    //更新已选择的产品的顺序
//                                    for(int index = position; index < selectCache.size(); index ++){
//                                        selectCac
//                                    }
                                    selectCache.remove(dataLocalList.get(position).getSid());

                                    dataLocalList.remove(position);
                                } else {
                                    dataLocalList.get(position).setCount(resultCount);//
                                }
                                shopCartAdapter.notifyDataSetChanged();
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
     * 更新数据
     *
     * @param status
     * @param position
     * @throws Exception
     */
    void updateAmount(Boolean status, int position) throws Exception {

        if (position > FLAG_UPDATE_SELECT_ONE_KEY) {//有数据情况

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
        } else {
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

        }

        if (selectCache.size() == dataLocalList.size()) {//判断是否全选
            checkSelectAll.setChecked(true);
        } else {
            checkSelectAll.setChecked(false);
        }

        SpannableString spannableString = new SpannableString("总价：￥" + currentAmount);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.home_page_general_red)),
                3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置颜色
        spannableString.setSpan(new AbsoluteSizeSpan(11, true), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置“￥”字体
        spannableString.setSpan(new AbsoluteSizeSpan(22, true), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置价格字体
        tvAmount.setText(spannableString);
    }

    public List<ProSummary> getDataShopCart() {
        return dataLocalList;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            reqShopcart(true);//访问本地更新
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteShopCart() throws Exception {

        String url = NetInterface.HOST + NetInterface.METHON_SHOP_CART_PRO_DELETE_BY_IDS;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(getApplicationContext()).getLoginState());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        if (generateDot().equals("")) {//没有选择内容
            Toast.makeText(getActivity(), getResources().getString(R.string.prompt_at_least_one), Toast.LENGTH_SHORT).show();
            return;
        }
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
                                for (Map.Entry<String, Integer> entry : selectCache.entrySet()) {
                                    DBHelper.getInstance(getActivity()).deleteShopById(entry.getKey());
                                    dataLocalList.remove(entry.getValue());
                                }
                                selectCache.clear();//清空当前选择的内容
                                shopCartAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            DebugFlags.logD(TAG, "失败");
                        }

                        initData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

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
}
