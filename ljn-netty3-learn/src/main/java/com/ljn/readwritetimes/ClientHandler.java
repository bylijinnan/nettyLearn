package com.ljn.readwritetimes;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelUpstreamHandler {

    private static final AtomicInteger COUNT =  new AtomicInteger(0);
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        for (int i = 0; i < 1000; i++) {
            String msg = "hello" + i;
            ctx.getChannel().write(ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
        }
    }
    
    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
            throws Exception {
        int count = COUNT.addAndGet(1);
        System.out.println("[client]write:" + count);
        super.writeComplete(ctx, e);
    }
}


/*
输出如下，确实发送了1000次
[client]write:1
...............
[client]write:999
[client]write:1000
 */

