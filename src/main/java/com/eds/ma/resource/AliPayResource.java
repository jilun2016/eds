package com.eds.ma.resource;

import com.eds.ma.bis.common.service.IEdsConfigService;
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
import java.math.BigDecimal;
import java.util.Map;

/**
 * ali支付资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/ali/pay")
public class AliPayResource extends BaseAuthedResource{

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
    public Map<String, Object> userBalancePrepay(@Valid BalancePayRequest request){
        logger.debug("UserResource.userBalancePrepay({},{})",super.getOpenId(),request);
        return wxPayService.prepay(super.getUserId(),super.getOpenId(),TransTypeEnum.S_JYLX_YECZ.value(),request.getBalance(),"余额充值");
    }

    /**
     * 微信押金支付
     */
    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> userDepositPrepay(){
        logger.debug("UserResource.userDepositPrepay({})",super.getOpenId());
        //计算押金
        BigDecimal defaultUnitDeposit = edsConfigService.queryEdsConfigDeposit();
        return wxPayService.prepay(super.getUserId(), super.getOpenId(), TransTypeEnum.S_JYLX_YJCZ.value(),defaultUnitDeposit,"支付押金");
    }

}