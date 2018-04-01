package com.eds.ma.resource;

import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.DeviceDepositPrePayRequest;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 微信小程序处理
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/wx")
public class WxPayResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(WxPayResource.class);

	@Autowired
	private IWxMaService wxMaService;

	@Autowired
	private IWxService wxService;

	@Autowired
	private SysConfig sysConfig;

	/**
	 * 微信预支付
	 * @param request
	 */
	@POST
	@Path("/pre_pay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void wxPrepay(@Valid DeviceDepositPrePayRequest request){
		logger.debug("WxMaResource.wxPrepay({})",request);


		GiftWxPrePayOrder wxPrePayOrder = new GiftWxPrePayOrder();
		wxPrePayOrder.setChainId(chainId);
		wxPrePayOrder.setOrderCode(OrderCodeCreater.createTradeNO());
		wxPrePayOrder.setOpenId(WebUtils.getOpenId(request));
		wxPrePayOrder.setTitle(giftDetailVo.getGiftBaseName()+"支付");
		wxPrePayOrder.setPayMoney(giftFee.multiply(BigDecimal.valueOf(giftCount)));
		wxPrePayOrder.setGiftCount(giftCount);
		wxPrePayOrder.setGiftId(giftId);
		wxPrePayOrder.setGiftName(giftDetailVo.getGiftBaseName());
		wxPrePayOrder.setUserId(userId);
		wxPrePayOrder.setRemark(votePrepayRequest.getRemark());
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		try {

			String payResult = wxService.jsOnPay(wxPrePayOrder);
			resultMap.put("payResult", payResult);
		} catch (Exception e) {
			logger.error("VoteController.votePrepay error",e);
			throw new VoteRuntimeException("10002");
		}
		ResponseUtils.createSuccessResponse(response,resultMap);
	}

	/**
	 * 微信支付回调
	 * @param request
	 * @param response
	 */
	@RequestMapping(value ="/vote/pay/callback",method = {RequestMethod.POST})
	public void wxPayCallback(HttpServletRequest request, HttpServletResponse response) {
		try {
			String xml = InputStreamUtils.InputStreamTOString(request.getInputStream(), "UTF-8");
			logger.info("~~~~~~~~~~~~~~~~~~callback_xml:" + xml);
			wxService.optWxPayCallback(xml);
		} catch (Exception e) {
			logger.error("wxPayCallback occurs exception ",e);
		}
	}

}