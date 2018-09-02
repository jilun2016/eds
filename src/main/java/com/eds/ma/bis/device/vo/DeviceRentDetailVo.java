package com.eds.ma.bis.device.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待租借设备详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
@Data
public class DeviceRentDetailVo {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备原始code
     */
    private String deviceOriginCode;

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
}
