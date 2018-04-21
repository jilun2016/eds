package com.eds.ma.bis.user.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包信息
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public class UserWalletVo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 押金
     */
    private BigDecimal deposit;

    /**
     * 余额
     */
    private BigDecimal balance;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserWalletVo{");
        sb.append("userId=").append(userId);
        sb.append(", deposit=").append(deposit);
        sb.append(", balance=").append(balance);
        sb.append('}');
        return sb.toString();
    }
}
