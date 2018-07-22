package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.user.entity.WxUser;
import com.eds.ma.bis.wx.entity.WxAccessToken;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.util.HTTPUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class WxServiceImpl implements IWxService {

    private static Logger logger = Logger.getLogger(WxServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public WxUser saveWxUser(String openId, Boolean subscribeStatus) {
        if(BooleanUtils.isFalse(subscribeStatus)){
            WxUser updateSubscribeWxUser = new WxUser();
            updateSubscribeWxUser.setWxOpenId(openId);
            updateSubscribeWxUser.setSubscribeStatus(subscribeStatus);
            dao.update(updateSubscribeWxUser);
        }else{
            //查询token
            QueryBuilder queryWxAccessTokenQb = QueryBuilder.where(Restrictions.eq("appId",sysConfig.getWxAppId()))
                    .and(Restrictions.eq("dataStatus",1));
            WxAccessToken wxAccessToken = dao.query(queryWxAccessTokenQb,WxAccessToken.class);
            if(Objects.nonNull(wxAccessToken)){
                //通过openId 获取用户信息
                Map<String,Object> wxUserParaMap = new HashMap<>();
                wxUserParaMap.put("access_token",wxAccessToken.getToken());
                wxUserParaMap.put("openid",openId);
                wxUserParaMap.put("lang","zh_CN");
                Map wxUserMap = HTTPUtil.sendGet(sysConfig.getWxUserInfoUrl(),wxUserParaMap);
                logger.info("getwxUser result:"+wxUserMap);
                if(StringUtils.isEmpty(MapUtils.getString(wxUserMap,"errmsg"))){
                    //通过openId查询db公众号用户信息
                    QueryBuilder queryWxUserQb = QueryBuilder.where(Restrictions.eq("wxOpenId",openId))
                            .and(Restrictions.eq("dataStatus",1));
                    WxUser dbWxUser = dao.query(queryWxUserQb,WxUser.class);
                    WxUser saveWxUser = new WxUser();
                    saveWxUser.setWxOpenId(openId);
                    Boolean subscribe = MapUtils.getBoolean(wxUserMap,"subscribe");
                    saveWxUser.setSubscribeStatus(subscribe);
                    saveWxUser.setRawData(String.valueOf(wxUserMap));
                    String nickName = MapUtils.getString(wxUserMap,"nickname");
                    String headimgurl = MapUtils.getString(wxUserMap,"headimgurl");
                    String unionid = MapUtils.getString(wxUserMap,"unionid");
                    saveWxUser.setNickname(nickName);
                    saveWxUser.setHeadimgurl(headimgurl);
                    saveWxUser.setWxUnionId(unionid);
                    if(Objects.nonNull(dbWxUser)){
                        saveWxUser.setUpdated(DateFormatUtils.getNow());
                        dao.update(saveWxUser);
                    }else{
                        saveWxUser.setCreated(DateFormatUtils.getNow());
                        dao.create(saveWxUser);
                    }
                    return saveWxUser;
                }else{
                    logger.info("getwxUser error:"+wxUserMap);
                }
            }
        }

        return null;
    }

    private Map<String,String> getWxMaSessionKey(String code){
        Map<String,String> result = new HashMap<>();
        Map<String,Object> sessionParaMap = new HashMap<>();
        sessionParaMap.put("js_code",code);
        sessionParaMap.put("appid",sysConfig.getWxMaAppId());
        sessionParaMap.put("grant_type","authorization_code");
        sessionParaMap.put("secret",sysConfig.getWxMaAppSecret());

        String resultJson = HTTPUtil.sendGetString(sysConfig.getWxMaSessionUrl(),sessionParaMap);
        logger.info("WxMinaServiceImpl.queryMaSession.result:{}",resultJson);
        Map<String,Object> resultJsonMap = HTTPUtil.Json2Map(resultJson);
        int errorCode = MapUtils.getIntValue(resultJsonMap,"errcode",-1);
        if(!Objects.equals(errorCode, -1)){
            logger.error("WxMinaServiceImpl.queryMaSession failed.result:{}",resultJson);
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
        String sessionKey = MapUtils.getString(resultJsonMap,"session_key");
        if(StringUtils.isBlank(sessionKey)){
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }

        String openId = MapUtils.getString(resultJsonMap,"openid");
        if(StringUtils.isBlank(openId)){
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
        result.put("resultJson",resultJson);
        result.put("openId",openId);
        result.put("sessionKey",sessionKey);
        return result;
    }



}
