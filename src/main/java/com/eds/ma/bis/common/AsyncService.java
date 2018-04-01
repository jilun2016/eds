package com.eds.ma.bis.common;

import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * 异步业务处理
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Component
public class AsyncService {
    private static Logger logger = Logger.getLogger(AsyncService.class);

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IUserService userService;


    /**
     * 保存微信用户信息
     * @param openId
     * @param nickname
     * @param headimgurl
     * @param rawData
     */
    @Async
    public void asyncSaveOpenId(String openId,String nickname,String headimgurl,String rawData) {
        logger.info("AsyncService.asyncSaveOpenId({},{},{},{})", openId,nickname,headimgurl,rawData);
        //保存微信用户信息
        User user = userService.queryUserByOpenId(openId);
        if(Objects.nonNull(user)){
            user.setUpdated(DateFormatUtils.getNow());
            user.setNickname(nickname);
            user.setHeadimgurl(headimgurl);
            user.setRawData(rawData);
            userService.updateUser(user);
        }else{
            userService.saveUser(user);
        }
    }

    /**
     * 异常调用返回Future
     * 
     * @param i
     * @return
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) {
        logger.info("asyncInvokeReturnFuture, parementer={}", i);
        Future<String> future;
        try {
            Thread.sleep(1000 * 1);
            future = new AsyncResult<String>("success:" + i);
        } catch (InterruptedException e) {
            future = new AsyncResult<String>("error");
        }
        return future;
    }

}