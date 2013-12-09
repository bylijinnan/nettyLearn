package com.ljn.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class Encoder extends OneToOneEncoder {
    // ...
    public static ChannelBuffer encodeMessage(Envelope message)
            throws IllegalArgumentException {
        // verify that no fields are set to null
 
        // version(1b) + type(1b) + payload length(4b) + payload(nb)
        int size = 6 + message.getPayload().length;
 
        ChannelBuffer buffer = ChannelBuffers.buffer(size);
        buffer.writeByte(message.getVersion());
        buffer.writeByte(message.getType());
        buffer.writeInt(message.getPayload().length);
        buffer.writeBytes(message.getPayload());
 
        return buffer;
    }
 
    @Override
    protected Object encode(ChannelHandlerContext channelHandlerContext,
                            Channel channel, Object msg) throws Exception {
        if (msg instanceof Envelope) {
            return encodeMessage((Envelope) msg);
        } else {
            return msg;
        }
    }
 
    // ...
}