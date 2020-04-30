package com.zcf.mahjong.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Feigesms {

	 public static void SendSms(String code,String phone) {
	        try {
	            CloseableHttpClient client = null;
	            CloseableHttpResponse response = null;
	            try {
	                List<BasicNameValuePair> formparams = new ArrayList<>();
	                formparams.add(new BasicNameValuePair("Account","18288020882"));
				    formparams.add(new BasicNameValuePair("Pwd","284a62d65f82a5c20901fae7c"));//登录后台 首页显示
				    formparams.add(new BasicNameValuePair("Content","您的验证码是"+code+",请在两分钟内完成验证。"));
				    formparams.add(new BasicNameValuePair("Mobile",phone));
				    formparams.add(new BasicNameValuePair("SignId","148777"));//登录后台 添加签名获取id
				   
	                HttpPost httpPost = new HttpPost("http://api.feige.ee/SmsService/Send");
	                httpPost.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
	                client = HttpClients.createDefault();
	                response = client.execute(httpPost);
	                HttpEntity entity = response.getEntity();
	                String result = EntityUtils.toString(entity);
	                System.out.println(result);
	            } finally {
	                if (response != null) {
	                    response.close();
	                }
	                if (client != null) {
	                    client.close();
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
