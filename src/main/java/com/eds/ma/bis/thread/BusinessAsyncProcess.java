package com.eds.ma.bis.thread;

import com.alibaba.fastjson.JSON;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.vo.PayRefundVo;
import com.eds.ma.bis.wx.service.IWxRefundPayService;
import com.eds.ma.email.SendMailUtil;
import com.xcrm.cloud.database.db.util.StringUtil;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**	
 * 业务相关异步线程统一线程池处理类
 * @author gaoyan
 * @version 1.0
 * @created 2015年12月30日 下午4:44:23
 */
@Service
public class BusinessAsyncProcess {
	private static final Logger log = LoggerFactory.getLogger(BusinessAsyncProcess.class);

	@Autowired
	private IWxRefundPayService wxRefundPayService;

	@Autowired
	private IOrderService orderService;

	/**
	 * 异步退款处理
	 * @param refundPayOrderPool
	 * @param toRefundMoney
	 */
	@Async
	public void asyncPayRefund(List<PayOrder> refundPayOrderPool, BigDecimal toRefundMoney) {
		List<PayRefundVo> payRefundVos = new ArrayList<>();
		BigDecimal leftToRefundMoney = toRefundMoney;
		for (PayOrder payOrder : refundPayOrderPool) {
			if (leftToRefundMoney.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal payOrderMoney = payOrder.getPayMoney();
				BigDecimal refundMoney = null;
				if (leftToRefundMoney.compareTo(payOrderMoney) > 0) {
					refundMoney = payOrderMoney;
					leftToRefundMoney = leftToRefundMoney.subtract(payOrderMoney);
				} else {
					refundMoney = leftToRefundMoney;
					leftToRefundMoney = BigDecimal.ZERO;
				}
				PayRefundVo payRefundVo = new PayRefundVo();
				payRefundVo.setPayOrder(payOrder);
				payRefundVo.setRefundMoney(refundMoney);
				payRefundVos.add(payRefundVo);
			} else {
				break;
			}
		}

		if(ListUtil.isNotEmpty(payRefundVos)){
			payRefundVos.forEach(payRefundVo -> {
				try {
					wxRefundPayService.submiteRefund(payRefundVo.getPayOrder(), payRefundVo.getRefundMoney());
					//保存提现交易记录
					FinanceIncome financeIncome = new FinanceIncome();
					financeIncome.setTransCode(OrderCodeCreater.createTradeNO());
					financeIncome.setContent("提现");
					financeIncome.setUserId(payRefundVo.getPayOrder().getUserId());
					financeIncome.setOpenId(payRefundVo.getPayOrder().getBuyerId());
					financeIncome.setTransType(TransTypeEnum.S_JYLX_TX.value());
					financeIncome.setTransTime(DateFormatUtils.getNow());
					financeIncome.setMoney(payRefundVo.getRefundMoney());
					financeIncome.setOrderCode(payRefundVo.getPayOrder().getOrderCode());
					orderService.saveFinanceIncome(financeIncome);
				}catch (Exception e){
					//退款失败,发送系统bug邮件提醒
					String text = "###############params###############" + System.getProperty("line.separator")
							+JSON.toJSONString(payRefundVo)
							+ System.getProperty("line.separator")
							+ "###############exception###############:" + System.getProperty("line.separator")
							+ StringUtil.getStackTrace(e);
					SendMailUtil sendEmail = new SendMailUtil(
							"15604090129@163.com", "jlt2016YUIOYHN", "15604090129@163.com",
							"退款失败，请及时关注", text, "eds-share", "", "");
					try {
						sendEmail.send();
					} catch (Exception ex) {
						log.error("SendMailUtil.send() error ",ex);
					}
				}
			});
		}
	}
	
}
