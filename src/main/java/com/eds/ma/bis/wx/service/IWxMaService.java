package com.eds.ma.bis.wx.service;

import com.eds.ma.bis.user.vo.UserInfoVo;

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
     * 获取小程序 openId
     * @param code
     * @return
     */
    String queryMaUserOpenId(String code);

    /**
     * 保存小程序手机号
     * @param userId
     * @param code
     * @param encryptedData
     * @param iv
     */
    void saveUserPhone(Long userId, String code, String encryptedData, String iv);
}

