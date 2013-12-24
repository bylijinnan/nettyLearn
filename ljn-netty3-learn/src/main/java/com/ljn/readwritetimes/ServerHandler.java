package com.ljn.readwritetimes;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.CharsetUtil;

public class ServerHandler extends SimpleChannelUpstreamHandler{
    
    private static final AtomicInteger COUNT =  new AtomicInteger(0);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        int count = COUNT.addAndGet(1);
        ChannelBuffer msg = (ChannelBuffer)e.getMessage();
        String msg_str = msg.toString(CharsetUtil.UTF_8);
        System.out.println("[server]messageReceived:" + count + ", " + msg_str);
        super.messageReceived(ctx, e);
    }
}

/*
接收次数则不确定：
[server]messageReceived:144, hello925
[server]messageReceived:145, hello926
[server]messageReceived:146, hello927hello928
[server]messageReceived:147, hello929
[server]messageReceived:148, hello930
......
[server]messageReceived:151, hello986[...]hello999
 */



