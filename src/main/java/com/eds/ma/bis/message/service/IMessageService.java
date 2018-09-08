package com.eds.ma.bis.message.service;

import com.eds.ma.bis.message.TmplEvent;
import com.eds.ma.bis.message.vo.SmsMessageContent;

/**
 * 消息service
 * @Author gaoyan
 * @Date: 2018/4/22
 */
public interface IMessageService {

    /**
     * 推送微信公众号消息
     * @param openId
     * @param tmplEvent 推送事件 {@link com.eds.ma.bis.message.TmplEvent}
     */
    void pushWxMessage(String openId, TmplEvent tmplEvent, String... parameters);

    /**
     * 推送短信
     * @param smsMessageContent
     */
    void pushSmsMessage(SmsMessageContent smsMessageContent);
}
