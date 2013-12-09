package com.ljn.handler.delimiter;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler {


    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        String msg = "ABC\nDEF\r\n";
        ChannelBuffer buff = ChannelBuffers.buffer(msg.length());
        buff.writeBytes(msg.getBytes());
        e.getChannel().write(buff);
        System.out.println("in channelConnected: write ok");
    }
}
