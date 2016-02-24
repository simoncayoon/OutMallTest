package com.beetron.outmall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.R;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.utils.NetController;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/22.
 * Time: 11:22.
 */
public class ShopCartAdapter extends BaseAdapter {

    private Context mContext;
    private List<ShopCartModel> dataShopcart;
    private LayoutInflater inflater;

    public ShopCartAdapter(Context context, List<ShopCartModel> dataShopcart) {
        mContext = context;
        this.dataShopcart = dataShopcart;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataShopcart.size();
    }

    @Override
    public Object getItem(int position) {
        return dataShopcart.get(position);
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
            convertView = inflater.inflate(R.layout.shop_cart_detail_layout, null);
            viewHolder.checkSelect = (CheckBox) convertView.findViewById(R.id.shop_cart_check_select);
            viewHolder.imgView = (NetworkImageView) convertView.findViewById(R.id.iv_shop_cart_item_img);
            viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.shop_cart_item_title);
            viewHolder.itemSummary = (TextView) convertView.findViewById(R.id.shop_cart_item_summary);
            viewHolder.itemSalsePrice = (TextView) convertView.findViewById(R.id.shop_cart_item_sales_price);
            viewHolder.itemPrimaryPrice = (TextView) convertView.findViewById(R.id.shop_cart_item_primary_price);
            viewHolder.itemVolume = (TextView) convertView.findViewById(R.id.shop_cart_item_volume);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        ShopCartModel shopcartItem = dataShopcart.get(position);
        viewHolder.itemTitle.setText(shopcartItem.getGs().getTitle());
        viewHolder.itemSummary.setText(shopcartItem.getGs().getJianshu());
        viewHolder.itemSalsePrice.setText(shopcartItem.getGs().getPrice2());
        viewHolder.itemPrimaryPrice.setText(shopcartItem.getGs().getPrice1());
        viewHolder.itemVolume.setText(shopcartItem.getNum());
        viewHolder.imgView.setImageUrl(shopcartItem.getGs().getImg(), NetController.getInstance(mContext).getImageLoader());
        return convertView;
    }

    class ViewHolder {
        CheckBox checkSelect;
        NetworkImageView imgView;
        TextView itemTitle, itemSummary, itemSalsePrice, itemPrimaryPrice, itemVolume;
    }
}
