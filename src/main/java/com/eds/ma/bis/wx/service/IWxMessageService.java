package com.eds.ma.bis.wx.service;


/**
 * 微信消息服务
 * @Author gaoyan
 * @Date: 2018/7/20
 */
public interface IWxMessageService {

    /**
     * 推送微信消息回调处理
     * @param xml
     */
    void handleWxCallBackMessage(String xml);

}

