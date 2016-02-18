package com.beetron.outmall.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.ProSummary;
import com.beetron.outmall.R;
import com.beetron.outmall.utils.DBHelper;
import com.beetron.outmall.utils.DebugFlags;
import com.beetron.outmall.utils.NetController;

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
        public void notifyCountChange(String fid) {

        }
    };

    private ShopCartCountListener countListener = callBack;

    public ProSummaryAdapter(Context context, List<ProSummary> proList) {

        mContext = context;
        proSummaryList = proList;
        inflater = LayoutInflater.from(mContext);
        if (context instanceof ShopCartCountListener) {
            countListener = (ShopCartCountListener)context;
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
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pro_summary_item_layout, null);
            viewHolder.proImg = (NetworkImageView) convertView.findViewById(R.id.niv_pro_summary_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_pro_summary_title);
            viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_pro_summary_content);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tv_pro_price_mall);
            viewHolder.shopCart = (ViewWithBadge) convertView.findViewById(R.id.tv_shop_cart_add);
            viewHolder.price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
            viewHolder.discount = (TextView) convertView.findViewById(R.id.tv_pro_price_discount);
            viewHolder.saleVolume = (TextView) convertView.findViewById(R.id.tv_pro_sales_volume);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        ProSummary proItem = proSummaryList.get(position);
        viewHolder.proImg.setImageUrl(proItem.getImg(), NetController.getInstance(mContext).getImageLoader());
        viewHolder.title.setText(proItem.getTitle());
        viewHolder.summary.setText(proItem.getJianshu());
        viewHolder.discount.setText(String.valueOf(proItem.getPrice2()));
        viewHolder.shopCart.setOnClickListener(new AddShopCart(position));
        try {
            viewHolder.shopCart.setBadge(BadgeView.POSITION_TOP_RIGHT,
                    DBHelper.getInstance(mContext).getShopCartCounById(DBHelper.FLAG_PROSUMMARY_BY_SID, proItem.getSid()), 6, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.price.setText( "￥" + proItem.getPrice1());
        viewHolder.saleVolume.setText("已销售" + proItem.getXl() + "笔");
        return convertView;
    }

    public interface ShopCartCountListener {
        public void notifyCountChange(String fid);
    }

    class ViewHolder {
        NetworkImageView proImg;
        TextView title, summary, discount, price, saleVolume;
        ViewWithBadge shopCart;
    }

    class AddShopCart implements View.OnClickListener{

        int position ;

        public AddShopCart(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {

            ProSummary clickItem = proSummaryList.get(position);
            if (DBHelper.getInstance(mContext).addShopCart(clickItem) != -1L){
                clickItem.setCount(clickItem.getCount() + 1);
                notifyDataSetChanged();
                //通知更新数据
                countListener.notifyCountChange(clickItem.getFid());
            }else{
                DebugFlags.logD(TAG, "添加购物车数据库失败！");
            }
        }
    }
}
