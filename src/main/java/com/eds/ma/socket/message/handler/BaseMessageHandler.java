package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.message.vo.CommonHeadMessageVo;

/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
public abstract class BaseMessageHandler {
    public abstract void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge);

}