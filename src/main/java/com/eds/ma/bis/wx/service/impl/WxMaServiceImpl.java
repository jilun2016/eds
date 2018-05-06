package com.eds.ma.bis.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.util.AesCbcUtil;
import com.eds.ma.util.CookieUtils;
import com.eds.ma.util.HTTPUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信小程序service
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Service
@Transactional
public class WxMaServiceImpl implements IWxMaService {

    private static Logger logger = Logger.getLogger(WxMaServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private IUserService userService;

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public UserInfoVo queryMaUserInfo(String code, String encryptedData, String iv) {
        Map<String,String> sessionMap = getWxMaSessionKey(code);
        String sessionKey = MapUtils.getString(sessionMap,"sessionKey");
        String resultJson = MapUtils.getString(sessionMap,"resultJson");
        try {
            String result = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            if (StringUtils.isNotEmpty(result)) {
                JSONObject userInfoJSON = JSONObject.parseObject(result);
                if(StringUtils.isBlank(userInfoJSON.getString("openId"))){
                    throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
                }
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setOpenId(userInfoJSON.getString("openId"));
                userInfoVo.setHeadimgurl(userInfoJSON.getString("avatarUrl"));
                userInfoVo.setNickName(userInfoJSON.getString("nickName"));
                //异步保存用户信息
                userService.asyncSaveOpenId(userInfoVo.getOpenId(),userInfoVo.getNickName(),userInfoVo.getHeadimgurl(),userInfoJSON.toJSONString());
                return userInfoVo;
            }else{
                logger.error("WxMinaServiceImpl.parse user info failed.resultJson:{}",resultJson);
                throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
            }
        } catch (Exception e) {
            logger.error("WxMinaServiceImpl.parse user info failed.resultJson:{}",resultJson);
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
    }

    @Override
    public void saveUserPhone(User user, String code, String encryptedData, String iv) {
        Map<String,String> sessionMap = getWxMaSessionKey(code);
        String sessionKey = MapUtils.getString(sessionMap,"sessionKey");
        String resultJson = MapUtils.getString(sessionMap,"resultJson");
        try {
            String result = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            if (StringUtils.isNotEmpty(result)) {
                JSONObject userPhoneInfoJSON = JSONObject.parseObject(result);
                if((Objects.isNull(userPhoneInfoJSON))||(StringUtils.isBlank(userPhoneInfoJSON.getString("phoneNumber")))){
                    throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
                }
                String userMobile = userPhoneInfoJSON.getString("phoneNumber");
                User updateUser = new User();
                updateUser.setId(user.getId());
                updateUser.setMobile(userMobile);
                updateUser.setMobileRawData(userPhoneInfoJSON.toString());
                dao.update(updateUser);
            }else{
                logger.error("WxMinaServiceImpl.parse user info failed.resultJson:{}",resultJson);
                throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
            }
        } catch (Exception e) {
            logger.error("WxMinaServiceImpl.parse user info failed.resultJson:{}",resultJson);
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
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
        result.put("resultJson",resultJson);
        result.put("sessionKey",sessionKey);
        return result;
    }



}
