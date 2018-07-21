package com.eds.ma.bis.user.vo;

/**
 * 用户详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public class UserInfoVo {

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headimgurl;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserCouponVo{");
        sb.append("openId='").append(openId).append('\'');
        sb.append(", nickName='").append(nickName).append('\'');
        sb.append(", headimgurl='").append(headimgurl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
