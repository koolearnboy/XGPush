package com.jp.xgpush.service;

import com.tencent.xinge.Style;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 信鸽推送服务
 * @author xiaojx
 */
public class XGMessagePushService {
    private final static String URL = "https://openapi.xg.qq.com/v3/push/app";
    private String appId;
    private String secretKey;
    private SimpleDateFormat format = new SimpleDateFormat();

    public XGMessagePushService(String appId, String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
    }

    public static void main(String[] args) {
        XGMessagePushService push = new XGMessagePushService("614070d48a6a8","81b33518227f9e0bf1d8ef928d426431");
        Message message = new Message();
        message.setTitle("来自服务端的消息");
        message.setContent("God is a girl "+new Date());
        Map<String,String> params = new HashMap<>();
        params.put("key","value");
        message.setParams(params);
        try {
            JSONObject rs = push.sendSingleDevice(message,new String[]{"aceb0cc79e752a7a731b263e563170fc2d326d45"});
            System.out.println("发送结果为:"+rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 单/多设备推送透传消息
     * @param msg 需要传递的消息
     * @param token 需要接收消息的设备token数组
     * @return ret_code = -1 代表失败
     */
    public JSONObject sendSingleDevice(Message msg, String[] token) throws IOException {
        JSONObject ret = new JSONObject().put("ret_code","-1");

        String input = appId + ":" + secretKey;
        String base64AuthString = new BASE64Encoder().encode(input.getBytes());
        System.out.println("base64AuthString:" + base64AuthString);

        if (token!=null && msg!=null){
            JSONObject param = new JSONObject();
            param.put("audience_type", token.length>1?"token_list":"token");
            param.put("token_list", token);
            param.put("platform", "android");
            //消息类型为透传消息
            param.put("message_type", "message");
            // 0,1(响铃),1(震动),1(可清除),1
            Style msgStyle = new Style(0, 1, 1, 1, 1);
            param.put("message", new JSONObject().put("title",msg.getTitle())
                    .put("content",msg.getContent()));
            //设置自定义参数
            param.put("custom_content",msg.getParams());
            System.out.println(param);

            // 创建HttpClientBuilder
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            // HttpClient
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

            String result = "";
            HttpPost httpPost;
            HttpResponse httpResponse;
            HttpEntity entity;
            httpPost = new HttpPost(URL);
            httpPost.setHeader("Authorization","Basic "+base64AuthString);
            httpPost.setHeader("Content-Type","application/json");
            httpPost.setEntity(new StringEntity(param.toString(),"UTF-8"));
            try {
                httpResponse = closeableHttpClient.execute(httpPost);
                entity = httpResponse.getEntity();
                if( entity != null ){
                    result = EntityUtils.toString(entity);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭连接
            closeableHttpClient.close();
            //
            ret = new JSONObject(result);
        }
        return ret;
    }

    public static class Message {
        private String title;
        private String content;
        private JSONObject m_params;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public JSONObject getParams() {
            return m_params;
        }

        public void setParams(Map<String, String> params) {
            this.m_params = new JSONObject(params);
        }
    }

    public String getAppId() {
        return appId;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
