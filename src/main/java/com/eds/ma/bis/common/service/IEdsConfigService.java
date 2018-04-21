package com.eds.ma.bis.common.service;


import com.eds.ma.bis.common.entity.EdsConfig;

import java.math.BigDecimal;

/**
 * eds配置service
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public interface IEdsConfigService {

    /**
     * 查询通用配置信息
     */
    EdsConfig queryEdsConfig();

    /**
     * 查询默认单台设备押金金额
     */
    BigDecimal queryEdsConfigDeposit();
}
