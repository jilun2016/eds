package com.eds.ma.resource;

import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.wx.service.IAliPayService;
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
import java.util.HashMap;
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
    private IAliPayService aliPayService;

    @Autowired
    private IEdsConfigService edsConfigService;

    /**
     * ali余额充值
     */
    @POST
    @Path("/balance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> userBalancePrepay(@Valid BalancePayRequest request){
        logger.debug("UserResource.userBalancePrepay({},{})",super.getOpenId(),request);
        String prePayParams = aliPayService.prepay(super.getUserId(),super.getUser().getAliUid(),TransTypeEnum.S_JYLX_YECZ.value(),request.getBalance(),"余额充值");
        Map<String,String> prePayResult = new HashMap<>();
        prePayResult.put("prePayParams",prePayParams);
        return prePayResult;
    }

    /**
     * ali押金支付
     */
    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> userDepositPrepay(){
        logger.debug("UserResource.userDepositPrepay({})",super.getUserId());
        //计算押金
        BigDecimal defaultUnitDeposit = edsConfigService.queryEdsConfigDeposit();
        String prePayParams = aliPayService.prepay(super.getUserId(), super.getUser().getAliUid(), TransTypeEnum.S_JYLX_YJCZ.value(),defaultUnitDeposit,"支付押金");
        Map<String,String> prePayResult = new HashMap<>();
        prePayResult.put("prePayParams",prePayParams);
        return prePayResult;
    }

}