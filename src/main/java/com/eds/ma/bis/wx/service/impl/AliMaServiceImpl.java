package com.eds.ma.bis.wx.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.eds.ma.bis.user.entity.AliUser;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.AliUserInfoVo;
import com.eds.ma.bis.wx.service.IAliMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

/**
 * 支付宝小程序service
 * @Author gaoyan
 * @Date: 2018/7/17
 */
@Service
@Transactional
public class AliMaServiceImpl implements IAliMaService {

    private static Logger logger = Logger.getLogger(AliMaServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IUserService userService;


    @Override
    public AliUserInfoVo queryAliUserInfo(String code) {
        AliUserInfoVo aliUserInfoVo = new AliUserInfoVo();
        AlipayClient alipayClient = new DefaultAlipayClient(
                sysConfig.getAliGatewayUrl(),sysConfig.getAliMaAppId(),sysConfig.getAliGatewayPrivateKey(),
                "json","GBK",sysConfig.getAliGatewayPublicKey(),"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(code);
        AlipaySystemOauthTokenResponse tokenResponse = null;
        try {
            tokenResponse = alipayClient.execute(request);
            logger.info("AliMaServiceImpl.queryAliUserInfo.OauthToken result:{}",tokenResponse.getBody());
        } catch (Exception e) {
            logger.info("AliMaServiceImpl.queryAliUserInfo.OauthToken result:{}",code);
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
        if(tokenResponse.isSuccess()){
            AlipayUserInfoShareRequest userRequest = new AlipayUserInfoShareRequest();
            try {
                AlipayUserInfoShareResponse userResponse = alipayClient.execute(userRequest,tokenResponse.getAccessToken());
                logger.info("AliMaServiceImpl.queryAliUserInfo.userInfo result:{}",userResponse.getBody());
                if(userResponse.isSuccess()){
                    String aliUid = userResponse.getUserId();
                    String aliNickname = userResponse.getNickName();
                    String aliHeadimgurl = userResponse.getAvatar();
                    aliUserInfoVo.setAliUid(aliUid);
                    aliUserInfoVo.setHeadimgurl(aliHeadimgurl);
                    aliUserInfoVo.setNickName(aliNickname);
                    saveAliUser(aliUid, aliNickname, aliHeadimgurl, userResponse.getBody(), tokenResponse.getBody());
                } else {
                    logger.info("AliMaServiceImpl.userResponse fail.userInfo result:{}",userResponse.getBody());
                    throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
                }
            } catch (Exception e) {
                throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
            }
        } else {
            logger.info("AliMaServiceImpl.queryAliUserInfo.fail result:{}",tokenResponse.getBody());
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }
        return aliUserInfoVo;
    }


    @Override
    public void saveAliUser(String aliUid, String nickname, String headimgurl, String rawData, String tokenRawData) {
        Date now = DateFormatUtils.getNow();
        AliUser dbAliUser = dao.queryById(aliUid,AliUser.class);
        if(Objects.nonNull(dbAliUser)){
            dbAliUser.setAliHeadimgurl(headimgurl);
            dbAliUser.setAliNickname(nickname);
            dbAliUser.setAliUserRawData(rawData);
            dbAliUser.setAliTokenRawData(tokenRawData);
            dbAliUser.setUpdated(now);
            dao.update(dbAliUser);
        }else{
            AliUser aliUser = new AliUser();
            aliUser.setAliUid(aliUid);
            aliUser.setAliNickname(nickname);
            aliUser.setAliHeadimgurl(headimgurl);
            aliUser.setAliUserRawData(rawData);
            aliUser.setAliTokenRawData(tokenRawData);
            aliUser.setCreated(now);
            dao.create(aliUser);
        }
    }

    @Override
    public void aliMaLogin(String aliUid, String mobile, String smsCode) {
        User user = userService.queryUserByMobile(mobile);
        if (Objects.isNull(user)) {
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }

        //是否ali授权过,保存ali用户信息
        AliUser aliUser = userService.queryUserAliByAliUid(aliUid);
        if(Objects.isNull(aliUser)){
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }

        String dbSmsCode = user.getAliSmsCode();
        long now = System.currentTimeMillis();
        Date activeExpired = user.getAliSmsExpired();
        if(StringUtils.isEmpty(dbSmsCode)) {
            //验证码错误
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_ERROR);
        }
        if(!dbSmsCode.equals(smsCode)) {
            //验证码错误
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_ERROR);
        } else if(activeExpired != null && activeExpired.getTime() < now){
            //已过期
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_EXPIRED);
        } else {
            Ssqb updateInvalidUidSqb = Ssqb.create("com.eds.user.updateInvalidAliUid")
                    .setParam("aliUid", aliUid);
            dao.updateByMybatis(updateInvalidUidSqb);

            Ssqb query = Ssqb.create("com.eds.user.updateAliMemberLoginSuc")
                    .setParam("loginTime", new Timestamp(System.currentTimeMillis()))
                    .setParam("aliUid", aliUid)
                    .setParam("userId", user.getId());
            dao.updateByMybatis(query);
            //初始化钱包
            userService.queryUserWalletByUserId(user.getId());
        }
    }

}
