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
     * 限时购接口
     */
    public static final String METHON_GET_PRO_BY_CATEGORY_LIMIT = "?m=home&c=app&a=gxgood_list";

    /**
     * 获取商品详细信息
     */
    public static final String METHON_GET_PRO_BY_SID = "?m=home&c=app&a=goods";

    /**
     * 获取注册验证码
     */
    public static final String METHON_VERIFY_CODE_REGIST = "?m=home&c=app&a=reg_code";

    /**
     * 获取注册码通用
     */
    public static final String METHON_VERIFY_CODE_REGIST_GEN = "?m=home&c=app&a=code";

    /**
     * 登录
     */
    public static final String METHON_LOGIN = "?m=home&c=app&a=login_do";

    /**
     * 快速登录
     */
    public static final String METHON_LOGIN_FAST = "?m=home&c=app&a=tel_login";

    /**
     * 注册
     */
    public static final String METHON_REGIST = "?m=home&c=app&a=user_reg";

    /**
     * 密码找回
     */
    public static final String METHON_TAKE_PWD_BACK = "?m=home&c=app&a=find_pass";

    /**
     * 添加购物车
     */
    public static final String METHON_ADD_SHOPCART_BY_ID = "?m=home&c=app&a=add_to_car";

    /**
     * 获取购物车信息
     */
    public static final String METHON_GET_SHOPCART = "?m=home&c=app&a=cart";

    /**
     * 购物车加
     */
    public static final String METHON_ADD_SHOPCART = "?m=home&c=app&a=cart_jia";

    /**
     * 购物车减
     */
    public static final String METHON_MINUS_SHOPCART = "?m=home&c=app&a=cart_jian";

    /**
     * 获取地址列表
     */
    public static final String METHON_GET_ADDR_LIST = "?m=home&c=app&a=address_list";

    /**
     * 新增用户地址
     */
    public static final String METHON_ADDR_ADD = "?m=home&c=app&a=address_add";

    /**
     * 用户地址更新
     */
    public static final String METHON_ADDR_UPDATE = "?m=home&c=app&a=address_edit_do";

    /**
     * 删除地址
     */
    public static final String METHON_ADDR_DELETE = "?m=home&c=app&a=address_del";

    /**
     * 订单填写条件获取
     */
    public static final String METHON_ORDER_FIX = "?m=home&c=app&a=order_show";

    /**
     * 提交订单
     */
    public static final String METHON_ORDER_COMMIT = "?m=home&c=app&a=order_save";

    /**
     * 取消订单
     */
    public static final String METHON_ORDER_CANCEL = "?m=home&c=app&a=order_cancel";

    /**
     * 获取用户信息
     */
    public static final String METHON_GET_USER_INFO = "?m=home&c=app&a=uer_info";

    /**
     * 删除内容
     */
    public static final String METHON_SHOP_CART_PRO_DELETE_BY_IDS = "?m=home&c=app&a=clear_chose_cart";
    /**
     * 订单信息
     */
    public static final String METHON_ORDER_INFO = "?m=home&c=app&a=uer_order";
    /**
     * 修改用户信息
     */
    public static final String METHON_EDIT_USER_INFO = "?m=home&c=app&a=user_edit";
    /**
     * 签到
     */
    public static final String METHON_SIGN_INFO = "?m=home&c=app&a=qiandao";
    /**
     * 微信支付接口
     */
    public static final String METHON_WEIXIN_PAY = "?m=home&c=app&a=weixin_pay";
    /**
     * 微信支付成功通知接口
     */
    public static final String METHON_WEIXIN_PAY_NOTICE = "?m=home&c=app&a=update_order";

    /**
     * 升级接口
     */
    public static final String METHON_UPDATE_CHECK = "?m=home&c=app&a=update";

    /**
     * 积分详情
     */
    public static final String METHON_GET_SCORE_DETAIL = "?m=home&c=app&a=qiandao_detail";
}
