package com.eds.ma.bis.message.entity;


import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息推送记录表
 * @author gaoyan
 */
@Table(tableName = "t_b_message_record")
public class MessageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @PrimaryKeyField
    private Long id;

    /**
     * 微信第三方平台的appid
     */
    private String wxTPAppId;

    /**
     * 店铺公众号appid
     */
    private String authAppId;

    /**
     * 店铺唯一标识
     */
    private Long chainId;

    /**
     * 店铺ID
     */
    private Long tenantId;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 推送原始消息
     */
    private String msgRawData;

    /**
     * 推送回调原始消息
     */
    private String msgResultRawData;

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 是否免费：0-收费、1-免费
     */
    private Boolean isFree;

    /**
     * 消息总长度
     */
    private Integer messageSize;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 发送成功标识 -1:无意义 0:已发送 1:是 2:用户拒收 3:其他原因
     */
    private Integer result;

    /**
     * 消息推送类型
     */
    private String pushType;

    /**
     * 消息发送时间
     */
    private Date created;

    /**
     * 消息回调更新时间
     */
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWxTPAppId() {
        return wxTPAppId;
    }

    public void setWxTPAppId(String wxTPAppId) {
        this.wxTPAppId = wxTPAppId;
    }

    public String getAuthAppId() {
        return authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgRawData() {
        return msgRawData;
    }

    public void setMsgRawData(String msgRawData) {
        this.msgRawData = msgRawData;
    }

    public String getMsgResultRawData() {
        return msgResultRawData;
    }

    public void setMsgResultRawData(String msgResultRawData) {
        this.msgResultRawData = msgResultRawData;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean free) {
        isFree = free;
    }

    public Integer getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(Integer messageSize) {
        this.messageSize = messageSize;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageRecordNoChain{");
        sb.append("id=").append(id);
        sb.append(", wxTPAppId='").append(wxTPAppId).append('\'');
        sb.append(", authAppId='").append(authAppId).append('\'');
        sb.append(", chainId=").append(chainId);
        sb.append(", tenantId=").append(tenantId);
        sb.append(", msgType='").append(msgType).append('\'');
        sb.append(", msgRawData='").append(msgRawData).append('\'');
        sb.append(", msgResultRawData='").append(msgResultRawData).append('\'');
        sb.append(", msgId='").append(msgId).append('\'');
        sb.append(", mobile='").append(mobile).append('\'');
        sb.append(", isFree=").append(isFree);
        sb.append(", messageSize=").append(messageSize);
        sb.append(", errorCode='").append(errorCode).append('\'');
        sb.append(", errmsg='").append(errmsg).append('\'');
        sb.append(", result=").append(result);
        sb.append(", pushType='").append(pushType).append('\'');
        sb.append(", created=").append(created);
        sb.append(", updated=").append(updated);
        sb.append('}');
        return sb.toString();
    }
}