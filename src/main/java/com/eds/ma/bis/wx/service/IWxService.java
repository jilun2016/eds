package com.eds.ma.bis.wx.service;


import com.eds.ma.bis.wx.JsAPISignature;
import com.eds.ma.bis.wx.WxTokenTypeEnum;
import com.eds.ma.bis.wx.entity.AccessToken;

/**
 * 微信服务
 * @Author gaoyan
 * @Date: 2017/7/8
 */
public interface IWxService {

    /**
     * 生成微信授权跳转url
     * @param redirectUrl
     */
    String buildWxAuthRedirect(String redirectUrl);

    /**
     * 获取微信js-sdk 分享配置信息
     * @param currentSharePageUrl  当前页面url
     * @return
     */
    JsAPISignature getShareConfigInfo(String currentSharePageUrl) throws Exception;

    /**
     * 获取微信公众号的AccessToken
     * @param type
     * @param appId
     * @return
     */
    AccessToken queryAccessTokenByType(WxTokenTypeEnum type, String appId);

    /**
     * 获取微信的js的token
     * @param appId
     * @return
     */
    AccessToken queryWxJsAccessToken(String appId);

    /**
     * 更新微信的accessToken
     * @param accessToken
     */
    void updateAccessToken(AccessToken accessToken);
}

