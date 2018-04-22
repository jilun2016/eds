package com.eds.ma.bis.message.service;

import com.eds.ma.bis.message.vo.SmsMessageContent;

/**
 * 消息service
 * @Author gaoyan
 * @Date: 2018/4/22
 */
public interface IMessageService {

    /**
     * 推送微信小程序消息
     * @param openId
     * @param tmplEvent 推送事件 {@link com.eds.ma.bis.message.TmplEvent}
     */
    void pushWxMaMessage(String openId, String tmplEvent);

    /**
     * 推送短信
     * @param smsMessageContent
     */
    void pushSmsMessage(SmsMessageContent smsMessageContent);
}
