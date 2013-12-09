package com.ljn.handler.integerheader;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.ljn.util.Helper;

/**
MESSAGE FORMAT
 ==============

 Offset:  0        4                   (Length + 4)
          +--------+------------------------+
 Fields:  | Length | Actual message content |
          +--------+------------------------+
          
 * @author lijinnan
 * @date:2013-12-4 下午3:30:00  
 */
public class IntegerHeaderFrameDecoder extends FrameDecoder {

    /**
     *  从中解析出真正的消息部分，并返回（字符串形式）
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer) throws Exception {
        
        System.out.println("enter decode");
        if (buffer.readableBytes() < 4) {
            return null;
        }
        buffer.markReaderIndex();
        int len = buffer.readInt();
        if (buffer.readableBytes() < len) {
            buffer.resetReaderIndex();
            return null;
        }
        System.out.println("len = " + len);
        ChannelBuffer msg = buffer.readBytes(len);
        Helper.printAsBytesWithoutChange(msg);
        return msg.toString(CharsetUtil.UTF_8);
        
    }

}
