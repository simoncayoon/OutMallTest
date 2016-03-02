package com.beetron.outmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.beetron.outmall.adapter.ShopCartAdapter;
import com.beetron.outmall.customview.CusNaviView;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.ShopCartModel;
import com.beetron.outmall.models.ShopCartResult;

import java.util.List;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/3/1.
 * Time: 14:27.
 */
public class ShopCartActivity extends Activity {

    private CusNaviView navigationView;
    private ListView lvShopcart;
    private CheckBox checkSelectAll;
    private TextView tvAmount;
    private Button btnAccount;
    private List<ShopCartModel> dataShopCart;
    private List<Integer> indexCache;

    private ShopCartResult shopcartResult;
    private ShopCartAdapter shopCartAdapter;
    private Double currentAmount = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_cart_activity_layout);

        initView();
    }

    private void initView() {
        initNavi();

        lvShopcart = (ListView) findViewById(R.id.shop_cart_detail_list);
        lvShopcart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dataShopCart.get(position).isSelect()) {//减操作
                    currentAmount -= Double.valueOf(dataShopCart.get(position).getGs().getPrice2()) *
                            Integer.valueOf(dataShopCart.get(position).getNum());
                } else {//加操作
                    currentAmount += Double.valueOf(dataShopCart.get(position).getGs().getPrice2()) *
                            Integer.valueOf(dataShopCart.get(position).getNum());
                }
                try {
                    updateAmount(currentAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataShopCart.get(position).setIsSelect(dataShopCart.get(position).isSelect() ? false : true);
                shopCartAdapter.notifyDataSetChanged();
            }
        });
        checkSelectAll = (CheckBox) findViewById(R.id.cb_shop_cart_select_all);
        checkSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Double amount = 0.00;
                    for (int index = 0; index < dataShopCart.size(); index++) {
                        dataShopCart.get(index).setIsSelect(true);
                        amount += Integer.valueOf(dataShopCart.get(index).getNum()) *
                                Double.valueOf(dataShopCart.get(index).getGs().getPrice2());
                    }
                    try {
                        updateAmount(amount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int index = 0; index < dataShopCart.size(); index++) {
                        dataShopCart.get(index).setIsSelect(false);
                    }
                    try {
                        updateAmount(0.00);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                shopCartAdapter.notifyDataSetChanged();
            }
        });
        tvAmount = (TextView) findViewById(R.id.tv_shop_cart_amount);
        btnAccount = (Button) findViewById(R.id.btn_to_account_shop_cart);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShopCartActivity.this, OrderFixActivity.class);
                try {
                    OrderInfoModel intentData = OrderInfoModel.getInstance();
//                    intentData.setProDetail(dataShopCart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    private void initNavi() {
        navigationView = (CusNaviView) findViewById(R.id.general_navi_id);
        navigationView.setNaviTitle(getResources().getString(R.string.product_detail));
        navigationView.setBtn(CusNaviView.PUT_BACK_ENABLE, CusNaviView.NAVI_WRAP_CONTENT, 56);
        ((Button) navigationView.getLeftBtn()).setText(getResources().getString(R.string.framework_navi_home_page));//设置返回标题

        navigationView.setBtn(CusNaviView.PUT_RIGHT, 23, 23);
        navigationView.getRightBtn().setBackgroundResource(R.mipmap.nav_ic_delete);
        navigationView.setGravity(Gravity.CENTER);
        navigationView.setNaviBtnListener(new CusNaviView.NaviBtnListener() {
            @Override
            public void leftBtnListener() {
                finish();
            }

            @Override
            public void rightBtnListener() {
                //去购物车
                startActivity(new Intent(ShopCartActivity.this, ShopCartActivity.class));
            }
        });
    }

    void updateAmount(Double amount) throws Exception {
        currentAmount = amount;
        SpannableString spannableString = new SpannableString("总价：￥" + currentAmount);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.home_page_general_red)),
                3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置颜色
        spannableString.setSpan(new AbsoluteSizeSpan(11, true), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置“￥”字体
        spannableString.setSpan(new AbsoluteSizeSpan(22, true), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//设置价格字体
        tvAmount.setText(spannableString);
    }
}
