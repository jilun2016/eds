package com.eds.ma.bis.order.service;

import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.vo.OrderDetailVo;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.PayStatusEnum;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.common.page.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    protected Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public Order queryOrderById(Long orderId) {
        return dao.queryById(orderId, Order.class);
    }

    @Override
    public void saveOrder(Order order) {
        dao.save(order);
    }

    @Override
    public void savePayOrder(PayOrder payOrder) {
        dao.save(payOrder);
    }

    @Override
    public PayOrder queryPayOrderByPayCode(String payCode) {
        Ssqb query = Ssqb.create("com.eds.wx.pay.queryPayOrderWithLock")
                .setParam("payCode", payCode)
                .setParam("dataStatus", 1);
        return dao.findForObj(query, PayOrder.class);
    }

    @Override
    public List<PayOrder> queryToRefundPayOrder(String openId) {
        Ssqb query = Ssqb.create("com.eds.wx.pay.queryToRefundPayOrder")
                .setParam("openId", openId);
        return dao.findForList(query, PayOrder.class);
    }

    @Override
    public int updatePayOrderForCallBack(String payCode, String nonce, String tradeNo, String buyerId,
                                         BigDecimal payMoney, BigDecimal totalFee, BigDecimal cashFee, String payTime, String bankType, String sellerId, String appId, String isSubscribed) {

        if (payMoney.compareTo(totalFee) == 0) {
            Ssqb updateQuery = Ssqb.create("com.eds.wx.pay.updatePayOrderForCallBack")
                    .setParam("appId", appId)
                    .setParam("bankType", bankType)
                    .setParam("buyerId", buyerId)
                    .setParam("cashFee", cashFee)
                    .setParam("totalFee", totalFee)
                    .setParam("isSubscribe", isSubscribed)
                    .setParam("notifyId", nonce)
                    .setParam("payStatus", PayStatusEnum.TRADE_SUCCESS.value())
                    .setParam("payTime", payTime)
                    .setParam("sellerId", sellerId)
                    .setParam("tradeNo", tradeNo)
                    .setParam("payCode", payCode);
            return dao.updateByMybatis(updateQuery);
        }
        return 0;
    }

    @Override
    public void saveFinanceIncome(FinanceIncome financeIncome) {
        dao.save(financeIncome);
    }

    @Override
    public Pagination queryOrders(String orderStatus, User user, Integer pageNo, Integer pageSize) {
        //通过openId查询userId
        Ssqb queryOrderListSqb = Ssqb.create("com.eds.order.queryOrders")
                .setParam("orderStatus", orderStatus)
                .setParam("userId", user.getId())
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);queryOrderListSqb.setIncludeTotalCount(true);
        return dao.findForPage(queryOrderListSqb);
    }

    @Override
    public OrderDetailVo queryOrderDetail(User user, Long orderId) {
        //通过openId查询userId
        Ssqb queryOrderDetailSqb = Ssqb.create("com.eds.order.queryOrderDetail")
                .setParam("userId", user.getId())
                .setParam("orderId", orderId);
        return dao.findForObj(queryOrderDetailSqb,OrderDetailVo.class);
    }

    @Override
    public Long queryLatestOrderId(User user) {
        //通过openId查询userId
        Ssqb queryOrderIdSqb = Ssqb.create("com.eds.order.queryLatestOrderId")
                .setParam("userId", user.getId());
        return dao.findForObj(queryOrderIdSqb,Long.class);
    }
}
