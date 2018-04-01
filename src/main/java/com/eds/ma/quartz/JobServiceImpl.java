package com.eds.ma.quartz;

import com.eds.ma.bis.wx.JsAPIs;
import com.eds.ma.bis.wx.WxTokenTypeEnum;
import com.eds.ma.bis.wx.entity.AccessToken;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.quartz.service.IJobService;
import com.eds.ma.rest.common.CommonConstants;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

@Service
public class JobServiceImpl implements IJobService {
	
	protected Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	private IWxService wxService;

	@Autowired
	private SysConfig sysConfig;

	@Override
	public void wxRefreshTokenJob() throws Exception {
		String appId = sysConfig.getWxAppId();
		AccessToken jsToken = wxService.queryWxJsAccessToken(appId);
		if (Objects.nonNull(jsToken)) {
			long updated = jsToken.getUpdated().getTime();
			long expired = updated + Long.parseLong((jsToken.getExpired() * 1000) + "");
			long now = System.currentTimeMillis();
			if (expired <= now) {
				//已经过期了
				JsAPIs jsAPIs = JsAPIs.with(sysConfig.getWxAccessTokenUrl(),sysConfig.getJsSdkTicketUrl());
				Map accessTokenMap = jsAPIs.getWxAccessToken(appId, sysConfig.getWxAppSecret());
				if (MapUtils.isNotEmpty(accessTokenMap)) {
					String access_token = MapUtils.getString(accessTokenMap, CommonConstants.ACCESS_TOKEN);
					Integer expires_in = MapUtils.getInteger(accessTokenMap, CommonConstants.EXPIRES_IN);
					Map jsTicketMap = jsAPIs.getWxJsSdkTicket(access_token);
					if (MapUtils.isNotEmpty(jsTicketMap)) {
						String jsTicket = MapUtils.getString(jsTicketMap, "ticket");

						AccessToken accessToken = wxService.queryAccessTokenByType(WxTokenTypeEnum.ACCESS_TOKEN, appId);
						accessToken.setExpired(expires_in);
						accessToken.setToken(access_token);
						accessToken.setUpdated(new Timestamp(System.currentTimeMillis()));
						wxService.updateAccessToken(accessToken);

						jsToken.setExpired(expires_in);
						jsToken.setToken(jsTicket);
						jsToken.setUpdated(new Timestamp(System.currentTimeMillis()));
						wxService.updateAccessToken(jsToken);
					}
				}
			}
		}
	}
}
