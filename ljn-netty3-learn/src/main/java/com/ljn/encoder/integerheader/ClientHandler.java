package com.ljn.encoder.integerheader;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 发送消息，并在消息前面加上header（一个整数），表示消息的长度
 * @author lijinnan
 * @date:2013-12-4 下午3:55:05  
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    private static AtomicInteger COUNT = new AtomicInteger(0);

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        e.getChannel().write(formMessage());
        System.out.println("in channelConnected: write ok");
    }
    
    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        e.getChannel().write(formMessage());
        System.out.println("in channelInterestChanged: write ok");
    }
    
    protected ChannelBuffer formMessage() {
        String msg = "hello" + COUNT.addAndGet(1);
        int len = msg.length();
        int headerLength = Integer.SIZE / 8;
        ChannelBuffer buff = ChannelBuffers.buffer(headerLength + len);
        buff.writeInt(len);
        buff.writeBytes(msg.getBytes());
        return buff;
    }
}
