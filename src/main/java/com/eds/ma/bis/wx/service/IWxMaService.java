package com.eds.ma.bis.wx.service;

import com.eds.ma.bis.user.vo.UserInfoVo;

import java.math.BigDecimal;

/**
 * 微信小程序服务
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public interface IWxMaService {

    /**
     * 获取小程序用户信息
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    UserInfoVo queryMaUserInfo(String code, String encryptedData, String iv);

    /**
     * 微信推送消息
     * @param openId
     * @param tmplEvent
     */
    void pushWxMaMessage(String openId,String tmplEvent);

}

