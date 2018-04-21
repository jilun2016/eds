package com.eds.ma.bis.user.service;

import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWallet;
import com.eds.ma.bis.user.vo.PayRefundVo;
import com.eds.ma.bis.user.vo.UserWalletVo;
import com.eds.ma.bis.wx.PayStatusEnum;
import com.eds.ma.bis.wx.service.IWxRefundPayService;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 用户基本信息service
 *
 * @Author gaoyan
 * @Date: 2018/2/10
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    protected Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IWxRefundPayService wxRefundPayService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void saveUser(User user) {
        dao.save(user);
    }

    @Override
    public User queryUserById(Long userId) {
        return dao.queryById(userId, User.class);
    }

    @Override
    public User queryUserByOpenId(String openId) {
        QueryBuilder queryUserQb = QueryBuilder.where(Restrictions.eq("openId", openId));
        return dao.query(queryUserQb, User.class);
    }

    @Override
    public void updateUser(User user) {
        dao.update(user);
    }

    @Override
    public UserWallet queryUserWalletByUserId(Long userId) {
        QueryBuilder queryUserWalletQb = QueryBuilder.where(Restrictions.eq("userId", userId));
        UserWallet userWallet = dao.query(queryUserWalletQb, UserWallet.class);
        if (Objects.isNull(userWallet)) {
            userWallet = saveDefaultUserWallet(userId);
        }
        return userWallet;
    }

    @Override
    public UserWallet queryUserWalletByUserIdWithLock(Long userId) {
        Ssqb queryWalletSqb = Ssqb.create("com.eds.user.queryUserWalletByUserIdWithLock")
                .setParam("userId", userId);
        UserWallet userWallet = dao.findForObj(queryWalletSqb, UserWallet.class);
        if (Objects.isNull(userWallet)) {
            userWallet = saveDefaultUserWallet(userId);
        }
        return userWallet;
    }

    @Override
    public UserWallet saveDefaultUserWallet(Long userId) {
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(userId);
        userWallet.setCreated(DateFormatUtils.getNow());
        dao.save(userWallet);
        return userWallet;
    }

    @Override
    public void updateUserWallet(Long userId, BigDecimal deposit, BigDecimal balance) {
        Ssqb updateDeviceSqb = Ssqb.create("com.eds.user.updateUserWallet")
                .setParam("userId", userId)
                .setParam("deposit", deposit)
                .setParam("balance", balance);
        dao.updateByMybatis(updateDeviceSqb);
    }

    @Override
    public User checkUserExist(String openId) {
        //对用户信息,钱包进行校验
        User user = queryUserByOpenId(openId);
        if (Objects.isNull(user) || Objects.isNull(user.getId())) {
            throw new BizCoreRuntimeException(BizErrorConstants.USER_NOT_EXIST_ERROR);
        }
        return user;
    }

    @Override
    public UserWalletVo queryUserWallet(String openId) {
        User user = checkUserExist(openId);
        UserWallet userWallet = queryUserWalletByUserId(user.getId());
        UserWalletVo userWalletVo = new UserWalletVo();
        userWalletVo.setUserId(userWallet.getUserId());
        userWalletVo.setBalance(userWallet.getBalance());
        userWalletVo.setDeposit(userWallet.getDeposit());
        return userWalletVo;
    }

    @Override
    public int walletWithdraw(String openId) {
        int result = 1;
        User user = checkUserExist(openId);
        Long userId = user.getId();
        UserWallet userWallet = queryUserWalletByUserIdWithLock(userId);
        BigDecimal balance = userWallet.getBalance();
        BigDecimal deposit = userWallet.getDeposit();
        //校验总金额大于0 ,可以进行提现
        BigDecimal allRefundMoney = balance.add(deposit);
        if (allRefundMoney.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_ZERO_ERROR);
        }

        List<PayOrder> payOrderList = orderService.queryToRefundPayOrder(openId);
        if (ListUtil.isNotEmpty(payOrderList)) {
            //校验提现的金额应该大于钱包中的余额
            BigDecimal allWxRefundMoney = payOrderList.stream()
                    .map(PayOrder::getPayMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (allRefundMoney.compareTo(allWxRefundMoney) > 0) {
                throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_MONEY_COMPARE_ERROR);
            }

            List<PayOrder> depositPayOrderList = null;
            List<PayOrder> balancePayOrderList = null;
            //发起余额和押金两笔退款订单
            if (deposit.compareTo(BigDecimal.ZERO) > 0) {
                depositPayOrderList = payOrderList.stream()
                        .filter(a -> Objects.equals(a.getTransType(), TransTypeEnum.S_JYLX_YJCZ.value()))
                        .collect(Collectors.toList());
                if (ListUtil.isEmpty(depositPayOrderList)) {
                    throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_MONEY_COMPARE_ERROR);
                }

                BigDecimal allWxRefundDepositMoney = depositPayOrderList.stream()
                        .map(PayOrder::getPayMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (deposit.compareTo(allWxRefundDepositMoney) > 0) {
                    throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_MONEY_COMPARE_ERROR);
                }
            }

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                balancePayOrderList = payOrderList.stream()
                        .filter(a -> Objects.equals(a.getTransType(), TransTypeEnum.S_JYLX_YECZ.value()))
                        .collect(Collectors.toList());
                if (ListUtil.isEmpty(balancePayOrderList)) {
                    throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_MONEY_COMPARE_ERROR);
                }

                BigDecimal allWxRefundBalanceMoney = balancePayOrderList.stream()
                        .map(PayOrder::getPayMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (balance.compareTo(allWxRefundBalanceMoney) > 0) {
                    throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_MONEY_COMPARE_ERROR);
                }
            }

            //校验ok,钱包扣除待退款的金额,发起微信退款
            updateUserWallet(userId, deposit.negate(), balance.negate());
            //更新历史支付订单状态 TRADE_FINISHED(交易结束，不可退款)
            List<PayOrder> updatePayOrderList = payOrderList.stream().map(payOrder -> {
                PayOrder updatePayOrder = new PayOrder();
                updatePayOrder.setId(payOrder.getId());
                updatePayOrder.setPayStatus(PayStatusEnum.TRADE_FINISHED.value());
                return updatePayOrder;
            }).collect(Collectors.toList());
            dao.batchUpdate(updatePayOrderList, PayOrder.class);
            //发起多笔支付退款
            //押金退款
            if (ListUtil.isNotEmpty(depositPayOrderList)) {
                result = asyncPayRefund(depositPayOrderList, deposit);
            }
            //余额退款
            if (ListUtil.isNotEmpty(balancePayOrderList)) {
                result = asyncPayRefund(balancePayOrderList, balance);
            }
        } else {
            throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_ZERO_ERROR);
        }
        return result;
    }

    @Override
    public void revertRefundFailedRecord(PayOrder payOrder) {
        if (Objects.nonNull(payOrder)) {
            Long userId = payOrder.getUserId();
            //查询退款支付订单是否存在
            PayOrder revertPayOrder = orderService.queryPayOrderByPayCode(payOrder.getPayCode());
            if (Objects.nonNull(revertPayOrder)) {

                //回滚订单交易状态
                PayOrder updatePayOrder = new PayOrder();
                updatePayOrder.setPayStatus(PayStatusEnum.TRADE_SUCCESS.value());
                QueryBuilder updateQb = QueryBuilder.where(Restrictions.eq("id", revertPayOrder.getId()))
                        .and(Restrictions.eq("userId", updatePayOrder.getUserId()))
                        .and(Restrictions.eq("payStatus", PayStatusEnum.TRADE_FINISHED.value()));
                dao.updateByQuery(updatePayOrder, updateQb);

                //回滚用户钱包
                if (Objects.equals(updatePayOrder.getTransType(), TransTypeEnum.S_JYLX_YJCZ.value())) {
                    updateUserWallet(userId, updatePayOrder.getPayMoney(), null);
                }
                if (Objects.equals(updatePayOrder.getTransType(), TransTypeEnum.S_JYLX_YECZ.value())) {
                    updateUserWallet(userId, null, updatePayOrder.getPayMoney());
                }
            }
        }
    }


    /**
     * 保存微信用户信息
     *
     * @param openId
     * @param nickname
     * @param headimgurl
     * @param rawData
     */
    @Async
    @Override
    public void asyncSaveOpenId(String openId, String nickname, String headimgurl, String rawData) {
        log.info("UserService.asyncSaveOpenId({},{},{},{})", openId, nickname, headimgurl, rawData);
        //保存微信用户信息
        Date now = DateFormatUtils.getNow();
        User user = queryUserByOpenId(openId);
        if (Objects.nonNull(user)) {
            user.setUpdated(now);
            user.setNickname(nickname);
            user.setHeadimgurl(headimgurl);
            user.setRawData(rawData);
            updateUser(user);
        } else {
            user = new User();
            user.setCreated(now);
            user.setHeadimgurl(headimgurl);
            user.setNickname(nickname);
            user.setOpenId(openId);
            user.setRawData(rawData);
            saveUser(user);
            //初始化钱包
            if (Objects.nonNull(user.getId())) {
                saveDefaultUserWallet(user.getId());
            }
        }
    }

    /**
     * 异步退款处理
     * @param refundPayOrderPool
     * @param toRefundMoney
     * @return 退款部分失败 0 退款成功 :1
     */
    @Override
    public int asyncPayRefund(List<PayOrder> refundPayOrderPool, BigDecimal toRefundMoney) {
        List<Integer> resultList = new ArrayList<>();
        CompletableFuture[] cfs = null;
        List<PayRefundVo> payRefundVos = new ArrayList<>();
        BigDecimal leftToRefundMoney = toRefundMoney;
        for (PayOrder payOrder : refundPayOrderPool) {
            if (leftToRefundMoney.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal payOrderMoney = payOrder.getPayMoney();
                BigDecimal refundMoney = null;
                if (leftToRefundMoney.compareTo(payOrderMoney) > 0) {
                    refundMoney = payOrderMoney;
                    leftToRefundMoney = leftToRefundMoney.subtract(payOrderMoney);
                } else {
                    refundMoney = leftToRefundMoney;
                    leftToRefundMoney = BigDecimal.ZERO;
                }
                PayRefundVo payRefundVo = new PayRefundVo();
                payRefundVo.setPayOrder(payOrder);
                payRefundVo.setRefundMoney(refundMoney);
                payRefundVos.add(payRefundVo);
            } else {
                break;
            }
        }

        if(ListUtil.isNotEmpty(payRefundVos)){
            cfs = payRefundVos.stream()
                    .map(payRefundVo-> CompletableFuture.supplyAsync(()->{
                        try {
                            wxRefundPayService.submiteRefund(payRefundVo.getPayOrder(), payRefundVo.getRefundMoney());
                            return 1;
                        }catch (Exception e){
                            revertRefundFailedRecord(payRefundVo.getPayOrder());
                            return 0;
                        }
                    }, taskExecutor)
                            .whenComplete((v, e) -> {//如需获取任务完成先手顺序，此处代码即可
                                resultList.add(v);
                            }))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();//封装后无返回值，必须自己whenComplete()获取
        }

        return BooleanUtils.toInteger(resultList.stream().noneMatch(result -> result == 0));
    }
}
