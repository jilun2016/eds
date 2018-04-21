package com.eds.ma.bis.device.vo;

import java.math.BigDecimal;

/**
 * 待租借设备详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public class DeviceRentDetailVo {

    /**
     * 设备id
     */
    private Long deviceId;

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
     * 设备精度
     */
    private BigDecimal deviceLng;

    /**
     * 设备纬度
     */
    private BigDecimal deviceLat;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getSpId() {
        return spId;
    }

    public void setSpId(Long spId) {
        this.spId = spId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public BigDecimal getDeviceLng() {
        return deviceLng;
    }

    public void setDeviceLng(BigDecimal deviceLng) {
        this.deviceLng = deviceLng;
    }

    public BigDecimal getDeviceLat() {
        return deviceLat;
    }

    public void setDeviceLat(BigDecimal deviceLat) {
        this.deviceLat = deviceLat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceRentDetailVo{");
        sb.append("deviceId=").append(deviceId);
        sb.append(", spId=").append(spId);
        sb.append(", deviceStatus='").append(deviceStatus).append('\'');
        sb.append(", deviceLng=").append(deviceLng);
        sb.append(", deviceLat=").append(deviceLat);
        sb.append('}');
        return sb.toString();
    }
}
