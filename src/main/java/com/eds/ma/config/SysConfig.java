package com.eds.ma.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 本地资源属性配置
 * @Author gaoyan
 * @Date: 2017/5/23
 */
@Data
@Configuration
@PropertySource({"classpath:commonConfig.properties","classpath:systemConfig.properties"})
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
     * 微信消息token
     */
    @Value("${wxMessageToken}")
    private String wxMessageToken;

    /**
     * 微信公众号消息加密密钥
     */
    @Value("${wxMessageAESKey}")
    private String wxMessageAESKey;

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
     * 小程序发送模板消息的Url
     */
    @Value("${wxMaTemplateUrl}")
    private String wxMaTemplateUrl;

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

    /**
     * ali的Cookie配置
     */
    @Value("${edsAliCookieHost}")
    private String edsAliCookieHost;

    /**
     * 阿里小程序appid
     */
    @Value("${aliMaAppId}")
    private String aliMaAppId;

    /**
     * 阿里网关密钥
     */
    @Value("${aliGatewayPrivateKey}")
    private String aliGatewayPrivateKey;

    /**
     * 阿里网关公钥
     */
    @Value("${aliGatewayPublicKey}")
    private String aliGatewayPublicKey;

    /**
     * 阿里网关url
     */
    @Value("${aliGatewayUrl}")
    private String aliGatewayUrl;

    /**
     * 阿里网关url
     */
    @Value("${aliPayCallbackUrl}")
    private String aliPayCallbackUrl;
}