package com.beetron.outmall.utils;

import com.beetron.outmall.models.AddrInfoModel;
import com.beetron.outmall.models.OrderInfoModel;
import com.beetron.outmall.models.OrderPostModel;

/**
 * Created by luomaozhong on 16/3/5.
 */
public interface CannelOrderListenner {
    /***
     * 取消订单
     * @param orderID
     */
    public void onCannel(String orderID);

    /***
     * 查看详细订单
     * @param orderInfoModel
     * @param orderPostModel
     */
    public void showDetail(OrderInfoModel orderInfoModel, OrderPostModel orderPostModel, AddrInfoModel addrInfoModel);

    /**
     * 去支付
     * @return
     */
    public String payOrder(String orderID);
}
