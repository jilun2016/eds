package com.eds.ma.bis.user.entity;import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;import com.xcrm.cloud.database.db.annotation.Table;import java.math.BigDecimal;import java.util.Date;/******************************************************************************* * javaBeans * t_eds_user_wallet --> UserWallet * <table explanation> * @author Generate Code Tool * @created 2018-01-08 10:38:33 *  */	@Table(tableName = "t_eds_user_wallet")public class UserWallet {	//field	/**	 * id 	 */	@PrimaryKeyField	private Long id;	/** 	 * 微信openId	 */	private Long userId;	/** 	 * 用户手机号	 */	private BigDecimal deposit;	/**	 * 昵称	 */	private BigDecimal balance;	/**	 * 短信验证码	 */	private String smsCode;	/**	 * 短信验证码失效时间	 */	private Date smsExpired;	/**	 * 创建时间 	 */	private Date created;    /**     * 更新时间     */    private Date updated;	public Long getId() {		return id;	}	public void setId(Long id) {		this.id = id;	}	public Long getUserId() {		return userId;	}	public void setUserId(Long userId) {		this.userId = userId;	}	public BigDecimal getDeposit() {		return deposit;	}	public void setDeposit(BigDecimal deposit) {		this.deposit = deposit;	}	public BigDecimal getBalance() {		return balance;	}	public void setBalance(BigDecimal balance) {		this.balance = balance;	}	public String getSmsCode() {		return smsCode;	}	public void setSmsCode(String smsCode) {		this.smsCode = smsCode;	}	public Date getSmsExpired() {		return smsExpired;	}	public void setSmsExpired(Date smsExpired) {		this.smsExpired = smsExpired;	}    public Date getCreated() {		return created;	}	public void setCreated(Date created) {		this.created = created;	}	public Date getUpdated() {		return updated;	}	public void setUpdated(Date updated) {		this.updated = updated;	}    @Override    public String toString() {        final StringBuilder sb = new StringBuilder("UserWallet{");        sb.append("id=").append(id);        sb.append(", userId=").append(userId);        sb.append(", deposit=").append(deposit);        sb.append(", balance=").append(balance);        sb.append(", smsCode='").append(smsCode).append('\'');        sb.append(", smsExpired=").append(smsExpired);        sb.append(", created=").append(created);        sb.append(", updated=").append(updated);        sb.append('}');        return sb.toString();    }}