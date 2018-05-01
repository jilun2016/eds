package com.eds.ma.bis.device.vo;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SpDetailVo{");
        sb.append("spId=").append(spId);
        sb.append(", spDistance='").append(spDistance).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
