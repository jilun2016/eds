package com.eds.ma.bis.order;

import org.apache.commons.lang.RandomStringUtils;

/**
 * OrderCodeCreater
 * @Author gaoyan
 * @Date: 2018/4/5 0005
 */
public class OrderCodeCreater {
	
	/**
	 * 7位随机数+订单创建时间
	 * @return
	 */
	public static String createTradeNO() {
		return RandomStringUtils.randomNumeric(7) + (System.currentTimeMillis());
	}
}
