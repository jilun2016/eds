package com.eds.ma.bis.user.vo;

import lombok.Data;

/**
 * 用户上下文详情信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
@Data
public class ContextUser {
    /**
     * id
     */
    private Long userId;

    /**
     * 默认的昵称
     */
    private String nickname;

    /**
     * 默认的头像
     */
    private String headimgurl;

    /**
     * 支付宝小程序uid
     */
    private String aliUid;

    /**
     * 用户小程序openId
     */
    private String openId;

    /**
     * 用户微信UnionId
     */
    private String wxUnionId;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 微信昵称
     */
    private String wxNickname;

    /**
     * 微信头像
     */
    private String wxHeadimgurl;

    /**
     * 是否领取关注公众号优惠券
     */
    private Boolean subscribeCoupon;

    /**
     * 微信用户原始数据
     */
    private String wxUseRawData;

    /**
     * 阿里小程序用户昵称
     */
    private String aliNickname;

    /**
     * 阿里小程序用户头像
     */
    private String aliHeadimgurl;

    /**
     * 阿里小程序用户原始信息
     */
    private String aliUserRawData;

    /**
     * 阿里小程序token原始信息
     */
    private String aliTokenRawData;

}
