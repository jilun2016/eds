package com.eds.ma.bis.common.service;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class EdsConfigServiceImpl implements IEdsConfigService {

    protected Logger log = LoggerFactory.getLogger(EdsConfigServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public EdsConfig queryEdsConfig() {
        return dao.query(QueryBuilder.create(),EdsConfig.class);
    }

    @Override
    public BigDecimal queryEdsConfigDeposit() {
        EdsConfig edsConfig = queryEdsConfig();
        return edsConfig.getDeposit();
    }
}
