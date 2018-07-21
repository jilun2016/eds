package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.wx.service.IWxMessageService;
import com.eds.ma.config.SysConfig;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WxMessageServiceImpl implements IWxMessageService {

    private static Logger logger = Logger.getLogger(WxMessageServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Override
    public void handleWxCallBackMessage(String xml) {

    }
}
