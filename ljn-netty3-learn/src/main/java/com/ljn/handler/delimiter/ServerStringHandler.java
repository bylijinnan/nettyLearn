package com.ljn.handler.delimiter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.CharsetUtil;

/**
 * 打印接收到的字符串
 * @author lijinnan
 * @date:2013-12-4 下午3:41:28  
 */
public class ServerStringHandler extends SimpleChannelUpstreamHandler{

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer buff = (ChannelBuffer)e.getMessage();
        String msg = (String)buff.toString(CharsetUtil.UTF_8);
        
        //String s = "abc\n"; escapeJava(s) 会输出转义字符abc\n，而不是换行符
        String msg_escape = StringEscapeUtils.escapeJava(msg);  
        System.out.println("msg = " + msg_escape);
    }
}
