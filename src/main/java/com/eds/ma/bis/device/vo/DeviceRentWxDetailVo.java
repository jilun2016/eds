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
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备原始code
     */
    private Long deviceOriginCode;

    /**
     * 设备GPS位置信息消息编号
     */
    private Long deviceGpsNo;

    /**
     * 商户id
     */
    private Long spId;

    /**
     * 设备状态
     * {@link com.eds.ma.bis.device.DeviceStatusEnum}
     */
    private String deviceStatus;

    /**
     * 订单状态
     * {@link com.eds.ma.bis.device.OrderStatusEnum}
     */
    private String orderStatus;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 租借时间
     */
    private Date rentTime;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 店铺位置精度
     */
    private BigDecimal spLng;

    /**
     * 店铺位置纬度
     */
    private BigDecimal spLat;

    /**
     * 补偿数据A1整数位
     */
    private Long adjustParamA1;

    /**
     * 补偿数据A2小数位
     */
    private Long adjustParamA2;

    /**
     * 补偿数据A3小数位
     */
    private Long adjustParamA3;

    /**
     * 补偿数据B1整数位
     */
    private Long adjustParamB1;

    /**
     * 补偿数据B2小数位
     */
    private Long adjustParamB2;

    /**
     * 补偿数据B3小数位
     */
    private Long adjustParamB3;
}
