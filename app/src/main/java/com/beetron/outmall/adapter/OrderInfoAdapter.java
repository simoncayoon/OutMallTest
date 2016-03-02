package com.beetron.outmall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beetron.outmall.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class OrderInfoAdapter extends BaseAdapter {

    private ArrayList<JSONObject> mlist;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> imgList;

    public OrderInfoAdapter(Context c, ArrayList<JSONObject> list) {
        inflater = LayoutInflater.from(c);
        mlist = list;
        context = c;
    }

    public void upDate(ArrayList<JSONObject> list) {
        mlist = list;
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
//            viewHolder.gridView = (GridView) convertView.findViewById(R.id.gridView);
            viewHolder.btn_order = (Button) convertView.findViewById(R.id.btn_order);
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder = new ViewHolder();
//        convertView = inflater.inflate(R.layout.order_info_item, null);
//        viewHolder.tv_cation_pay = (TextView) convertView.findViewById(R.id.tv_cation_pay);
//        viewHolder.tv_total_num = (TextView) convertView.findViewById(R.id.tv_total_num);
////            viewHolder.gridView = (GridView) convertView.findViewById(R.id.gridView);
//        viewHolder.btn_order = (Button) convertView.findViewById(R.id.btn_order);
//        viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);

        JSONObject json = mlist.get(position);
        Log.d("OrderInfoAdapter", "json:" + json.toString());
        try {
            viewHolder.layout.removeAllViews();
            viewHolder.tv_cation_pay.setText("实际付款：￥" + json.getString("payment"));
            JSONArray jsonArray = json.getJSONArray("gs");
            viewHolder.tv_total_num.setText("共" + jsonArray.length() + "件商品");
            imgList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                View mView = LayoutInflater.from(context).inflate(R.layout.img_gv_item, null);
                //imgList.add(jsonArray.getJSONObject(position).getString("img"));
                Log.d("OrderInfoAdapter", "url:" + jsonArray.getJSONObject(i).getString("img"));
                ImageView imageView = (ImageView) mView.findViewById(R.id.imageView2);
                //imageView.setImageUrl(jsonArray.getJSONObject(i).getString("img"), NetController.getInstance(context).getImageLoader());
                Picasso.with(context)
                        .load(jsonArray.getJSONObject(i).getString("img"))
                        //.placeholder(R.mipmap.address_ic_arrow)
//                        .error(R.drawable.user_placeholder_error)
                        .into(imageView);
                viewHolder.layout.addView(mView);
            }


//            viewHolder.gridView.setAdapter(new ImageAdapter(context, imgList));
//            viewHolder.gridView.setNumColumns(imgList.size());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_total_num, tv_cation_pay;
        Button btn_order;
        GridView gridView;
        LinearLayout layout;
    }

}
