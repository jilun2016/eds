package com.eds.ma.bis.user.entity;import com.xcrm.cloud.database.db.annotation.PrimaryKeyField;import com.xcrm.cloud.database.db.annotation.Table;import lombok.Data;import java.util.Date;/******************************************************************************* * javaBeans * t_eds_user --> User * <table explanation> * @author Generate Code Tool * @created 2018-01-08 10:38:33 *  */@Data@Table(tableName = "t_eds_user_reserve_record")public class UserReserveRecord {	/**	 * id 	 */	@PrimaryKeyField	private Long id;	/**	 * 用户手机号	 */	private String mobile;	/**	 * 预约店铺	 */	private Long reserveSpId;	/**	 * 创建时间	 */	private Date reserveTime;	/**	 * 预约通知时间	 */	private Date reserveNoticeTime;	/**	 * 1:正常 0:删除	 */	private Boolean dataStatus;}