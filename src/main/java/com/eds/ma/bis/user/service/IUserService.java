package com.eds.ma.bis.user.service;

import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.user.entity.AliUser;
import com.eds.ma.bis.user.entity.UserWxMa;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWallet;
import com.eds.ma.bis.user.vo.ContextUser;
import com.eds.ma.bis.user.vo.UserWalletVo;

import java.math.BigDecimal;

/**
 * 用户接口
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public interface IUserService {

    /**
     * 保存用户信息
     * @param userWxMa
     */
    void saveUserWxMa(UserWxMa userWxMa);

    /**
     * 查询用户信息
     * @param userId
     */
    User queryUserById(Long userId);

    /**
     * 通过openId查询用户信息
     * @param openId
     */
    ContextUser queryUserByOpenId(String openId);

    /**
     * 通过aliUid查询用户信息
     * @param aliUid
     */
    ContextUser queryUserByAliUid(String aliUid);

    /**
     * unionId
     * @param unionId
     */
    UserWxMa queryUserWxMaByUnionId(String unionId);

    /**
     * unionId
     * @param openId
     */
    UserWxMa queryUserWxMaByOpenId(String openId);

    /**
     * aliUid
     * @param aliUid
     */
    AliUser queryUserAliByAliUid(String aliUid);

    /**
     * 通过手机号查询用户信息
     * @param mobile
     */
    User queryUserByMobile(String mobile);

    /**
     * 更新用户信息
     * @param userWxMa
     */
    void updateUserWxMa(UserWxMa userWxMa);

    /**
     * 查询用户钱包信息
     * @param userId
     */
    UserWallet queryUserWalletByUserId(Long userId);

    /**
     * 查询用户钱包信息(加锁)
     * @param userId
     */
    UserWallet queryUserWalletByUserIdWithLock(Long userId);

    /**
     * 保存用户默认钱包信息
     * @param userId
     */
    UserWallet saveDefaultUserWallet(Long userId);

    /**
     * 累加用户钱包信息
     * @param userId
     * @param deposit
     * @param balance
     */
    void updateUserWallet(Long userId, BigDecimal deposit,BigDecimal balance);

    /**
     * 计算当前用户的押金
     * @param userId
     * @param defaultUnitDeposit
     * @return
     */
    BigDecimal caculateCurrentDeposit(Long userId,BigDecimal defaultUnitDeposit);

    /**
     * 查询用户使用中的设备
     * @param userId
     * @return
     */
    int queryUserRentingDeviceCount(Long userId);

    /**
     * 校验用户押金是否满足租借条件
     * @param userId
     * @return
     */
    Boolean checkUserRentDepositValid(Long userId);

    /**
     * 查询用户钱包
     * @param userId
     * @return
     */
    UserWalletVo queryUserWallet(Long userId);

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     * @param user
     * @param isNeedSms
     * @param smsCode
     */
    int walletWithdraw(ContextUser user, Boolean isNeedSms, String smsCode);

    /**
     * 用户退款失败回滚
     * @param payOrder
     */
    void revertRefundFailedRecord(PayOrder payOrder);

    /**
     * 保存微信用户信息
     *
     * @param unionId
     * @param openId
     * @param nickname
     * @param headimgurl
     * @param rawData
     */
    void asyncSaveOpenId(String unionId, String openId, String nickname, String headimgurl, String rawData);

    /**
     * 发送用户提现短信验证码
     * @param userId
     * @param mobile
     */
    void sendWithdrawSmsCode(Long userId, String mobile);

    /**
     * 查询用户租借次数
     * @param userId
     * @return
     */
    Integer queryUserRentTimes(Long userId);

    /**
     * 保存用户分销记录
     * 如果当前用户有进行中的分销记录,那么返回进行中的,否则创建
     * @param openId
     * @return
     */
    Long saveUserDist(String openId);

    /**
     * 分销绑定处理
     * @param distId
     * @param openId
     */
    void shareBindUserDist(Long distId, String openId);

    /**
     * 用户注册保存用户信息
     * @param appId
     * @param mobile
     */
    void saveUserForRegist(String appId, String mobile);
}
