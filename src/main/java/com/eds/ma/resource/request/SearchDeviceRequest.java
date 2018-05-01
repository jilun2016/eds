package com.eds.ma.resource.request;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;

/**
 * 查询设备列表
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public class SearchDeviceRequest{

    /**
     * 用户精度
     */
    @QueryParam("userLng")
    @NotNull(message = "用户精度不允许为空")
    private BigDecimal userLng;

    /**
     * 用户纬度
     */
    @QueryParam("userLat")
    @NotNull(message = "用户纬度不允许为空")
    private BigDecimal userLat;

    /**
     * 设备距离范围 单位 米 m
     * 默认5km
     */
    @QueryParam("distance")
    @Range(min=0,message = "设备距离范围参数无效")
    private Integer distance;

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

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchDeviceRequest{");
        sb.append("userLng=").append(userLng);
        sb.append(", userLat=").append(userLat);
        sb.append(", distance=").append(distance);
        sb.append('}');
        return sb.toString();
    }
}