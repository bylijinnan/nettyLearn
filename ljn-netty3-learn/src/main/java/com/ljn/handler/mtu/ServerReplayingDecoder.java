package com.ljn.handler.mtu;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class ServerReplayingDecoder extends ReplayingDecoder<VoidEnum> {

    private boolean readLength;
    private int length;
    
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    private static final AtomicInteger BYTES_COUNT = new AtomicInteger(0);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer message = (ChannelBuffer)e.getMessage();
        int readableBytes = message.readableBytes();
        
        int time = COUNT.addAndGet(1);
        BYTES_COUNT.addAndGet(readableBytes);
        
        System.out.println("[server]messageReceived-" + time + ":" + readableBytes);
        
        super.messageReceived(ctx, e);
    }
    
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buf, VoidEnum state) throws Exception {
        if (!readLength) {
            length = buf.readInt();
            readLength = true;
            checkpoint();
        }

        if (readLength) {
            ChannelBuffer frame = buf.readBytes(length);
            System.out.println("[server]messageReceived Count=" + COUNT);
            System.out.println("[server]messageReceived BYTES_COUNT=" + BYTES_COUNT);
            readLength = false;
            checkpoint();
            return frame;
        }

        return null;
    }
    
}
/*
可以看到，由于TCP/IP的机制，Client会多次发送，Server端就多次接收，
且每次接收到的消息的长度可能会不一样，但最终会全部接收：
[server]messageReceived-1:1024
[server]messageReceived-2:2048
[server]messageReceived-3:2048
[server]messageReceived-4:3072
[server]messageReceived-5:4
[server]messageReceived Count=5
[server]messageReceived BYTES_COUNT=8196
*/
