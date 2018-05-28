package com.eds.ma.bis.order.service;

import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.vo.OrderDetailVo;
import com.eds.ma.bis.user.entity.User;
import com.xcrm.common.page.Pagination;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单service
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public interface IOrderService {

    /**
     * 查询订单信息
     * @param orderId
     */
    Order queryOrderById(Long orderId);

    /**
     * 保存订单信息
     * @param order
     */
    void saveOrder(Order order);

    /**
     * 保存支付订单信息
     * @param payOrder
     */
    void savePayOrder(PayOrder payOrder);

    /**
     * 查询支付订单信息
     * @param payCode
     * @return
     */
    PayOrder queryPayOrderByPayCode(String payCode);

    /**
     * 查询待退款支付订单列表
     * @param openId
     * @return
     */
    List<PayOrder> queryToRefundPayOrder(String openId);

    /**
     * 更新微信支付回调
     * @param payCode
     * @param nonce
     * @param tradeNo
     * @param buyerId
     * @param payMoney
     *@param totalFee
     * @param cashFee
     * @param timeEndString
     * @param bankType
     * @param sellerId
     * @param appId
     * @param isSubscribed
     */
    int updatePayOrderForCallBack(String payCode, String nonce, String tradeNo, String buyerId, BigDecimal payMoney, BigDecimal totalFee, BigDecimal cashFee, String timeEndString, String bankType, String sellerId, String appId, String isSubscribed);

    /**
     * 保存交易记录
     * @param financeIncome
     */
    void saveFinanceIncome(FinanceIncome financeIncome);

    /**
     * 查询订单列表
     * @param orderStatus
     * @param user
     * @param pageNo
     * @param pageSize
     * @return
     */
    Pagination queryOrders(String orderStatus, User user, Integer pageNo, Integer pageSize);

    /**
     * 查询订单详情
     *
     * @param user
     * @param orderId
     * @return
     */
    OrderDetailVo queryOrderDetail(User user, Long orderId);

    /**
     * 查询最新的使用中的订单id
     * @param user
     * @return
     */
    Long queryLatestOrderId(User user);

    /**
     * 计算租金金额
     * @param rentTime
     * @return
     */
    BigDecimal caculateRentFee(Date rentTime);

    /**
     * 查询用户的交易流水
     * @param userId
     * @param pageNo
     * @param pageSize
     */
    Pagination queryTrnasFinanceIncome(Long userId, Integer pageNo, Integer pageSize);
}
