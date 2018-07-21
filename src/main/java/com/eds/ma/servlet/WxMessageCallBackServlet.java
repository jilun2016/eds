package com.eds.ma.servlet;


import com.eds.ma.bis.wx.service.IWxMessageService;
import com.eds.ma.bis.wx.service.IWxPayService;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import com.yunpian.sdk.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
            String msgSignature = request.getParameter("msg_signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String openId = request.getParameter("openId");
            logger.info("~~~~~~message_callback~~~~~~request_para:" + request.getQueryString());
            logger.info("~~~~~~~~message_callback~~~~~request_para_detail_xml:" + xml);

            wxMessageService.handleWxCallBackMessage(openId,msgSignature,timestamp,nonce,xml,response);
        } catch (Exception e) {
            logger.error("WxMessageCallBackServlet occurs exception ",e);
        }finally {
            //随机字符串
            String echostr = request.getParameter("echostr");
            try {
                response.getOutputStream().println(echostr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
