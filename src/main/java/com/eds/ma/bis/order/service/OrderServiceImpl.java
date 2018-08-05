package com.eds.ma.bis.order.service;

import com.eds.ma.bis.device.OrderStatusEnum;
import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.vo.OrderDetailVo;
import com.eds.ma.bis.wx.PayStatusEnum;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public List<PayOrder> queryToRefundPayOrder(String openId, String aliUid) {
        Ssqb query = Ssqb.create("com.eds.wx.pay.queryToRefundPayOrder")
                .setParam("openId", openId)
                .setParam("aliUid", aliUid);
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
    public Pagination queryOrders(String orderStatus, Long userId, Integer pageNo, Integer pageSize) {
        //通过openId查询userId
        Ssqb queryOrderListSqb = Ssqb.create("com.eds.order.queryOrders")
                .setParam("orderStatus", orderStatus)
                .setParam("userId", userId)
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);
        queryOrderListSqb.setIncludeTotalCount(true);
        return dao.findForPage(queryOrderListSqb);
    }

    @Override
    public OrderDetailVo queryOrderDetail(Long userId, Long orderId) {
        //通过openId查询userId
        Ssqb queryOrderDetailSqb = Ssqb.create("com.eds.order.queryOrderDetail")
                .setParam("userId", userId)
                .setParam("orderId", orderId);
        OrderDetailVo orderDetailVo = dao.findForObj(queryOrderDetailSqb,OrderDetailVo.class);
        if(Objects.nonNull(orderDetailVo) && Objects.equals(orderDetailVo.getOrderStatus(),OrderStatusEnum.S_DDZT_JXZ.value())){
            BigDecimal rentMoney =  caculateRentFee(orderDetailVo.getRentTime());
            orderDetailVo.setTotalFee(rentMoney);
        }
        return orderDetailVo;
    }

    @Override
    public Long queryLatestOrderId(Long userId) {
        //通过openId查询userId
        Ssqb queryOrderIdSqb = Ssqb.create("com.eds.order.queryLatestOrderId")
                .setParam("userId", userId);
        return dao.findForObj(queryOrderIdSqb,Long.class);
    }

    @Override
    public BigDecimal caculateRentFee(Date rentTime){
        //4小时内收费58元,每1小时累加10元
        Date now = DateFormatUtils.getNow();
        BigDecimal baseRentFee = BigDecimal.valueOf(58);
        long interval=(now.getTime()-rentTime.getTime())/1000;//秒
        long diffHour=interval%(24*3600)/3600;//小时
        if(diffHour <= 4){
            return BigDecimal.valueOf(58);
        }else{
            BigDecimal stepMoney = BigDecimal.valueOf((diffHour -4)*10);
            return baseRentFee.add(stepMoney);
        }

    }

    @Override
    public Pagination queryTrnasFinanceIncome(Long userId, Integer pageNo, Integer pageSize) {
        //通过openId查询userId
        Ssqb queryFinanceIncomeSqb = Ssqb.create("com.eds.order.queryTrnasFinanceIncome")
                .setParam("userId", userId)
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);
        return dao.findForPage(queryFinanceIncomeSqb);
    }
}
