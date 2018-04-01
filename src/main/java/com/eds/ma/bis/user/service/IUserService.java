package com.eds.ma.bis.user.service;

import com.eds.ma.bis.user.entity.User;

/**
 * 打卡接口
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

}
