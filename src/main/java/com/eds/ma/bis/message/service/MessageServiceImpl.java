package com.eds.ma.bis.message.service;

import com.alibaba.fastjson.JSON;
import com.eds.ma.bis.message.MessagePushTypeEnum;
import com.eds.ma.bis.message.entity.MessageRecord;
import com.eds.ma.bis.message.entity.SysMessageTmpl;
import com.eds.ma.bis.message.vo.SmsMessageContent;
import com.eds.ma.bis.sdk.sms.YunpianSmsSender;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService {

    protected Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private static final Integer SUCCESS_STATUS_CODE = 0;

    @Autowired
    private YunpianSmsSender yunpianSmsSender;

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public void pushWxMaMessage(String openId, String tmplEvent) {
        SysMessageTmpl sysMessageTmpl = querySysMessageTmpl(tmplEvent);

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
