package com.eds.ma.bis.order.entity;

import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;
import com.xcrm.cloud.database.db.annotation.Table;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/*******************************************************************************
 * javaBeans
 * t_eds_finance_income --> FinanceIncome
 * <table explanation>
 * @author Generate Code Tool
 * @created 2016-02-19 14:01:32
 * 
 */	
@Table(tableName = "t_eds_finance_income")
public class FinanceIncome implements Serializable {

	private static final long serialVersionUID = 1L;

	//field

	/** 
	 * 收入明细ID 
	 */
	@PrimaryKeyField
	private Long id;

	/**
	 * 交易编号 
	 */
	private String transCode;

	/** 
	 * 交易内容 
	 */
	private String content;

	/**
	 * 用户ID
	 */
	private Long userId;

	/** 
	 * 交易类型 
	 */
	private String transType;

	/** 
	 * 交易时间 
	 */
	private Date transTime;

	/**
	 * 金额
	 */
	private BigDecimal money;

	/**
	 * 用户openID
	 */
	private String openId;

	/** 
	 * 订单Code
	 */
	private String orderCode;

	/**
	 * 备注
	 */
	private String remark;

	/** 
	 * 删除标示 
	 */
	private Boolean dataStatus;

	//method


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Date getTransTime() {
		return transTime;
	}

	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Boolean dataStatus) {
		this.dataStatus = dataStatus;
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FinanceIncome{");
        sb.append("id=").append(id);
        sb.append(", transCode='").append(transCode).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", userId=").append(userId);
        sb.append(", transType='").append(transType).append('\'');
        sb.append(", transTime=").append(transTime);
        sb.append(", money=").append(money);
        sb.append(", openId='").append(openId).append('\'');
        sb.append(", orderCode='").append(orderCode).append('\'');
        sb.append(", remark='").append(remark).append('\'');
        sb.append(", dataStatus=").append(dataStatus);
        sb.append('}');
        return sb.toString();
    }
}