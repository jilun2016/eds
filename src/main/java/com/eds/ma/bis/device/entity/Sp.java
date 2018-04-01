package com.eds.ma.bis.device.entity;import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;import com.xcrm.cloud.database.db.annotation.Table;import java.math.BigDecimal;import java.util.Date;/******************************************************************************* * javaBeans * t_eds_sp --> Sp * <table explanation> * @author Generate Code Tool * @created 2018-01-08 10:38:33 *  */	@Table(tableName = "t_eds_sp")public class Sp {	//field	/**	 * id 	 */	@PrimaryKeyField	private Long id;	/** 	 * 商家名称	 */	private String spName;	/** 	 * 商家地址	 */	private String spAddress;	/**	 * 商家营业时间	 */	private String spBusinessTime;	/**	 * 商家图片	 */	private String spImage;	/**	 * 商户坐标	 */	private BigDecimal spLng;	/**	 * 商户坐标	 */	private BigDecimal spLat;	/**	 * 创建时间 	 */	private Date created;	/** 	 * 1:正常 0:删除	 */	private Boolean dataStatus;	public Long getId() {		return id;	}	public void setId(Long id) {		this.id = id;	}	public String getSpName() {		return spName;	}	public void setSpName(String spName) {		this.spName = spName;	}	public String getSpAddress() {		return spAddress;	}	public void setSpAddress(String spAddress) {		this.spAddress = spAddress;	}	public String getSpBusinessTime() {		return spBusinessTime;	}	public void setSpBusinessTime(String spBusinessTime) {		this.spBusinessTime = spBusinessTime;	}	public String getSpImage() {		return spImage;	}	public void setSpImage(String spImage) {		this.spImage = spImage;	}	public BigDecimal getSpLng() {		return spLng;	}	public void setSpLng(BigDecimal spLng) {		this.spLng = spLng;	}	public BigDecimal getSpLat() {		return spLat;	}	public void setSpLat(BigDecimal spLat) {		this.spLat = spLat;	}	public Date getCreated() {		return created;	}	public void setCreated(Date created) {		this.created = created;	}	public Boolean getDataStatus() {		return dataStatus;	}	public void setDataStatus(Boolean dataStatus) {		this.dataStatus = dataStatus;	}	@Override	public String toString() {		final StringBuilder sb = new StringBuilder("Sp{");		sb.append("id=").append(id);		sb.append(", spName='").append(spName).append('\'');		sb.append(", spAddress='").append(spAddress).append('\'');		sb.append(", spBusinessTime='").append(spBusinessTime).append('\'');		sb.append(", spImage='").append(spImage).append('\'');		sb.append(", spLng=").append(spLng);		sb.append(", spLat=").append(spLat);		sb.append(", created=").append(created);		sb.append(", dataStatus=").append(dataStatus);		sb.append('}');		return sb.toString();	}}