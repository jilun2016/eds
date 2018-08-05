package com.eds.ma.quartz;

import com.eds.ma.config.SysConfig;
import com.eds.ma.quartz.service.IJobService;
import com.eds.ma.util.SystemProfileEnum;
import com.xcrm.common.util.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 定时刷新公众号授权的token
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Component
public class WxJob {

	private static final Logger logger = LoggerFactory.getLogger(WxJob.class);

	@Autowired
	private IJobService jobService;
	
	@Scheduled(cron="0 * * * * ?")
	public void wxRefreshTokenJob() {
		//每小时扫描时效
		if(true){
			logger.info("----------------wxRefreshTokenJob JOB end "+ DateFormatUtils.formatDate(new Date(),null)+"-------------------");
			jobService.wxRefreshToken();
		}
	}

	/**
	 * 每半个小时，查询退款中的订单的微信处理情况
	 */
	@Scheduled(cron="0 * * * * ?")
	public void refundOrderRunJob() {
		logger.debug("----------------refundOrderRunJob JOB开始 "+ DateFormatUtils.formatDate(new Date(),null)+"-------------------");
		jobService.wxRefundOrderJob();
		logger.debug("----------------refundOrderRunJob JOB结束 "+ DateFormatUtils.formatDate(new Date(),null)+"-------------------");
	}


	/**
	 * 零点执行优惠券过期操作
	 */
	@Scheduled(cron="0 0 * * * ?")
	public void couponExpiredRunJob() {
		//零点更新基础优惠券和会员优惠券过期操作
		if(true) {
			logger.info("----------------couponExpiredRunJob JOB开始 "+DateFormatUtils.formatDate(new Date(),null)+"-------------------");
			jobService.couponExpiredRunJob();
			logger.debug("----------------couponExpiredRunJob JOB结束 "+DateFormatUtils.formatDate(new Date(),null)+"-------------------");
		}
	}
}