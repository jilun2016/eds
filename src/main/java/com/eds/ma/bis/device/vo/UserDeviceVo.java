package com.eds.ma.bis.device.vo;

/**
 * 用户使用设备详情信息
 * @Author gaoyan
 * @Date: 2018/4/30
 */
public class UserDeviceVo {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备code
     */
    private String deviceCode;

    /**
     * 设备借次数
     */
    private Integer borrowTimes;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getBorrowTimes() {
        return borrowTimes;
    }

    public void setBorrowTimes(Integer borrowTimes) {
        this.borrowTimes = borrowTimes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdleDeviceVo{");
        sb.append("deviceId=").append(deviceId);
        sb.append(", deviceCode='").append(deviceCode).append('\'');
        sb.append(", borrowTimes=").append(borrowTimes);
        sb.append('}');
        return sb.toString();
    }
}
