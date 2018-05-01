package com.eds.ma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 本地资源属性配置
 * @Author gaoyan
 * @Date: 2017/5/23
 */
@Configuration
@PropertySource({"classpath:sys-config.properties","classpath:sys-common-config.properties"})
public class SysConfig {

    /**
     * 环境信息
     */
    @Value("${projectProfile}")
    private String projectProfile;

    /**
     * 阿里云oss accessKeyId
     */
    @Value("${accessKeyId}")
    private String accessKeyId;

    /**
     * 阿里云oss accessKeySecret
     */
    @Value("${accessKeySecret}")
    private String accessKeySecret;

    /**
     * 阿里云oss bucketName
     */
    @Value("${bucketName}")
    private String bucketName;

    /**
     * 微信授权
     */
    @Value("${wxAuthUrl}")
    private String wxAuthUrl;

    /**
     * 微信appid
     */
    @Value("${wxAppId}")
    private String wxAppId;

    /**
     * 微信AppSecret
     */
    @Value("${wxAppSecret}")
    private String wxAppSecret;

    /**
     * 微信授权回调url
     */
    @Value("${wxCallbackUrl}")
    private String wxCallbackUrl;

    /**
     * 微信授权token的url
     */
    @Value("${wxAuthTokenUrl}")
    private String wxAuthTokenUrl;

    /**
     * 微信授权后重定向url
     */
    @Value("${wxRedirectUrl}")
    private String wxRedirectUrl;

    /**
     * 微信获取用户信息url
     */
    @Value("${wxUserInfoUrl}")
    private String wxUserInfoUrl;

    /**
     * 获取微信accessToken的url
     */
    @Value("${wxAccessTokenUrl}")
    private String wxAccessTokenUrl;

    /**
     * 获取微信jsSdkTicket的url
     */
    @Value("${jsSdkTicketUrl}")
    private String jsSdkTicketUrl;

    /**
     * 小程序session的Url
     */
    @Value("${wxMaSessionUrl}")
    private String wxMaSessionUrl;

    /**
     * 微信小程序appid
     */
    @Value("${wxMaAppId}")
    private String wxMaAppId;

    /**
     * 微信小程序AppSecret
     */
    @Value("${wxMaAppSecret}")
    private String wxMaAppSecret;

    /**
     * 微信支付回调信息url
     */
    @Value("${wxPayCallbackUrl}")
    private String wxPayCallbackUrl;

    /**
     * 微信商户号
     */
    @Value("${wxMchId}")
    private String wxMchId;

    /**
     * 微信商户号
     */
    @Value("${wxMerchantKey}")
    private String wxMerchantKey;

    /**
     * openId的Cookie配置
     */
    @Value("${edsCookieHost}")
    private String edsCookieHost;

    public String getProjectProfile() {
        return projectProfile;
    }

    public void setProjectProfile(String projectProfile) {
        this.projectProfile = projectProfile;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getWxAuthUrl() {
        return wxAuthUrl;
    }

    public void setWxAuthUrl(String wxAuthUrl) {
        this.wxAuthUrl = wxAuthUrl;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWxAppSecret() {
        return wxAppSecret;
    }

    public void setWxAppSecret(String wxAppSecret) {
        this.wxAppSecret = wxAppSecret;
    }

    public String getWxCallbackUrl() {
        return wxCallbackUrl;
    }

    public void setWxCallbackUrl(String wxCallbackUrl) {
        this.wxCallbackUrl = wxCallbackUrl;
    }

    public String getWxAuthTokenUrl() {
        return wxAuthTokenUrl;
    }

    public void setWxAuthTokenUrl(String wxAuthTokenUrl) {
        this.wxAuthTokenUrl = wxAuthTokenUrl;
    }

    public String getWxRedirectUrl() {
        return wxRedirectUrl;
    }

    public void setWxRedirectUrl(String wxRedirectUrl) {
        this.wxRedirectUrl = wxRedirectUrl;
    }

    public String getWxUserInfoUrl() {
        return wxUserInfoUrl;
    }

    public void setWxUserInfoUrl(String wxUserInfoUrl) {
        this.wxUserInfoUrl = wxUserInfoUrl;
    }

    public String getWxAccessTokenUrl() {
        return wxAccessTokenUrl;
    }

    public void setWxAccessTokenUrl(String wxAccessTokenUrl) {
        this.wxAccessTokenUrl = wxAccessTokenUrl;
    }

    public String getJsSdkTicketUrl() {
        return jsSdkTicketUrl;
    }

    public void setJsSdkTicketUrl(String jsSdkTicketUrl) {
        this.jsSdkTicketUrl = jsSdkTicketUrl;
    }

    public String getWxMaSessionUrl() {
        return wxMaSessionUrl;
    }

    public void setWxMaSessionUrl(String wxMaSessionUrl) {
        this.wxMaSessionUrl = wxMaSessionUrl;
    }

    public String getWxMaAppId() {
        return wxMaAppId;
    }

    public void setWxMaAppId(String wxMaAppId) {
        this.wxMaAppId = wxMaAppId;
    }

    public String getWxMaAppSecret() {
        return wxMaAppSecret;
    }

    public void setWxMaAppSecret(String wxMaAppSecret) {
        this.wxMaAppSecret = wxMaAppSecret;
    }

    public String getEdsCookieHost() {
        return edsCookieHost;
    }

    public void setEdsCookieHost(String edsCookieHost) {
        this.edsCookieHost = edsCookieHost;
    }

    public String getWxPayCallbackUrl() {
        return wxPayCallbackUrl;
    }

    public void setWxPayCallbackUrl(String wxPayCallbackUrl) {
        this.wxPayCallbackUrl = wxPayCallbackUrl;
    }

    public String getWxMchId() {
        return wxMchId;
    }

    public void setWxMchId(String wxMchId) {
        this.wxMchId = wxMchId;
    }

    public String getWxMerchantKey() {
        return wxMerchantKey;
    }

    public void setWxMerchantKey(String wxMerchantKey) {
        this.wxMerchantKey = wxMerchantKey;
    }
}