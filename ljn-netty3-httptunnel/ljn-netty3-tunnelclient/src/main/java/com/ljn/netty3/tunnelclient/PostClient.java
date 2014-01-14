package com.ljn.netty3.tunnelclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 

/**
 * 直接发送POST请求测试
 * @author lijinnan
 * @date:2014-1-3
 */
public class PostClient {

    private final String USER_AGENT = "Mozilla/5.0";

    // HTTP POST request
    private void sendPost() throws Exception {
 
        String url = "http://localhost:8088/netty3tunnelserver/netty-tunnel";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
 
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
 
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
 
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
 
        //print result
        System.out.println("response.toString() : " + response.toString());
 
    }
    
    public static void main(String[] args) throws Exception {
        new PostClient().sendPost();
    }
 
}

/*
输出结果：
Sending 'POST' request to URL : http://localhost:8088/netty3tunnelserver/netty-tunnel
Post parameters : sn=C02G8416DRJM&cn=&locale=&caller=&num=12345
Response Code : 200
response.toString() : sn=C02G8416DRJM&cn=&locale=&caller=&num=12345

可见，从Server端返回了Client发送过去的参数
 */
