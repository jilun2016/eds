package com.eds.ma.servlet;


import com.eds.ma.bis.wx.service.IWxMessageService;
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
@WebServlet(urlPatterns="/v1/wx/message/callback")
public class WxMessageCallBackServlet extends HttpServlet {

    private static final long serialVersionUID = -8685285401859800066L;

    private static Logger logger = Logger.getLogger(WxMessageCallBackServlet.class);

    @Autowired
    private IWxMessageService wxMessageService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("----WxMessageCallBackServlet.doGet()----");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            String xml = InputStreamUtils.InputStreamTOString(request.getInputStream(), "UTF-8");
            logger.info("~~~~~~~~~~~WxMessageCallBackServlet callback_xml:" + xml);
            wxMessageService.handleWxCallBackMessage(xml);
        } catch (Exception e) {
            logger.error("WxMessageCallBackServlet occurs exception ",e);
        }
    }

}
