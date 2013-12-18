package com.ljn.handler.mtu;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author lijinnan
 * @date:2013-12-18
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer buff = (ChannelBuffer)e.getMessage();
        String msg = buff.toString(CharsetUtil.UTF_8);
        System.out.println("[server]ServerHandler:" + msg.length());
        
        //输出到文件，方便观察。打印到console时，可能看到的是一片空白，但实际上是有输出的
        FileUtils.writeStringToFile(new File("C:/Users/lijinnan/Desktop/tmp.txt"), msg, CharsetUtil.UTF_8);
        System.out.println("[server]ServerHandler:finished");
        
    }

}

/*输出：
[server]ServerHandler:8192
[server]ServerHandler:finished
*/