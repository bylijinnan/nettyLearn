package com.ljn.encoder.integerheader;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 打印接收到的字符串
 * @author lijinnan
 * @date:2013-12-4 下午3:41:28  
 */
public class ServerStringHandler extends SimpleChannelUpstreamHandler{

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        String msg = (String)e.getMessage();
        System.out.println("msg = " + msg);
    }
}
