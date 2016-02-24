package com.beetron.outmall.constant;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/1/24.
 * Time: 09:53.
 */
public class NetInterface {

    /**
     * 接口地址
     */
    public static final String HOST = "http://chulai-mai.com/index.php";

    /**
     * 获取产品分类
     */
    public static final String METHON_GET_PRO_CATEGORY = "?m=Home&c=App&a=fenlei";

    /**
     * 获取对应分类的产品列表
     */
    public static final String METHON_GET_PRO_ABOUT_CATEGORY = "/Home/App/good_list";

    /**
     * 获取产品分类
     */
    public static final String METHON_GET_PRO_DETAIL = "?m=home&c=app&a=goods";

    /**
     * 获取产品其他图片
     */
    public static final String METHON_GET_PRO_ALBUM = "?m=home&c=app&a=goods_pic";

    /**
     * 加入购物车
     */
    public static final String METHON_ADD_SHOP_CART = "?m=home&c=app&a=add_to_car";

    /**
     * 获取幻灯片列表
     */
    public static final String METHON_GET_IMG_SCAN = "?m=home&c=app&a=piclist";

    /**
     * 获取对应分类下来的商品列表
     */
    public static final String METHON_GET_PRO_BY_CATEGORY = "?m=Home&c=App&a=good_list";

    /**
     * 获取商品详细信息
     */
    public static final String METHON_GET_PRO_BY_SID = "?m=home&c=app&a=goods";

    /**
     * 获取注册验证码
     */
    public static final String METHON_VERIFY_CODE_REGIST = "?m=home&c=app&a=reg_code";

    /**
     * 登录
     */
    public static final String METHON_LOGIN = "?m=home&c=app&a=login_do";

    /**
     * 注册
     */
    public static final String METHON_REGIST = "?m=home&c=app&a=user_reg";

    /**
     * 添加购物车
     */
    public static final String METHON_ADD_SHOPCART_BY_ID = "?m=home&c=app&a=add_to_car";


    public static final String METHON_GET_SHOPCART = "?m=home&c=app&a=cart";
}
