package com.eds.ma.bis.user.service;

import com.eds.ma.bis.user.entity.User;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户基本信息service
 * @Author gaoyan
 * @Date: 2018/2/10
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    protected Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public void saveUser(User user) {
        dao.save(user);
    }

    @Override
    public User queryUserById(Long userId) {
        return dao.queryById(userId,User.class);
    }

    @Override
    public User queryUserByOpenId(String openId) {
        QueryBuilder queryUserQb = QueryBuilder.where(Restrictions.eq("openId",openId));
        return dao.query(queryUserQb,User.class);
    }

    @Override
    public void updateUser(User user) {
        dao.update(user);
    }
}
