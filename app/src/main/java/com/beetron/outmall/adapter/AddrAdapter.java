package com.beetron.outmall.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.beetron.outmall.AddrManager;
import com.beetron.outmall.R;
import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.ProCategory;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/24.
 * Time: 21:41.
 */
public class AddrAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private Context mContext;
    private List<AddrInfoModel> addrList ;

    public AddrAdapter(Context addrManager, List<AddrInfoModel> addrList) {

        this.mContext = addrManager;
        this.addrList = addrList;
        inflater = LayoutInflater.from(addrManager);
    }

    @Override
    public int getCount() {
        return addrList.size();
    }

    @Override
    public Object getItem(int position) {
        return addrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.addr_info_item_layout, null);
            viewHolder.tvAddrTitle = (TextView) convertView.findViewById(R.id.addr_info_item_title);
            viewHolder.tvAddrDetail = (TextView) convertView.findViewById(R.id.addr_info_item_detail);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        AddrInfoModel addrItem = addrList.get(position);
        String gender = "";
        if (addrItem.getSex().equals("1")) {
            gender = "男";
        } else {
            gender = "女";
        }
        viewHolder.tvAddrTitle.setText("收货人："+ addrItem.getName() +
                "(" + gender + ")|" + addrItem.getMobile());
        viewHolder.tvAddrDetail.setText("收货地址：" + addrItem.getAddress());
        return convertView;
    }

   class  ViewHolder{
       TextView tvAddrTitle, tvAddrDetail;
   }
}
