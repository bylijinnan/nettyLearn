package com.ljn.handler.pitfall;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerHandlerA extends SimpleChannelUpstreamHandler{

    /**
     * 本意：把HandlerA去掉，让HandlerB接着处理messageReceived
     * 如API所说，像下面的写法是不行的
     * 如果pipeline里面只有一个handler，必须先add后remove
     * 也就是把下面代码的1和2调换一下顺序
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        System.out.println("enter A-messageReceived");
        
        ctx.getPipeline().remove(this); //1.
        ctx.getPipeline().addLast("handlerB", new ServerHandlerB());    //2.
        ctx.sendUpstream(e);
    }
}
