package com.eds.ma.bis.wx.service;


import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * ali支付服务
 * @Author gaoyan
 * @Date: 2018/4/5
 */
public interface IAliPayService {

    /**
     * 生成支付code
     * @return
     */
    String getPayCode();

    /**
     * ali预支付
     * @param userId
     * @param aliUid
     * @param transType
     * @param payMoney
     * @param payTitle
     */
    String prepay(Long userId, String aliUid, String transType, BigDecimal payMoney, String payTitle);

    /**
     * 处理ali支付回调
     * @param requestParams
     * @param response
     */
    void optAliPayCallback(Map<String, String[]> requestParams, HttpServletResponse response);

}

