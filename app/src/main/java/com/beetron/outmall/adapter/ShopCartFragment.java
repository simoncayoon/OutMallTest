package com.beetron.outmall.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.R;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;
import com.beetron.outmall.utils.SpanTextUtil;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/2.
 * Time: 15:16.
 */
public class ShopCartFragment extends BaseAdapter {

    private static final String TAG = ShopCartAdapter.class.getSimpleName();
    private Context mContext;
    private List<ProSummary> dataShopcart;
    private LayoutInflater inflater;

    private ProCountChange countChang = new ProCountChange() {

        @Override
        public void proChange(String flag, int position) throws Exception {

        }
    };

    private ProCountChange callback = countChang;

    public ShopCartFragment(Fragment context, List<ProSummary> dataShopcart) {
        mContext = context.getActivity();
        this.dataShopcart = dataShopcart;
        inflater = LayoutInflater.from(mContext);
        if (context instanceof ProCountChange) {
            callback = (ProCountChange) context;
        } else {
            DebugFlags.logD(TAG, "context must implement CountChange!");
        }
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
            viewHolder.itemCountAdd = (TextView) convertView.findViewById(R.id.shop_cart_item_add);
            viewHolder.itemCountMinus = (TextView) convertView.findViewById(R.id.shop_cart_item_minus);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        ProSummary shopcartItem = dataShopcart.get(position);
        viewHolder.checkSelect.setChecked(shopcartItem.getIsSelect());
        viewHolder.itemTitle.setText(shopcartItem.getTitle());
        viewHolder.itemSummary.setText(shopcartItem.getJianshu());
        viewHolder.itemSalsePrice.setText(SpanTextUtil.setSpanSize(0, 1, "￥" + shopcartItem.getPrice2(), 10));
        viewHolder.itemPrimaryPrice.setText("￥" + shopcartItem.getPrice1());
        viewHolder.itemPrimaryPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.itemVolume.setText(String.valueOf(shopcartItem.getCount()));
        viewHolder.imgView.setImageUrl(shopcartItem.getImg(), NetController.getInstance(mContext).getImageLoader());
        viewHolder.itemCountAdd.setOnClickListener(new OnProCountChangeListener(ProCountChange.FLAG_ADD, position));
        viewHolder.itemCountMinus.setOnClickListener(new OnProCountChangeListener(ProCountChange.FLAG_MINUS, position));
        return convertView;
    }

    public interface ProCountChange {

        public static final String FLAG_ADD = "FLAG_ADD";
        public static final String FLAG_MINUS = "FLAG_MINUS";

        public void proChange(String flag, int position) throws Exception;
    }

    class ViewHolder {
        CheckBox checkSelect;
        NetworkImageView imgView;
        TextView itemTitle, itemSummary, itemSalsePrice, itemPrimaryPrice, itemVolume, itemCountAdd, itemCountMinus;
    }

    class OnProCountChangeListener implements View.OnClickListener {

        private String flag;
        private int position;

        public OnProCountChangeListener(String flag, int position) {
            this.flag = flag;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            try {
                callback.proChange(flag, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
