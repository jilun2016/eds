package com.eds.ma.resource.request;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 用户手机号请求
 * @Author gaoyan
 * @Date: 2018/4/1
 */
public class UserPhoneRequest {

    @NotEmpty(message="登录code不允许为空")
    private String code;

    @NotEmpty(message="加密数据不允许为空")
    private String encryptedData;

    @NotEmpty(message="加密算法的初始向量不允许为空")
    private String iv;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserPhoneRequest{");
        sb.append("code='").append(code).append('\'');
        sb.append(", encryptedData='").append(encryptedData).append('\'');
        sb.append(", iv='").append(iv).append('\'');
        sb.append('}');
        return sb.toString();
    }
}