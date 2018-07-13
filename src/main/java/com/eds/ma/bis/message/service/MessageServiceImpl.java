package com.eds.ma.bis.message.service;

import com.alibaba.fastjson.JSON;
import com.eds.ma.bis.message.MessagePushTypeEnum;
import com.eds.ma.bis.message.entity.MessageRecord;
import com.eds.ma.bis.message.entity.SysMessageTmpl;
import com.eds.ma.bis.message.vo.SmsMessageContent;
import com.eds.ma.bis.sdk.sms.YunpianSmsSender;
import com.eds.ma.bis.wx.entity.WxAccessToken;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.util.HTTPUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsBatchSend;
import com.yunpian.sdk.model.SmsSingleSend;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService {

    protected Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private static final Integer SUCCESS_STATUS_CODE = 0;

    @Autowired
    private YunpianSmsSender yunpianSmsSender;

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private SysConfig sysConfig;

    @Override
    public void pushWxMaMessage(String openId, String tmplEvent) {
        SysMessageTmpl sysMessageTmpl = querySysMessageTmpl(tmplEvent);

        Map<String,String> result = new HashMap<>();
        Map<String,Object> templateParaMap = new HashMap<>();
        templateParaMap.put("touser",openId);
        templateParaMap.put("template_id",sysMessageTmpl.getMaTmplShortId());
        templateParaMap.put("form_id",123456);
        templateParaMap.put("data",sysMessageTmpl.getMaWxTmpl());

        //查询token
        QueryBuilder queryWxAccessTokenQb = QueryBuilder.where(Restrictions.eq("appId",sysConfig.getWxMaAppId()))
                .and(Restrictions.eq("dataStatus",1));
        WxAccessToken wxAccessToken = dao.query(queryWxAccessTokenQb,WxAccessToken.class);
        String resultJson = HTTPUtil.sendGetString(sysConfig.getWxMaTemplateUrl()+wxAccessToken.getToken(),templateParaMap);
        logger.info("MessageServiceImpl.pushWxMaMessage.result:{}",resultJson);
        Map<String,Object> resultJsonMap = HTTPUtil.Json2Map(resultJson);
        int errorCode = MapUtils.getIntValue(resultJsonMap,"errcode",-1);
        if(!Objects.equals(errorCode, -1)){
            logger.error("MessageServiceImpl.pushWxMaMessage failed.result:{}",resultJson);
            throw new BizCoreRuntimeException(BizErrorConstants.WX_MA_SESSION_QUERY_ERROR);
        }

    }

    @Override
    public void pushSmsMessage(SmsMessageContent smsMessageContent) {
        if(Objects.nonNull(smsMessageContent.getTmplEvent())
                && Objects.nonNull(smsMessageContent.getMobile())){
            String tmplEvent = smsMessageContent.getTmplEvent();
            SysMessageTmpl sysMessageTmpl = querySysMessageTmpl(tmplEvent);
            if(Objects.nonNull(sysMessageTmpl)){
                String msgContent = MessageFormat.format(sysMessageTmpl.getSmsTmpl(), smsMessageContent.getSmsParams());
                int msgSize = msgContent.length();
                Result<SmsBatchSend> result =yunpianSmsSender.sendSms(smsMessageContent.getMobile(),msgContent);
                if (result != null && result.getData() != null && ListUtil.isNotEmpty(result.getData().getData())) {
                    Date now = DateFormatUtils.getNow();
                    String msgRawData = JSON.toJSONString(smsMessageContent);
                    List<SmsSingleSend> smsSingleSends = result.getData().getData();
                    List<MessageRecord> messageRecords = new ArrayList<>();
                    int successConsumeCount = result.getData().getTotal_count();
                    for (SmsSingleSend smsSingleSend : smsSingleSends) {
                        MessageRecord messageRecord = new MessageRecord();
                        messageRecord.setPushType(MessagePushTypeEnum.MESSAGE_SMS.value());
                        messageRecord.setCreated(now);
                        messageRecord.setMsgRawData(msgRawData);
                        messageRecord.setMsgType(smsMessageContent.getTmplEvent());
                        if(SUCCESS_STATUS_CODE.equals(smsSingleSend.getCode())){
                            successConsumeCount = successConsumeCount + smsSingleSend.getCount();
                            messageRecord.setResult(1);
                            messageRecord.setErrorCode("0");
                            messageRecord.setErrmsg("ok");
                        }else{
                            messageRecord.setResult(0);
                            messageRecord.setErrorCode(smsSingleSend.getCode().toString());
                            messageRecord.setErrmsg(smsSingleSend.getMsg());
                        }
                        messageRecord.setMsgResultRawData(result.toString());
                        if(smsSingleSend.getSid() != null){
                            messageRecord.setMsgId(smsSingleSend.getSid().toString());
                        }
                        messageRecord.setMobile(smsSingleSend.getMobile());
                        messageRecord.setMessageSize(msgSize);
                        messageRecords.add(messageRecord);
                    }

                    if (ListUtil.isNotEmpty(messageRecords)) {
                        dao.batchSave(messageRecords, MessageRecord.class);
                    }
                }
            }
        }
    }

    private SysMessageTmpl querySysMessageTmpl(String tmplEvent){
        QueryBuilder querySysMsgQb = QueryBuilder.where(Restrictions.eq("tmplEvent",tmplEvent))
                .and(Restrictions.eq("dataStatus",1));
        return dao.query(querySysMsgQb,SysMessageTmpl.class);
    }
}
