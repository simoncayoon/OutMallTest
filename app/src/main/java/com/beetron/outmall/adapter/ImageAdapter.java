package com.beetron.outmall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.beetron.outmall.utils.NetController;

import java.util.ArrayList;

/**
 * Created by luomaozhong on 16/3/1.
 */
public class ImageAdapter extends BaseAdapter {
    private ArrayList<String> imageIDs=new ArrayList<>();
    private Context context;

    public ImageAdapter(Context c, ArrayList<String> imageIDs) {
        context=c;
        this.imageIDs = imageIDs;
    }

    public int getCount() {
        Log.d("ImageAdapter","imageIDs.size:"+imageIDs.size());
        return imageIDs.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkImageView imageView;
        if (convertView == null) {
            imageView = new NetworkImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (NetworkImageView) convertView;
        }
        //imageView.setImageResource(imageIDs[position]);
        imageView.setImageUrl(imageIDs.get(position), NetController.getInstance(context).getImageLoader());
        return imageView;
    }
}
