package com.eds.ma.quartz;

import com.eds.ma.bis.wx.entity.WxAccessToken;
import com.eds.ma.bis.wx.sdk.common.AccessToken;
import com.eds.ma.bis.wx.sdk.common.DefaultAccessTokenHolder;
import com.eds.ma.config.SysConfig;
import com.eds.ma.quartz.service.IJobService;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JobServiceImpl implements IJobService {
	
	protected Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	private BaseDaoSupport dao;

	@Autowired
	private SysConfig sysConfig;

	@Override
	public void wxRefreshToken() {
		Ssqb querySqb = Ssqb.create("com.eds.wx.common.queryWxExpiredToken");
		List<Map<String, Object>> wxAuthList = dao.findForMapList(querySqb);
		if (ListUtil.isNotEmpty(wxAuthList)) {
			for (Map<String, Object> tenantWxAuthMap : wxAuthList) {
				String appId = MapUtils.getString(tenantWxAuthMap, "appId");
				String appSecret = MapUtils.getString(tenantWxAuthMap, "appSecret");
				DefaultAccessTokenHolder accessTokenHolder = DefaultAccessTokenHolder.with(sysConfig.getWxAccessTokenUrl());
				accessTokenHolder.init(appId,appSecret);
				AccessToken accessToken = accessTokenHolder.getAccessToken();
				Long expired = accessToken.getExpiresIn();
				Date now = DateFormatUtils.getNow();
				WxAccessToken wxAccessToken = new WxAccessToken();
				wxAccessToken.setAppId(appId);
				wxAccessToken.setUpdated(now);
				wxAccessToken.setExpired(expired);
				wxAccessToken.setExpireDate(new Date(now.getTime()+expired*1000));
				wxAccessToken.setDataStatus(true);
				wxAccessToken.setToken(accessToken.getAccessToken());
				QueryBuilder updateWxAccessTokenQb = QueryBuilder.where(Restrictions.eq("appId",appId));
				dao.updateByQuery(wxAccessToken,updateWxAccessTokenQb);
			}
		}
	}

	@Override
	public void couponExpiredRunJob() {
		String now = DateFormatUtils.getStringToday();
		// 更新
		//更新会员优惠券的过期状态
		Ssqb updateMemberCouponSqb = Ssqb.create("com.xcrm.eds.quartz.updateUserCouponExpired")
				.setParam("now", DateFormatUtils.getStringToday());
		dao.updateByMybatis(updateMemberCouponSqb);
	}
}
