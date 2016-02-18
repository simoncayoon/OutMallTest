package com.beetron.outmall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beetron.outmall.customview.BadgeView;
import com.beetron.outmall.customview.ViewWithBadge;
import com.beetron.outmall.models.ProCategory;
import com.beetron.outmall.R;
import com.beetron.outmall.utils.DBHelper;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/3.
 * Time: 11:48.
 */
public class CategoryMenuAdapter extends BaseAdapter {


    private final Context context;
    private final List<ProCategory> menuData;
    private LayoutInflater layoutInflater;


    public CategoryMenuAdapter(Context context, List<ProCategory> menuData) {
        this.context = context;
        this.menuData = menuData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuData.size();
    }

    @Override
    public Object getItem(int position) {
        return menuData.get(position);
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
            convertView = layoutInflater.inflate(R.layout.home_page_menu_item_layout, null);
            viewHolder.menuTitle = (ViewWithBadge) convertView.findViewById(R.id.tv_home_page_menu);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        ProCategory menuItem = menuData.get(position);
        viewHolder.menuTitle.setText(menuItem.getName());
        viewHolder.menuTitle.setSelected(menuItem.isSelected());
        try {
            viewHolder.menuTitle.setBadge(BadgeView.POSITION_TOP_LEFT,
                    DBHelper.getInstance(context).getShopCartCounById(DBHelper.FLAG_PROSUMMARY_BY_FID, menuItem.getId()), 6, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder{
        private ViewWithBadge menuTitle;
    }
}
