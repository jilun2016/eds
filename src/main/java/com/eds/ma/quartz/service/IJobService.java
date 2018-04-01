package com.eds.ma.quartz.service;

public interface IJobService {
	/**
	 * 定时更新微信token
	 */
	void wxRefreshTokenJob() throws Exception;
}
