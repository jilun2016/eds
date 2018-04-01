package com.eds.ma.resource.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 微信押金预支付
 * @Author gaoyan
 * @Date: 2018/4/1
 */
public class DeviceDepositPrePayRequest {


    /**
     * 设备id
     */
    @NotNull(message = "设备id不允许为空")
    private Long deviceId;

    /**
     * 用户精度
     */
    @NotNull(message = "用户精度不允许为空")
    private BigDecimal userLng;

    /**
     * 用户纬度
     */
    @NotNull(message = "用户纬度不允许为空")
    private BigDecimal userLat;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public BigDecimal getUserLng() {
        return userLng;
    }

    public void setUserLng(BigDecimal userLng) {
        this.userLng = userLng;
    }

    public BigDecimal getUserLat() {
        return userLat;
    }

    public void setUserLat(BigDecimal userLat) {
        this.userLat = userLat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WxPrePayRequest{");
        sb.append("deviceId=").append(deviceId);
        sb.append(", userLng=").append(userLng);
        sb.append(", userLat=").append(userLat);
        sb.append('}');
        return sb.toString();
    }
}