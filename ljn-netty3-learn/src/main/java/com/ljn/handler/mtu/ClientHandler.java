package com.ljn.handler.mtu;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.CharsetUtil;

/**
 * 发送8192个“A”，并在ChannelBuffer写入长度（用int表示，占4个字节），因此实际发送的为8196个字节
 * @author lijinnan
 * @date:2013-12-18
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    private static final int LENGTH = 8192;

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        ChannelBuffer buff = ChannelBuffers.buffer(4 + LENGTH);
        buff.writeBytes(new byte[4]);
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append("A");
        }
        byte[] bytes = sb.toString().getBytes(CharsetUtil.UTF_8);
        buff.writeBytes(bytes);
        buff.setInt(0, buff.writerIndex() - 4);
        
        ChannelFuture future = e.getChannel().write(buff);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("in channelConnected: write ok");
            }
        });
    }

}
