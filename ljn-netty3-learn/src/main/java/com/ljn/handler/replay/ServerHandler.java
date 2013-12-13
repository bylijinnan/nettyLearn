package com.ljn.handler.replay;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @author lijinnan
 * @date:2013-12-13
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Message msg = (Message) e.getMessage();
        System.out.println("server messageReceived:" + msg);
        
        /*
         正确的输出：
         server messageReceived:Message [type=1000, id=A0001, params=[a, bb, ccc]]
         */
    }
}
