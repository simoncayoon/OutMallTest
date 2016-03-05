package com.beetron.outmall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.LoginActivity;
import com.beetron.outmall.R;
import com.beetron.outmall.constant.Constants;
import com.beetron.outmall.constant.NetInterface;
import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.PostEntity;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.TempDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/15.
 * Time: 13:35.
 */
public class ProSummaryAdapter extends BaseAdapter {

    private static final String TAG = ProSummaryAdapter.class.getSimpleName();
    private Context mContext;
    private List<ProSummary> proSummaryList;
    private LayoutInflater inflater;

    private ShopCartCountListener callBack = new ShopCartCountListener() {
        @Override
        public void notifyCountChange() {

        }
    };

    private ShopCartCountListener countListener = callBack;

    public ProSummaryAdapter(Context context, List<ProSummary> proList) {

        mContext = context;
        proSummaryList = proList;
        inflater = LayoutInflater.from(mContext);
        if (context instanceof ShopCartCountListener) {
            countListener = (ShopCartCountListener) context;
        } else {
            DebugFlags.logD(TAG, "请实现购物车监听接口！！");
        }
    }

    @Override
    public int getCount() {
        return proSummaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return proSummaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pro_summary_item_layout, null);
            viewHolder.proImg = (NetworkImageView) convertView.findViewById(R.id.niv_pro_summary_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_pro_summary_title);
            viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_pro_summary_content);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tv_pro_price_mall);
            viewHolder.shopCart = (ViewWithBadge) convertView.findViewById(R.id.tv_shop_cart_add);
            viewHolder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.discount = (TextView) convertView.findViewById(R.id.tv_pro_price_discount);
            viewHolder.saleVolume = (TextView) convertView.findViewById(R.id.tv_pro_sales_volume);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        try {
            int initCount = DBHelper.getInstance(mContext).
                    getShopCartCounById(DBHelper.FLAG_PROSUMMARY_BY_SID, proSummaryList.get(position).getSid());
            proSummaryList.get(position).setCount(initCount);//获取商品在购物车里的数量
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProSummary proItem = proSummaryList.get(position);
        viewHolder.proImg.setImageUrl(proItem.getImg(), NetController.getInstance(mContext).getImageLoader());
        viewHolder.title.setText(proItem.getTitle());
        viewHolder.summary.setText(proItem.getJianshu());
        viewHolder.discount.setText(String.valueOf(proItem.getPrice2()));
        viewHolder.shopCart.setOnClickListener(new AddShopCart(position));
        try {
            viewHolder.shopCart.setBadge(BadgeView.POSITION_TOP_RIGHT, proItem.getCount()
                    , 6, 0);
            if (proItem.getCount() > 0){
                viewHolder.shopCart.setBackgroundResource(R.mipmap.list_ic_shopping_red);

            }else {
                viewHolder.shopCart.setBackgroundResource(R.mipmap.list_ic_shopping);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.price.setText("￥" + proItem.getPrice1());
        viewHolder.saleVolume.setText("已销售" + proItem.getXl() + "笔");
        return convertView;
    }

    void addShopCart(final int position) throws Exception {
        String url = NetInterface.HOST + NetInterface.METHON_ADD_SHOPCART_BY_ID;
        PostEntity postEntity = new PostEntity();
        postEntity.setToken(Constants.TOKEN_VALUE);
        postEntity.setUid(TempDataManager.getInstance(mContext.getApplicationContext()).getCurrentUid());
        postEntity.setIsLogin(TempDataManager.getInstance(mContext.getApplicationContext()).getLoginState());
        postEntity.setGid(proSummaryList.get(position).getSid());
        String postString = new Gson().toJson(postEntity, new TypeToken<PostEntity>() {
        }.getType());
        JSONObject postJson = new JSONObject(postString);
        JsonObjectRequest getCategoryReq = new JsonObjectRequest(Request.Method.POST, url, postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        DebugFlags.logD(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.getString(Constants.RESULT_STATUS_FIELD).equals(Constants.RESULT_SUCCEED_STATUS)){//返回成功
                                try {
                                    ProSummary clickItem = proSummaryList.get(position);
                                    JSONObject countJSON = jsonObject.getJSONObject(Constants.RESULT_CONTENT_FIELD);
                                    clickItem.setCount(countJSON.getInt("count"));
                                    if (DBHelper.getInstance(mContext).addShopCart(clickItem) != -1L) {
                                        //通知更新数据
                                        countListener.notifyCountChange();
                                    } else {
                                        DebugFlags.logD(TAG, "添加购物车数据库失败！");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString(Constants.RESULT_ERROR_FIELD).toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

            }
        });
        NetController.getInstance(mContext).addToRequestQueue(getCategoryReq, TAG);
    }

    public interface ShopCartCountListener {
        public void notifyCountChange() throws Exception;
    }

    class ViewHolder {
        NetworkImageView proImg;
        TextView title, summary, discount, price, saleVolume;
        ViewWithBadge shopCart;
    }

    class AddShopCart implements View.OnClickListener {

        int position;

        public AddShopCart(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            if (TempDataManager.getInstance(mContext.getApplicationContext()).isLogin()) {

                try {
                    addShopCart(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(LoginActivity.FLAG_NAVI_ROOT, mContext.getResources().getString(R.string.framework_navi_home_page));
                mContext.startActivity(intent);
            }
        }
    }
}
