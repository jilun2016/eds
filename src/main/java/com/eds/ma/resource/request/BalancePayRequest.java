package com.eds.ma.resource.request;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 用户余额充值请求
 * @Author gaoyan
 * @Date: 2018/4/1
 */
public class BalancePayRequest {

    @NotNull(message="充值金额不允许为空")
    @Range(min = 0,message = "充值金额无效")
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BalancePayRequest{");
        sb.append("balance=").append(balance);
        sb.append('}');
        return sb.toString();
    }
}