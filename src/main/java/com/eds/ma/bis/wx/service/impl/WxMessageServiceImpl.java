package com.eds.ma.bis.wx.service.impl;

import com.alipay.api.internal.util.XmlUtils;
import com.eds.ma.bis.user.entity.UserWxMa;
import com.eds.ma.bis.user.entity.WxUser;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.sdk.common.event.EventType;
import com.eds.ma.bis.wx.service.IWxMessageService;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.email.SendMailUtil;
import com.eds.ma.util.AesException;
import com.eds.ma.util.WXBizMsgCrypt;
import com.xcrm.log.Logger;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Objects;

@Service
@Transactional
public class WxMessageServiceImpl implements IWxMessageService {

    private static Logger logger = Logger.getLogger(WxMessageServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private IWxService wxService;

    @Autowired
    private IUserService userService;

    @Override
    public void handleWxCallBackMessage(String msgSignature, String timestamp, String nonce, String xml, HttpServletResponse response) {
        try {
            WXBizMsgCrypt msg = new WXBizMsgCrypt(sysConfig.getWxMessageToken(),
                    sysConfig.getWxMessageAESKey(), sysConfig.getWxAppId());
            //消息解密
            String decryptXml = msg.decryptMsg(msgSignature, timestamp, nonce, xml);
            Element rootElement = XmlUtils.getRootElementFromString(decryptXml);
            logger.info("handleWxMessage decryptXml:" + decryptXml);
            String event = XmlUtils.getElementValue(rootElement, "Event");
            //如果是订阅,那么查询公众号用户信息
            if (Objects.equals(event, EventType.subscribe.name())
                    || Objects.equals(event, EventType.unsubscribe.name())) {
                String openId = XmlUtils.getElementValue(rootElement, "FromUserName");
                String toUserName = XmlUtils.getElementValue(rootElement, "ToUserName");
                WxUser wxUser = wxService.saveWxUser(openId, Objects.equals(event, EventType.subscribe.name()));
                //如果是关注,那么发送客服消息,成功领取优惠券
                if (Objects.equals(event, EventType.subscribe.name())
                        && wxUser.getSubscribeStatus()
                        && Objects.nonNull(wxUser.getWxUnionId())) {
                    UserWxMa dbUserWxMa = userService.queryUserWxMaByUnionId(wxUser.getWxUnionId());
                    if (Objects.isNull(dbUserWxMa) || BooleanUtils.isFalse(dbUserWxMa.getSubscribeCoupon())) {
                        replyTextMessage(response, "感谢您关注享测就测公众号,请前往小程序领取优惠券.",
                                toUserName, openId);
                    }
                }
            }else{
                String MsgId = XmlUtils.getElementValue(rootElement, "MsgId");
                String toUserName = XmlUtils.getElementValue(rootElement, "ToUserName");
                if(Objects.nonNull(MsgId) && Objects.equals(toUserName,"gh_930080ebd8b0")){
                    //如果是用户发送消息,且公众号是享测就测公众号,那么发送邮件到testtestgogogo@sina.com


                }
            }
        } catch (Exception e) {
            logger.error("handleWxCallBackMessage error", e);
        }
    }


    public void replyTextMessage(HttpServletResponse response, String content, String toUserName, String fromUserName) throws UnsupportedEncodingException {
        Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
        content = new String(content.getBytes(), "iso8859-1");
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>");
        sb.append("<CreateTime>" + createTime + "</CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA[" + content + "]]></Content>");
        sb.append("</xml>");
        String replyMsg = sb.toString();
        logger.info("replyTextMessage origin:{} . ", replyMsg);
        String returnvaleue = "";
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(sysConfig.getWxMessageToken(),
                    sysConfig.getWxMessageAESKey(), sysConfig.getWxAppId());
            returnvaleue = pc.encryptMsg(replyMsg, createTime.toString(), "easemob");
        } catch (AesException e) {
            logger.error("replyTextMessage error . ", e);
        }

        logger.info("replyTextMessage crypt:{} . ", returnvaleue);
        output(response, replyMsg);
    }

    /**
     * 工具类：回复微信服务器"文本消息"
     *
     * @param response
     * @param returnvaleue
     */
    private void output(HttpServletResponse response, String returnvaleue) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
            pw.flush();
        } catch (IOException e) {
            logger.error("output error . ", e);
        }
    }


}
