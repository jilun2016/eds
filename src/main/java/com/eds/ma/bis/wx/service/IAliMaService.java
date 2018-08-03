package com.eds.ma.bis.wx.service;

import com.eds.ma.bis.user.vo.AliUserInfoVo;

/**
 * 支付宝小程序服务
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public interface IAliMaService {

    /**
     * 获取小程序用户信息
     * @param code
     * @return
     */
    AliUserInfoVo queryAliUserInfo(String code);

    /**
     * 保存ali用户信息
     *
     * @param nickname
     * @param headimgurl
     * @param rawData
     * @param tokenRawData
     */
    void saveAliUser(String aliUid, String nickname, String headimgurl, String rawData, String tokenRawData);

    /**
     * ali小程序登陆
     * @param appId
     * @param aliUid
     * @param mobile
     * @param smsCode
     */
    void aliMaLogin(String appId, String aliUid, String mobile, String smsCode);
}

