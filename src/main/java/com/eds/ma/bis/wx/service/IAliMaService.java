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

}

