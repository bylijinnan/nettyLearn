package com.ljn.handler.pitfall;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler {


    /**
     * 向Server发送一条字符串消息
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        String msg = "Hello, World";
        ChannelBuffer buff = ChannelBuffers.buffer(msg.length());
        buff.writeBytes(msg.getBytes());
        e.getChannel().write(buff);
        System.out.println("in channelConnected: write ok");
    }
}
