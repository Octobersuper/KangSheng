package com.zcf.mahjong.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.*;

public class Utlis {
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };
    /**
     * 发送post请求
     */
    public static String doPost(String url, Map<String, String> param, String encoding, RequestConfig requestConfig) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                UrlEncodedFormEntity entity = null;
                // 判断编码参数是否为空或者空串
                if (StringUtils.isNotBlank(encoding)) {
                    // 不为空,则设置编码
                    entity = new UrlEncodedFormEntity(paramList, encoding);
                } else {
                    // 为空,不设置编码
                    entity = new UrlEncodedFormEntity(paramList);
                }
                // 模拟表单
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            if (response.containsHeader("Location")) {
                // 包含重定向地址
                Map<String,String> map=new HashMap<String,String>();
                for (Header str : response.getAllHeaders()) {
                    if (StringUtils.isNotBlank(str.getValue())) {
                        map.put(str.getName(), str.getValue());
                    }
                }
                String resultString1 = new ObjectMapper().writeValueAsString(map);
                resultString= resultString1;
            } else {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        return resultString;
    }
    public static String doPostJson(String url, String json, RequestConfig requestConfig) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            // httpPost.setHeader(name, value);
            // 创建请求内容
            // 模拟表单(默认UTF-8的编码)
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            if (response.containsHeader("Location")) {
                // 包含重定向地址
                Map<String,String> map=new HashMap<String,String>();
                for (Header str : response.getAllHeaders()) {
                    if (StringUtils.isNotBlank(str.getValue())) {
                        map.put(str.getName(), str.getValue());
                    }
                }
                String resultString1 = new ObjectMapper().writeValueAsString(map);
                resultString= resultString1;
            } else {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        return resultString;
    }
    //MD5加密
    public static String MD5Encode(String origin, String sault) {
        String resultString = null;
        try {
            if (StringUtils.isNotBlank(sault)) {
                origin = origin + sault;
            }
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {

        }
        return resultString;
    }
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    //生成随机数
    public static String genRandomNum(int len) {
        // 35是因为数组是从0开始的，26个字母+10个数字
        final int maxNum = 35;
        int i; // 生成的随机数
        int count = 0; // 生成的随机数的长度
        char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        StringBuffer sb = new StringBuffer("");
        Random r = new Random();
        while (count < len) {
            // 生成随机数，取绝对值，防止生成负数，

            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1

            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count++;
            }
        }

        return sb.toString();
    }
    public static String getPostData(HttpServletRequest request) {
        // 读取请求内容
        BufferedReader br = null;
        InputStream is = null;
        InputStreamReader isr = null;
        String content = "";
        try {
            is = request.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // 将资料解码
            content = URLDecoder.decode(sb.toString(), HTTP.UTF_8);
        } catch (IOException e) {

            // TODO Auto-generated catch block

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return content;
    }
}
