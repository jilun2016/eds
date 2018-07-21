package com.eds.ma.bis.user.service;

import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWallet;
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
     * @param user
     */
    void saveUser(User user);

    /**
     * 查询用户信息
     * @param userId
     */
    User queryUserById(Long userId);

    /**
     * 通过openId查询用户信息
     * @param openId
     */
    User queryUserByOpenId(String openId);

    /**
     * unionId
     * @param unionId
     */
    User queryUserByUnionId(String unionId);

    /**
     * 更新用户信息
     * @param user
     */
    void updateUser(User user);

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
     * 校验用户是否存在
     * @param openId
     * @return
     */
    User checkUserExist(String openId);

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
     * @param user
     * @return
     */
    UserWalletVo queryUserWallet(User user);

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     * @param user
     * @param isNeedSms
     * @param smsCode
     */
    int walletWithdraw(User user, Boolean isNeedSms, String smsCode);

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
     * @param user
     * @param mobile
     */
    void sendWithdrawSmsCode(User user, String mobile);

    /**
     * 查询用户租借次数
     * @param id
     * @return
     */
    Integer queryUserRentTimes(Long userId);
}
