package com.eds.ma.bis.device.vo;

import java.math.BigDecimal;

/**
 * 店铺信息
 * @Author gaoyan
 * @Date: 2018/4/30
 */
public class SpDetailVo {

    /**
     * 商家id
     */
    private Long spId;

    /**
     * 商家距离
     */
    private String spDistance;

    /**
     * 设备精度
     */
    private BigDecimal spLng;

    /**
     * 设备纬度
     */
    private BigDecimal spLat;

    public Long getSpId() {
        return spId;
    }

    public void setSpId(Long spId) {
        this.spId = spId;
    }

    public String getSpDistance() {
        return spDistance;
    }

    public void setSpDistance(String spDistance) {
        this.spDistance = spDistance;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpDetailVo{");
        sb.append("spId=").append(spId);
        sb.append(", spDistance='").append(spDistance).append('\'');
        sb.append(", spLng=").append(spLng);
        sb.append(", spLat=").append(spLat);
        sb.append('}');
        return sb.toString();
    }
}
