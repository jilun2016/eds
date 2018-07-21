package com.eds.ma.bis.wx.service;


import javax.servlet.http.HttpServletResponse;

/**
 * 微信消息服务
 * @Author gaoyan
 * @Date: 2018/7/20
 */
public interface IWxMessageService {

    /**
     * 推送微信消息回调处理
     * @param openId
     * @param msgSignature
     * @param timestamp
     * @param nonce
     * @param xml
     * @param response
     */
    void handleWxCallBackMessage(String openId, String msgSignature, String timestamp, String nonce, String xml, HttpServletResponse response);

}

