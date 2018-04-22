package com.eds.ma.bis.user.service;

import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWallet;
import com.eds.ma.bis.user.vo.UserWalletVo;

import java.math.BigDecimal;
import java.util.List;

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
     * 查询用户钱包
     * @param openId
     * @return
     */
    UserWalletVo queryUserWallet(String openId);

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     * @param openId
     * @param smsCode
     */
    int walletWithdraw(String openId, String smsCode);

    /**
     * 用户退款失败回滚
     * @param payOrder
     */
    void revertRefundFailedRecord(PayOrder payOrder);

    /**
     * 保存微信用户信息
     *
     * @param openId
     * @param nickname
     * @param headimgurl
     * @param rawData
     */
    void asyncSaveOpenId(String openId, String nickname, String headimgurl, String rawData);

    /**
     * 异步退款处理
     * @param refundPayOrderPool
     * @param toRefundMoney
     * @return 退款部分失败 0 退款成功 :1
     */
    int asyncPayRefund(List<PayOrder> refundPayOrderPool, BigDecimal toRefundMoney);

    /**
     * 发送用户提现短信验证码
     * @param openId
     * @param mobile
     */
    void sendWithdrawSmsCode(String openId, String mobile);
}
