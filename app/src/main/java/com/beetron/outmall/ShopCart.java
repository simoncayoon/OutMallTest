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
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ResultEntity;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.models.ShopCartResult;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 15:32.
 */
public class ShopCart extends BaseFragment implements ShopCartAdapter.ProCountChange {

    private static final String TAG = ShopCart.class.getSimpleName();
    private ListView lvShopcart;
    private CheckBox checkSelectAll;
    private TextView tvAmount;
    private Button btnAccount;
    private List<ShopCartModel> dataShopCart;
    private List<Integer> indexCache;

    private ShopCartResult shopcartResult;
    private ShopCartAdapter shopCartAdapter;
    private Double currentAmount = 0.00;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.shop_cart_layout);

        initView();
        try {
            updateAmount(0.00);//初始化总价
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {

        //通知更新
        ((MainActivity) getActivity()).notifyCountChange();

        try {
            updateAmount(0.00);//初始化总价
        } catch (Exception e) {
            e.printStackTrace();
        }
        shopCartAdapter = new ShopCartAdapter(ShopCart.this, dataShopCart);
        lvShopcart.setAdapter(shopCartAdapter);
    }

    public void reqShopcart() throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_GET_SHOPCART;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
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
                        dataShopCart = shopcartResult.getList();

                        DBHelper.getInstance(getApplicationContext()).saveShopLocal(dataShopCart);
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

    private void initView() {
        lvShopcart = (ListView) findViewById(R.id.shop_cart_detail_list);
        lvShopcart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dataShopCart.get(position).isSelect()) {//减操作
                    currentAmount -= Double.valueOf(dataShopCart.get(position).getGs().getPrice2()) *
                            Integer.valueOf(dataShopCart.get(position).getNum());
                } else {//加操作
                    currentAmount += Double.valueOf(dataShopCart.get(position).getGs().getPrice2()) *
                            Integer.valueOf(dataShopCart.get(position).getNum());
                }
                try {
                    updateAmount(currentAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataShopCart.get(position).setIsSelect(dataShopCart.get(position).isSelect() ? false : true);
                shopCartAdapter.notifyDataSetChanged();
            }
        });
        checkSelectAll = (CheckBox) findViewById(R.id.cb_shop_cart_select_all);
        checkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Double amount = 0.00;
                    for (int index = 0; index < dataShopCart.size(); index++) {
                        dataShopCart.get(index).setIsSelect(true);
                        amount += Integer.valueOf(dataShopCart.get(index).getNum()) *
                                Double.valueOf(dataShopCart.get(index).getGs().getPrice2());
                    }
                    try {
                        updateAmount(amount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int index = 0; index < dataShopCart.size(); index++) {
                        dataShopCart.get(index).setIsSelect(false);
                    }
                    try {
                        updateAmount(0.00);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                shopCartAdapter.notifyDataSetChanged();
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
                    intentData.setProDetail(dataShopCart);
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
        postEntity.setGid(dataShopCart.get(position).getSid());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        Gson gson = new Gson();
                        ResultEntity<Integer> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<Integer>>() {
                                }.getType());
                        if (resultEntity.isSuccess()) {

                            dataShopCart.get(position).setNum(resultEntity.getResult());
                            shopCartAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), resultEntity.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetController.getInstance(getActivity()).addToRequestQueue(getCategoryReq, TAG);
    }

    void updateAmount(Double amount) throws Exception {
        currentAmount = amount;
        SpannableString spannableString = new SpannableString("总价：￥" + currentAmount);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.home_page_general_red)),
                3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置颜色
        spannableString.setSpan(new AbsoluteSizeSpan(11, true), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置“￥”字体
        spannableString.setSpan(new AbsoluteSizeSpan(22, true), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置价格字体
        tvAmount.setText(spannableString);
    }

    public List<ShopCartModel> getDataShopCart() {
        return dataShopCart;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (TempDataManager.getInstance(getApplicationContext()).isLogin()) {
        try {
            reqShopcart();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    public void deleteShopCart() throws Exception {

        String url = NetInterface.HOST + NetInterface.METHON_SHOP_CART_PRO_DELETE_BY_IDS;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(Constants.POST_UID_TEST);
        postEntity.setIsLogin("1");
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
                        Gson gson = new Gson();
                        ResultEntity<ShopCartResult> resultEntity = gson.fromJson(jsonObject.toString(),
                                new TypeToken<ResultEntity<ShopCartResult>>() {
                                }.getType());
                        if (resultEntity.isSuccess()){
                            DebugFlags.logD(TAG, "成功");
                            //本地数据库删除
                            for (int index : indexCache) {
                                DBHelper.getInstance(getActivity()).deleteShopById(dataShopCart.get(index).getSid());
                                dataShopCart.remove(index);
                            }
                            indexCache.clear();//清空当前选择的内容
                            shopCartAdapter.notifyDataSetChanged();
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

        indexCache = new ArrayList<Integer>();
        String generateStr = "";
        StringBuilder sb = new StringBuilder(generateStr);

        List<ShopCartModel> selectedList = new ArrayList<ShopCartModel>();

        for (int index = 0; index < dataShopCart.size(); index++) {//筛选选择的购物车项目
            ShopCartModel shopCartItem = dataShopCart.get(index);
            if (shopCartItem.isSelect()) {
                indexCache.add(index);
                selectedList.add(shopCartItem);
            }
        }

        if (selectedList.size() == 0){
            return "";
        }

        for (int index = 0; index < selectedList.size(); index++) {//添加逗号分隔符
            ShopCartModel shopCartItem = selectedList.get(index);
            if (shopCartItem.isSelect()) {
                sb.append(selectedList.get(index).getSid());
            }
            if (index == selectedList.size() - 1) {
                break;
            }
            sb.append(",");
        }
        DebugFlags.logD(TAG, "生成的逗号字符串 ：" + sb.toString());
        return sb.toString();
    }
}
