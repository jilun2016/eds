package com.eds.ma.bis.order.entity;import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;import com.xcrm.cloud.database.db.annotation.Table;import java.io.Serializable;import java.math.BigDecimal;import java.util.Date;@Table(tableName = "t_t_eds_pay_order")public class PayOrder implements Serializable {	private static final long serialVersionUID = 1L;	//field	@PrimaryKeyField	private Long id;	/**	 * 用户id	 */	private Long userId;	/** 	 * 支付订单号 	 */	private String payCode;	/** 	 * 支付内容 	 */	private String title;	/**	 * 支付业务交易类型	 */	private String transType;	/**	 * 支付状态 	 */	private String payStatus;	/** 	 * 订单编号 	 */	private String orderCode;	/** 	 * 支付金额 	 */	private BigDecimal payMoney;	/** 	 * 创建时间 	 */	private Date created;	/**	 * 第三方通知code 	 */	private String notifyId;	/** 	 * 交易号 	 */	private String tradeNo;	/**	 * 买家用户号 	 */	private String buyerId;	/**	 * 订单总金额(回调) 	 */	private BigDecimal totalFee;	/** 	 * 实收金额 	 */	private BigDecimal cashFee;	/** 	 * 支付时间 	 */	private Date payTime;	/** 	 * 付款银行 	 */	private String bankType;	/** 	 * 卖家id(微信mch_id) 	 */	private String sellerId;	/**	 * 开发者应用id 	 */	private String appId;	/** 	 * 是否关注公众账号 	 */	private String isSubscribe;	/** 	 * 删除标示 	 */	private Boolean dataStatus;		//method	public Long getId() {		return id;	}	public void setId(Long id) {		this.id = id;	}	public Long getUserId() {		return userId;	}	public void setUserId(Long userId) {		this.userId = userId;	}	public String getPayCode() {		return payCode;	}		public void setPayCode(String payCode) {		this.payCode = payCode;	}	public String getTitle() {		return title;	}	public void setTitle(String title) {		this.title = title;	}    public String getTransType() {        return transType;    }    public void setTransType(String transType) {        this.transType = transType;    }    public String getPayStatus() {		return payStatus;	}	public void setPayStatus(String payStatus) {		this.payStatus = payStatus;	}	public String getOrderCode() {		return orderCode;	}	public void setOrderCode(String orderCode) {		this.orderCode = orderCode;	}	public BigDecimal getPayMoney() {		return payMoney;	}	public void setPayMoney(BigDecimal payMoney) {		this.payMoney = payMoney;	}	public Date getCreated() {		return created;	}	public void setCreated(Date created) {		this.created = created;	}	public String getNotifyId() {		return notifyId;	}	public void setNotifyId(String notifyId) {		this.notifyId = notifyId;	}	public String getTradeNo() {		return tradeNo;	}	public void setTradeNo(String tradeNo) {		this.tradeNo = tradeNo;	}	public String getBuyerId() {		return buyerId;	}	public void setBuyerId(String buyerId) {		this.buyerId = buyerId;	}	public BigDecimal getTotalFee() {		return totalFee;	}	public void setTotalFee(BigDecimal totalFee) {		this.totalFee = totalFee;	}	public BigDecimal getCashFee() {		return cashFee;	}	public void setCashFee(BigDecimal cashFee) {		this.cashFee = cashFee;	}	public Date getPayTime() {		return payTime;	}	public void setPayTime(Date payTime) {		this.payTime = payTime;	}	public String getBankType() {		return bankType;	}	public void setBankType(String bankType) {		this.bankType = bankType;	}	public String getSellerId() {		return sellerId;	}	public void setSellerId(String sellerId) {		this.sellerId = sellerId;	}	public String getAppId() {		return appId;	}	public void setAppId(String appId) {		this.appId = appId;	}	public String getIsSubscribe() {		return isSubscribe;	}	public void setIsSubscribe(String isSubscribe) {		this.isSubscribe = isSubscribe;	}	public Boolean getDataStatus() {		return dataStatus;	}	public void setDataStatus(Boolean dataStatus) {		this.dataStatus = dataStatus;	}    @Override    public String toString() {        final StringBuilder sb = new StringBuilder("PayOrder{");        sb.append("id=").append(id);        sb.append(", userId=").append(userId);        sb.append(", payCode='").append(payCode).append('\'');        sb.append(", title='").append(title).append('\'');        sb.append(", transType='").append(transType).append('\'');        sb.append(", payStatus='").append(payStatus).append('\'');        sb.append(", orderCode='").append(orderCode).append('\'');        sb.append(", payMoney=").append(payMoney);        sb.append(", created=").append(created);        sb.append(", notifyId='").append(notifyId).append('\'');        sb.append(", tradeNo='").append(tradeNo).append('\'');        sb.append(", buyerId='").append(buyerId).append('\'');        sb.append(", totalFee=").append(totalFee);        sb.append(", cashFee=").append(cashFee);        sb.append(", payTime=").append(payTime);        sb.append(", bankType='").append(bankType).append('\'');        sb.append(", sellerId='").append(sellerId).append('\'');        sb.append(", appId='").append(appId).append('\'');        sb.append(", isSubscribe='").append(isSubscribe).append('\'');        sb.append(", dataStatus=").append(dataStatus);        sb.append('}');        return sb.toString();    }}