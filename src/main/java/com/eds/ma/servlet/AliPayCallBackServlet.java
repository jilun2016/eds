package com.eds.ma.servlet;


import com.eds.ma.bis.wx.service.IAliPayService;
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
import java.util.Map;

/**
 * 微信回调支付处理
 * @Author gaoyan
 * @Date: 2018/4/5
 */
@WebServlet(urlPatterns="/v1/ali/pay/callback")
public class AliPayCallBackServlet extends HttpServlet {

    private static final long serialVersionUID = -8685285401859800066L;

    private static Logger logger = Logger.getLogger(AliPayCallBackServlet.class);

    @Autowired
    private IAliPayService aliPayService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("----AliPayCallBackServlet.doGet()----");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, String[]> requestParams = request.getParameterMap();
            logger.info("~~~~~~~~~~~~~~~~~~callback_params:" + requestParams);
            aliPayService.optAliPayCallback(requestParams,response);
        } catch (Exception e) {
            logger.error("AliPayCallBack occurs exception ",e);
        }
    }

}
