package com.beetron.outmall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.beetron.outmall.R;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.utils.CannelOrderListenner;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class OrderInfoAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, Object>> mlist;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> imgList;
    private int type;
    private CannelOrderListenner cannelOrderListenner;

    public OrderInfoAdapter(Context c, ArrayList<HashMap<String, Object>> list, int type, CannelOrderListenner listenner) {
        inflater = LayoutInflater.from(c);
        mlist = list;
        context = c;
        this.type = type;
        cannelOrderListenner = listenner;
    }

    public void upDate(ArrayList<HashMap<String, Object>> list, int type) {
        mlist = list;
        this.type = type;
    }

    @Override
    public int getCount() {
//        Log.d("OrderInfoAdapter","getCount:"+mlist.size());
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.order_info_item, null);
            viewHolder.tv_cation_pay = (TextView) convertView.findViewById(R.id.tv_cation_pay);
            viewHolder.tv_total_num = (TextView) convertView.findViewById(R.id.tv_total_num);
            viewHolder.btn_order = (Button) convertView.findViewById(R.id.btn_order);
            viewHolder.btn_cancel = (Button) convertView.findViewById(R.id.btn_cancel);
            viewHolder.scanTab = (ScrollIndicatorView) convertView.findViewById(R.id.about_me_order_scan_tab);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder = new ViewHolder();
//        convertView = inflater.inflate(R.layout.order_info_item, null);
//        viewHolder.tv_cation_pay = (TextView) convertView.findViewById(R.id.tv_cation_pay);
//        viewHolder.tv_total_num = (TextView) convertView.findViewById(R.id.tv_total_num);
//        viewHolder.btn_order = (Button) convertView.findViewById(R.id.btn_order);
//        viewHolder.btn_cancel = (Button) convertView.findViewById(R.id.btn_cancel);
//        viewHolder.scanTab = (ScrollIndicatorView) convertView.findViewById(R.id.about_me_order_scan_tab);

        HashMap<String, Object> map = mlist.get(position);
        final OrderInfoModel orderInfoModel = (OrderInfoModel) map.get("orderInfoModels");
        final OrderPostModel orderPostModel = (OrderPostModel) map.get("orderPostModels");
        final AddrInfoModel addrInfoModel = (AddrInfoModel) map.get("addrInfoModel");

        if (type == 1) {
            viewHolder.btn_cancel.setVisibility(View.VISIBLE);
            viewHolder.btn_order.setText("去付款");
            viewHolder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消订单
                    cannelOrderListenner.onCannel(orderPostModel.getId());
                }
            });
        } else {
            viewHolder.btn_order.setText("查看订单");
            viewHolder.btn_cancel.setVisibility(View.GONE);
        }

        viewHolder.btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    //去支付
//                    cannelOrderListenner.payOrder(orderPostModel.getId());
                    cannelOrderListenner.showDetail(orderInfoModel, orderPostModel, addrInfoModel);

                } else {
                    //查看订单详情
                    cannelOrderListenner.showDetail(orderInfoModel, orderPostModel, addrInfoModel);
                }
            }
        });
        viewHolder.tv_cation_pay.setText("实际付款：￥" + orderPostModel.getZongjia());
        viewHolder.tv_total_num.setText("共" + orderInfoModel.getProDetail().size() + "件商品");
        viewHolder.scanTab.setHorizontalFadingEdgeEnabled(true);
        viewHolder.scanTab.setSplitAuto(false);
        viewHolder.scanTab.setHorizontalScrollBarEnabled(false);
        viewHolder.scanTab.setAdapter(getAdapter(orderInfoModel.getProDetail()));


        return convertView;
    }

    class ViewHolder {
        TextView tv_total_num, tv_cation_pay;
        Button btn_order;
        Button btn_cancel;
        GridView gridView;
        ScrollIndicatorView scanTab;

    }

    class ChildHolder {
        TextView tv_num, tv_price;
        RoundedImageView ri_product;
    }

    private Indicator.IndicatorAdapter getAdapter(final List<ProSummary> proList) {
        return new Indicator.IndicatorAdapter() {

            @Override
            public int getCount() {
                return proList.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ChildHolder childHolder = null;
                if (convertView == null) {
                    childHolder = new ChildHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.img_gv_item, null);
                    //imgList.add(jsonArray.getJSONObject(position).getString("img"));
                    childHolder.ri_product = (RoundedImageView) convertView.findViewById(R.id.imageView2);
                    childHolder.tv_price = (TextView) convertView.findViewById(R.id.order_pro_scan_price);
                    childHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                    convertView.setTag(childHolder);
                } else {
                    childHolder = (ChildHolder) convertView.getTag();
                }

//                childHolder = new ChildHolder();
//                convertView = LayoutInflater.from(context).inflate(R.layout.img_gv_item, null);
//                //imgList.add(jsonArray.getJSONObject(position).getString("img"));
//                childHolder.ri_product = (RoundedImageView) convertView.findViewById(R.id.imageView2);
//                childHolder.tv_price = (TextView) convertView.findViewById(R.id.order_pro_scan_price);
//                childHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                ProSummary proSummary = proList.get(position);

                try {

                    childHolder.tv_num.setText(String.valueOf(proSummary.getCount()));
                    childHolder.tv_price.setText("￥：" + proSummary.getPrice2());
                    Log.d("OrderInfoAdapter", "图片URL：" + proSummary.getImg());
                    Glide.with(context).load(proSummary.getImg()).into(childHolder.ri_product);
                } catch (Exception e) {
                    Log.d("OrderInfoAdapter", "Error:" + e.getMessage());
                }
                return convertView;
            }
        };
    }

}
