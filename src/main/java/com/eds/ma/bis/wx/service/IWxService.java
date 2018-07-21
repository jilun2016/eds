package com.eds.ma.bis.wx.service;

import com.eds.ma.bis.user.entity.WxUser;

/**
 * 微信公众号服务
 * @Author gaoyan
 * @Date: 2018/7/22
 */
public interface IWxService {

    /**
     * 保存公众号的用户信息
     * @param openId
     * @param subscribeStatus
     */
    WxUser saveWxUser(String openId, Boolean subscribeStatus);
}

