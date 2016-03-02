package com.beetron.outmall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beetron.outmall.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class SexAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private Context mContext;
    private List<HashMap<String,String>> sexList ;

    public SexAdapter(Context addrManager, List<HashMap<String,String>> sexList) {

        this.mContext = addrManager;
        this.sexList = sexList;
        inflater = LayoutInflater.from(addrManager);
    }

    public void upData(List<HashMap<String,String>> sexList){
        this.sexList = sexList;
    }

    @Override
    public int getCount() {
        return sexList.size();
    }

    @Override
    public Object getItem(int position) {
        return sexList.get(position);
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
            convertView = inflater.inflate(R.layout.sex_item, null);
            viewHolder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
            viewHolder.iv_ico = (ImageView) convertView.findViewById(R.id.iv_ico);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        HashMap<String,String> map=sexList.get(position);
        String gender = "";
        if (map.get("name").equals("1")) {
            gender = "男";
        } else {
            gender = "女";
        }
        viewHolder.tv_sex.setText(gender);
        if (map.get("checked").equals("yes")) {
            viewHolder.iv_ico.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_ico.setVisibility(View.GONE);
        }
        return convertView;
    }

    class  ViewHolder{
        TextView tv_sex;
        ImageView iv_ico;
    }
}