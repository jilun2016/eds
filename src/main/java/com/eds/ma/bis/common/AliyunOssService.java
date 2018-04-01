package com.eds.ma.bis.common;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.eds.ma.config.SysConfig;
import com.xcrm.common.util.InputStreamUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Objects;
import java.util.UUID;


@Service
public class AliyunOssService {


    private static Logger logger = Logger.getLogger(AliyunOssService.class);

    private static String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";

    @Autowired
    private SysConfig sysConfig;

    public String upload2OssWithOutKey(byte[] file, String fileType) throws ClientException {

        OSSClient ossClient = new OSSClient(endpoint, sysConfig.getAccessKeyId(), sysConfig.getAccessKeySecret());
        String key = StringUtils.remove(UUID.randomUUID().toString(), '-');
        char[] keyArry = key.toCharArray();
        key = "eds" + "/" + keyArry[0] + "/" + keyArry[1] + "/" + key;
        if (Objects.nonNull(fileType)) {
            key = key + "." + fileType;
        }
        ossClient.putObject(sysConfig.getBucketName(), key, new ByteArrayInputStream(file));

        ossClient.shutdown();

        return key;
    }

    public File downloadOssFile(String key) throws ClientException {
        OSSClient ossClient = new OSSClient(endpoint, sysConfig.getAccessKeyId(), sysConfig.getAccessKeySecret());
        OSSObject ossObject = ossClient.getObject(sysConfig.getBucketName(), key);
        File ossFile = new File(key);
        inputstreamtofile(ossObject.getObjectContent(),ossFile);
        ossClient.shutdown();
        return ossFile;
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
