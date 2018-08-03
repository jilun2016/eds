package com.eds.ma.bis.wx.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.eds.ma.bis.user.entity.AliUser;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWxMa;
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
                    throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
                }
            } catch (Exception e) {
                throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
            }
        } else {
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
            dao.save(aliUser);
        }
    }

    @Override
    public void aliMaLogin(String appId, String aliUid, String mobile, String smsCode) {
        User user = userService.queryUserByMobile(mobile);
        if (Objects.isNull(user)) {
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
            Ssqb query = Ssqb.create("com.eds.user.updateAliMemberLoginSuc")
                    .setParam("loginTime", new Timestamp(System.currentTimeMillis()))
                    .setParam("appId", appId)
                    .setParam("aliUid", aliUid)
                    .setParam("userId", user.getId());
            dao.updateByMybatis(query);
            //初始化钱包
            userService.saveDefaultUserWallet(user.getId());
        }
    }

    public static void main(String[] args) throws AlipayApiException {
        String aliGatewayPrivateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbvpvdt7M4q/ERvj/pn30NVl4lWkjkxNrQGt94A8Yg3mOgdaYCuaMqk5/yAiuQH/GFz1fBl/1hs3vIugrE1sbzQZdJXARC2I63Ade1Janj1/ZgCVffylHpNwV3JkGUsc0ZW9etcSY6N3nuUZpeUzCGkGTkWvrYaC7DJjIlx1tntTl+orhNmtP/N9tt+cAvZ0mkbmj2FEU7Q2ouqHCOiGe3oqGigk40N6yGgEnlc0RdN9luevECKguerb1q0FPLppItU+/YhtXraAR7Ew5BmGuZqzSPGqkLliVvlfhyXvv6nlj8mCZUUm1VLKGEVUIuCm4u/Fe0jAjQzVG0aEOoGRMLAgMBAAECggEBAI3bToLjqIVs7zWXEQXNNAzq5vOkyZI9z4QPhg39egBEL4BeyGfhCEJmlh/LZDxxK/8SPT9jHfJYld3dvqNNcYbt7ktuWZ89OrhsIDqp5JVYBtuwhu6ajIMcj4jYOpGiZUq8wbUDw6rxDslIYI+bvC5E5PCuAZ8NPdKYPsL0U0ggeE9GNB8+F3aWtuZEOCCJuPKz82DyueOBeLrPTrYW4ecdAR3UcQO2Iu3QRuGF3LjvHoqQfbGPZSnVAXt4iix3e/+9DxFucVIzcSQe9EpFmH49DtE7u9q4qtSp7MY79wvU4a5FaUHM/fe9f2JGQ9A8wPXpZ7KjUeZrK2BHTavp95ECgYEAyCuoyCwhOqBmBEDwTSFbwibzabZwiVsXsUw3dx+0GI6OpOATKE27vuIHwd5HCpUjbz2XScZk/WaVxmCSwawNb+Ix4oHiP63PFgcU+stF16Lir/3x1hWMsgcc29+F4ax8sKhu2WUG0+uDKXTM9/Y6g2RfYk/H85UKgTSzV3zo4KkCgYEAxy7l+Qt8gyghbSynm35C7oWuQ0QxkmDJ+I1SIOC7dgc1AAUCFuBtjNTjau4/mtnQVvQgy3wg5+hx39Yp8GZg/WFmNAc3NhMIsPBr+5YnMx+kjaYojsvEIbM4ZzdY7qGAmuwB/Az3lh2u15LC1Vafl52EThYPsXBIho6YB8cgwpMCgYADqLinIBdjaPc0cLNz1X5F6ExezFT2TdH7Dbed19iE8mYtIWN4QyOBc8RMiPGngd5p9ChF0viZoTxvqehE3g5I00uyJkmqfFTixDJf+0NFk9as/OlkznQJ9RNT+ZwK6gpFeG6+Tph3W0DEizwL9FZRfI45z9eJ3lmYzwvQN5pSMQKBgCLuGYxsNEC39+yyaQ4+vTQhI2dOfTcDXEgHWIDFiHpxVx0Sw8QJRYzuBJfLAkKAsiWdmGa29PpWbCnp3971xUqKGydCoK8N9Xc6ImWzdlpj0TK6EeDA4Ttbt6d6MFFg5zHD87ElAQJFYFUTurE5CLHJANvEyKnAXCekuAqdJCSbAoGAZOfEFVTwhXJwYeJnp6KXY2HOW7qKDQx9sLwI8kxVBZ8N7iqVJd6J8Cs/Rgd9XMTt8phTf1Fod7lZBMJ9HNfq6QOJk9/fzXitmiptDesGfsEwpqQ1zqiBcoAkKS/txmEyOhlKDpPok2KaKjdhTZV/yD18n+HCWvOrOwSl4bzTXfQ=";

        String       aliGatewayPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA02zL7cOKwHpE1zeqRxb9pS79bQ7qL4a0rAx7hLTy/U2oCn5BEJYCIkFGQCR45sPsR6/AasgjxJX2d/l6IxSgDIP8LrS7DG0CZm8hIEhGXN/wpZ0bMwG8zvL1EAmXXrki78vcYgib3yNl8/+TXcc1FXUaMSes8HvtLUoDcWBQrt3X+OAmk8dG25iOCny+YcFLwB+w4z4NgpgVr4t/L1exOKvoHucMsmngyO+v34uIzQQy1xfRnUeJQ2MPNPf/Nnm0JHH6AOwFUcQZEZgw9XiP6+fnqEyuQXi2TvO9E+hqU1Olg5Q6DaQCq8EdgY12+X3a4Ob6zntRLsFNQ/bxt49mtQIDAQAB";
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do","2018071160520849",aliGatewayPrivateKey,
                "json","GBK",aliGatewayPublicKey,"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode("7ee6be2e94df43b68d793cadc685TX53");
        AlipaySystemOauthTokenResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }


        AlipayUserInfoShareRequest request2 = new AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse response2 = alipayClient.execute(request2,response.getAccessToken());
        if(response2.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }


        AlipaySystemOauthTokenRequest request3 = new AlipaySystemOauthTokenRequest();
        request3.setGrantType("refresh_token");
        request3.setRefreshToken("authusrB62275809e2a94514a54b5fe0a1c64X53");
        AlipaySystemOauthTokenResponse response3 = alipayClient.execute(request3);
        if(response3.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }

    }


}
