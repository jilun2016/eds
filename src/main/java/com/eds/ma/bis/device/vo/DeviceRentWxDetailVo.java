package com.eds.ma.bis.device.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租借中设备微信详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
@Data
public class DeviceRentWxDetailVo {

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 昵称
     */
    private String nickName;
}
