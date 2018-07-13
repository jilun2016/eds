package com.eds.ma.bis.device.vo;

import lombok.Data;

/**
 * 设备常见问题
 */

@Data
public class DeviceFaqInfoVo {

    /**
     * 常见问题标题
     */
    private String title;

    /**
     * 常见问题说明
     */
    private String detail;

}
