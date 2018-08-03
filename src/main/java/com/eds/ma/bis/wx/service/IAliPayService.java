package com.eds.ma.bis.wx.service;


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
     * 微信预支付
     * @param userId
     * @param openId
     * @param transType
     * @param payMoney
     * @param payTitle
     */
    Map<String, Object> prepay(Long userId, String openId, String transType, BigDecimal payMoney, String payTitle);

    /**
     * 处理微信支付回调
     * @param xml
     */
    String optAliPayCallback(String xml);



}

