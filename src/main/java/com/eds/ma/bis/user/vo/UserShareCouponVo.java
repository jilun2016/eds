package com.eds.ma.bis.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 分享优惠券的关注用户信息
 * @Author gaoyan
 * @Date: 2018/7/22
 */
@Data
public class UserShareCouponVo {

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

    /**
     * 分享点击时间
     */
    private Date shardDateTime;
}
