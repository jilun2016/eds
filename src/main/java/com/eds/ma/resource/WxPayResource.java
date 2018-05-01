package com.eds.ma.resource;

import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.wx.service.IWxPayService;
import com.eds.ma.resource.request.BalancePayRequest;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/wx/pay")
public class WxPayResource extends BaseAuthedResource{

    private static Logger logger = Logger.getLogger(DeviceResource.class);

    @Autowired
    private IWxPayService wxPayService;

    @Autowired
    private IEdsConfigService edsConfigService;

    /**
     * 微信余额充值
     */
    @POST
    @Path("/balance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userBalancePrepay(@Valid BalancePayRequest request){
        logger.debug("UserResource.userBalancePrepay({},{})",super.getOpenId(),request);
        Map<String, Object> paySignMap = wxPayService.prepay(super.getOpenId(),TransTypeEnum.S_JYLX_YECZ.value(),request.getBalance(),"余额充值");
        return Response.ok(paySignMap).build();
    }

    /**
     * 微信押金支付
     */
    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userDepositPrepay(){
        logger.debug("UserResource.userDepositPrepay({})",super.getOpenId());
        //计算押金
        BigDecimal defaultUnitDeposit = edsConfigService.queryEdsConfigDeposit();
        Map<String, Object> paySignMap = wxPayService.prepay(super.getOpenId(), TransTypeEnum.S_JYLX_YJCZ.value(),defaultUnitDeposit,"支付押金");
        return Response.ok(paySignMap).build();
    }

}