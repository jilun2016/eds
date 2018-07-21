package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.wx.service.IWxMessageService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.util.WXBizMsgCrypt;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@Transactional
public class WxMessageServiceImpl implements IWxMessageService {

    private static Logger logger = Logger.getLogger(WxMessageServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Override
    public void handleWxCallBackMessage(String msgSignature, String timestamp, String nonce, String xml, HttpServletResponse response) {
        try {
            WXBizMsgCrypt msg = new WXBizMsgCrypt(sysConfig.getWxMessageToken(),
                    sysConfig.getWxMessageAESKey(), sysConfig.getWxAppId());
            //消息解密
            String decryptXml = msg.decryptMsg(msgSignature, timestamp, nonce, xml);
            logger.info("handleWxMessage decryptXml:"+decryptXml);
        }catch (Exception e){
            logger.error("handleWxCallBackMessage error",e);
        }

    }
}
