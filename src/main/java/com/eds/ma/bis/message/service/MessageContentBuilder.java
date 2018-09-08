package com.eds.ma.bis.message.service;

import com.xcrm.common.util.DateFormatUtils;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageContentBuilder {

    private static final String FIRST_CONTENT_COLOR = "#333333";

    private static final String CONTENT_COLOR = "#333333";

    /**
     * 检测记录通知
     * 您好,您有一份新的检测记录通知
     * 检测人:  昵称
     * 检测时间:
     * 检测结果：合格
     * 点击查看详情
     * @param nickName
     * @param checkResult 1:合格, 0:不合格
     * @param checkReportUrl 检测结果url
     * @return
     */
    public static Map<String, Object> buildDeviceCheckMessage(String nickName,Integer checkResult,String checkReportUrl) {
        String first = "您好,您有一份新的检测记录通知！\n";
        String remark = "\n点击查看详情";

        String now = DateFormatUtils.formatDate(new Timestamp(System.currentTimeMillis()),"yyyy-MM-dd HH:mm");

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("first", new WxMessageItem(first, FIRST_CONTENT_COLOR));
        contentMap.put("keyword1", new WxMessageItem(nickName, CONTENT_COLOR));
        contentMap.put("keyword2", new WxMessageItem(now, CONTENT_COLOR));
        contentMap.put("keyword3", new WxMessageItem(checkResult==1?"合格":"不合格", CONTENT_COLOR));
        contentMap.put("remark", new WxMessageItem(remark, FIRST_CONTENT_COLOR));
        return contentMap;
    }

    @Data
    private static class WxMessageItem {
        public WxMessageItem(String value, String color) {
            this.value = value;
            this.color = color;
        }
        private String value;
        private String color;
    }
}
