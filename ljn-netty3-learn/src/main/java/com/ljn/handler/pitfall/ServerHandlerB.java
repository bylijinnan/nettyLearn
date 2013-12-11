package com.ljn.handler.pitfall;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.ljn.util.Helper;

public class ServerHandlerB extends SimpleChannelUpstreamHandler{

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        System.out.println("B-messageReceived:");
        ChannelBuffer message = (ChannelBuffer)e.getMessage();
        Helper.printAsBytesWithoutChange(message);
    }
}
