package com.eds.ma.bis.sdk.sms;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.constant.YunpianConstant;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsBatchSend;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 云片短信通道
 * 短信api技术文档参考: https://www.yunpian.com/doc/zh_CN/domestic/batch_send.html
 * @Author gaoyan
 * @Date: 2017/10/13
 */
@Component
public class YunpianSmsSender {

	private YunpianClient client;

	private static final Logger log = LoggerFactory.getLogger(YunpianSmsSender.class);

	public void buildClient() {
		String APIKEY = "b6397314c07c2ce9c0a7f12f96f121f0";
		client = new YunpianClient(APIKEY).init();
	}

	/**
	 * 短信发送,mobile支持逗号分隔
	 * @param mobile
	 * @param messageContent
	 * @return
	 */
	public Result<SmsBatchSend> sendSms(String mobile, String messageContent) {
		try {
			buildClient();
			Map<String, String> param = client.newParam(2);
			param.put(YunpianConstant.MOBILE, mobile);
			param.put(YunpianConstant.TEXT, messageContent);
			Result<SmsBatchSend> r = client.sms().batch_send(param);
			boolean sendResult = Objects.equals(r.getCode(),0);
			if(BooleanUtils.isFalse(sendResult)){
				log.error("YunpianSmsSender.sendSms failed.mobile:{},contents:{},result is:{}"
						,mobile,messageContent,r);
			}
			return r;
		}catch (Exception e){
			log.error("YunpianSmsSender send sms error.mobile:"+mobile+",contents:"+ messageContent,e);
			return null;
		}finally {
			client.close();
		}
	}

}
