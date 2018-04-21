package com.eds.ma.servlet;


import com.eds.ma.bis.wx.service.IWxPayService;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信回调支付处理
 * @Author gaoyan
 * @Date: 2018/4/5
 */
@WebServlet(urlPatterns="/v1/pay/callback")
public class WxCallBackServlet extends HttpServlet {

    private static final long serialVersionUID = -8685285401859800066L;

    private static Logger logger = Logger.getLogger(WxCallBackServlet.class);

    @Autowired
    private IWxPayService wxPayService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("----WxCallBackServlet.doGet()----");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String xml = InputStreamUtils.InputStreamTOString(request.getInputStream(), "UTF-8");
            logger.info("~~~~~~~~~~~~~~~~~~callback_xml:" + xml);
            wxPayService.optWxPayCallback(xml);
        } catch (Exception e) {
            logger.error("wxPayCallback occurs exception ",e);
        }
    }

}
