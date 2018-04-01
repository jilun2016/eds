package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.wx.JsAPISignature;
import com.eds.ma.bis.wx.WxTokenTypeEnum;
import com.eds.ma.bis.wx.entity.AccessToken;
import com.eds.ma.bis.wx.sdk.common.decrypt.AesException;
import com.eds.ma.bis.wx.sdk.common.decrypt.SHA1;
import com.eds.ma.bis.wx.sdk.common.util.RandomStringGenerator;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.Objects;

/**
 * 微信service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxServiceImpl implements IWxService {

    private static Logger logger = Logger.getLogger(WxServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport dao;


    @Override
    public String buildWxAuthRedirect(String redirectUrl) {
        StringBuilder wxAuthUrl = new StringBuilder(sysConfig.getWxAuthUrl());
        wxAuthUrl.append("?appid=").append(sysConfig.getWxAppId());
        wxAuthUrl.append("&redirect_uri=").append(URLEncoder.encode(sysConfig.getWxCallbackUrl()));
        wxAuthUrl.append("&response_type=code");
        wxAuthUrl.append("&scope=snsapi_base");
        String state = URLEncoder.encode(redirectUrl);
        wxAuthUrl.append("&state=").append(state);
        wxAuthUrl.append("#wechat_redirect");
        return wxAuthUrl.toString();
    }

    @Override
    public JsAPISignature getShareConfigInfo(String currentSharePageUrl) throws Exception {
        if (StringUtils.isBlank(currentSharePageUrl)) {
            return null;
        }

        if (currentSharePageUrl.indexOf("#") > 0) {
            currentSharePageUrl = currentSharePageUrl.substring(0,currentSharePageUrl.indexOf("#"));
        }
        String appId = sysConfig.getWxAppId();
        AccessToken jsToken = queryWxJsAccessToken(appId);
        if (Objects.isNull(jsToken)) {
            return null;
        }
        String jsTicket = jsToken.getToken();
        long updated = jsToken.getUpdated().getTime();
        long expired = updated + Long.parseLong((jsToken.getExpired() * 1000) + "");
        return createJsAPISignature(appId,currentSharePageUrl, jsTicket, expired);
    }

    /**
     * 创建JsAPI签名
     * @param url
     * @return
     */
    private JsAPISignature createJsAPISignature(String appId,String url, String ticket,long expired){
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = RandomStringGenerator.getRandomStringByLength(16);
        try {
            String signature = SHA1.getSHA1("jsapi_ticket=" + ticket + "&noncestr=" + nonce + "&timestamp=" + timestamp + "&url=" + url);

            JsAPISignature jsAPISignature = new JsAPISignature();
            jsAPISignature.setAppId(appId);
            jsAPISignature.setNonce(nonce);
            jsAPISignature.setTimestamp(timestamp);
            jsAPISignature.setSignature(signature);
            jsAPISignature.setUrl(url);
            jsAPISignature.setExpired(expired);
            return jsAPISignature;
        } catch (AesException e) {
            logger.error("createJsAPISignature failed", e);
            throw new BizCoreRuntimeException(e.getMessage());
        }
    }

    @Override
    public AccessToken queryAccessTokenByType(WxTokenTypeEnum type, String appId) {
        Ssqb query = Ssqb.create("com.jlt.eds.wx.queryAccessTokenByType")
                .setParam("appId", appId)
                .setParam("type", type.value());
        return dao.findForObj(query, AccessToken.class);
    }

    @Override
    public AccessToken queryWxJsAccessToken(String appId) {
        Ssqb query = Ssqb.create("com.jlt.eds.wx.queryAccessTokenByType")
                .setParam("appId", appId)
                .setParam("type", WxTokenTypeEnum.JS_TICKET.value());
        return dao.findForObj(query, AccessToken.class);
    }

    @Override
    public void updateAccessToken(AccessToken accessToken) {
        dao.update(accessToken);
    }

}
