package com.eds.ma.bis.device.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设备详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public class DeviceInfoVo {

    /**
     * 用户坐标
     */
    private BigDecimal userLng;

    /**
     * 用户坐标
     */
    private BigDecimal userLat;

    /**
     * 设备列表
     */
    private List<DeviceDetailVo> deviceDetailVoList;

    public static class DeviceDetailVo {

        /**
         * 设备id
         */
        private Long deviceId;

        /**
         * 商家名称
         */
        private String spName;

        /**
         * 商家地址
         */
        private String spAddress;

        /**
         * 商家营业时间
         */
        private String spBusinessTime;

        /**
         * 商家图片
         */
        private String spImage;

        /**
         * 商户坐标
         */
        private BigDecimal spLng;

        /**
         * 商户坐标
         */
        private BigDecimal spLat;

        /**
         * 商家设备状态
         */
        private String spDeviceStatus;

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

        public String getSpName() {
            return spName;
        }

        public void setSpName(String spName) {
            this.spName = spName;
        }

        public String getSpAddress() {
            return spAddress;
        }

        public void setSpAddress(String spAddress) {
            this.spAddress = spAddress;
        }

        public String getSpBusinessTime() {
            return spBusinessTime;
        }

        public void setSpBusinessTime(String spBusinessTime) {
            this.spBusinessTime = spBusinessTime;
        }

        public String getSpImage() {
            return spImage;
        }

        public void setSpImage(String spImage) {
            this.spImage = spImage;
        }

        public BigDecimal getSpLng() {
            return spLng;
        }

        public void setSpLng(BigDecimal spLng) {
            this.spLng = spLng;
        }

        public BigDecimal getSpLat() {
            return spLat;
        }

        public void setSpLat(BigDecimal spLat) {
            this.spLat = spLat;
        }

        public Integer getBorrowTimes() {
            return borrowTimes;
        }

        public void setBorrowTimes(Integer borrowTimes) {
            this.borrowTimes = borrowTimes;
        }

        public String getSpDeviceStatus() {
            return spDeviceStatus;
        }

        public void setSpDeviceStatus(String spDeviceStatus) {
            this.spDeviceStatus = spDeviceStatus;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DeviceDetailVo{");
            sb.append("deviceId=").append(deviceId);
            sb.append(", spName='").append(spName).append('\'');
            sb.append(", spAddress='").append(spAddress).append('\'');
            sb.append(", spBusinessTime='").append(spBusinessTime).append('\'');
            sb.append(", spImage='").append(spImage).append('\'');
            sb.append(", spLng=").append(spLng);
            sb.append(", spLat=").append(spLat);
            sb.append(", spDeviceStatus='").append(spDeviceStatus).append('\'');
            sb.append(", borrowTimes=").append(borrowTimes);
            sb.append('}');
            return sb.toString();
        }

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

    public List<DeviceDetailVo> getDeviceDetailVoList() {
        return deviceDetailVoList;
    }

    public void setDeviceDetailVoList(List<DeviceDetailVo> deviceDetailVoList) {
        this.deviceDetailVoList = deviceDetailVoList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceInfoVo{");
        sb.append("userLng=").append(userLng);
        sb.append(", userLat=").append(userLat);
        sb.append(", deviceDetailVoList=").append(deviceDetailVoList);
        sb.append('}');
        return sb.toString();
    }
}
