package com.ljn.encoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

/**
 * @author lijinnan
 * @date:2013-11-20 下午3:40:30  
 */

public class Decoder extends ReplayingDecoder<DecodingState> {


    private Envelope message;
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer, DecodingState state) throws Exception {
        
        //定义好消息的结构，每一阶段结束后，下一次读取就会从这个阶段的下一个阶段开始读
        switch (state) {
        case VERSION:
            this.message.setVersion((buffer.readByte()));
            checkpoint(DecodingState.TYPE);
        case TYPE:
            this.message.setType((buffer.readByte()));
            checkpoint(DecodingState.PAYLOAD_LENGTH);
        case PAYLOAD_LENGTH:
            int size = buffer.readInt();
            if (size <= 0) {
                throw new Exception("Invalid content size");
            }
            byte[] content = new byte[size];
            this.message.setPayload(content);
            checkpoint(DecodingState.PAYLOAD);
        case PAYLOAD:
            buffer.readBytes(this.message.getPayload(), 0,
                             this.message.getPayload().length);

            try {
                return this.message;
            } finally {
                this.reset();
            }
        default:
            throw new Exception("Unknown decoding state: " + state);
    }
    }
    
    private void reset() {
        checkpoint(DecodingState.VERSION);
        this.message = new Envelope();
    }

}
