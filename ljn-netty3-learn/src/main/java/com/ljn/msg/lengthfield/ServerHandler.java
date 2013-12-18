package com.ljn.msg.lengthfield;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerHandler extends SimpleChannelUpstreamHandler{

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        String msg = (String) e.getMessage();
        System.out.println("[server]" + msg);
    }
}
