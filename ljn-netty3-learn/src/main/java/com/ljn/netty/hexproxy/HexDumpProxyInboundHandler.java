package com.ljn.netty.hexproxy;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

/**
 * @author lijinnan
 * @date:2013-12-3 上午10:01:54  
 */
public class HexDumpProxyInboundHandler extends SimpleChannelUpstreamHandler  {

    private ClientSocketChannelFactory factory;
    private String remoteHost;
    private int remotePort;
    
    //????为什么要用volatile
    private volatile Channel outboundChannel;
    
    public HexDumpProxyInboundHandler(ClientSocketChannelFactory factory,
            String remoteHost, int remotePort) {
        this.factory = factory;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        System.out.println("channelOpen");
        final Channel inboundChannel = e.getChannel();
            
        //暂时不返回响应，连接到真正的server后再返回
        inboundChannel.setReadable(false);  
        ClientBootstrap clientBootstrap = new ClientBootstrap(factory);
        clientBootstrap.getPipeline().addLast("handler", new OutboundHandler(e.getChannel()));
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(remoteHost, remotePort));
        outboundChannel = future.getChannel();
        future.addListener(new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("success");
                    inboundChannel.setReadable(true);
                } else {
                    System.out.println("fail");
                    inboundChannel.close();
                }
            }
        });
    }
    
    //这个代理会把接收到的信息转发给真实服务器，为了做到这一点，需要建立一个连接到真实
    //服务器的Channel（也就是上面代码的“outboundChannel”）并往里面写数据
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer message = (ChannelBuffer)e.getMessage();
        System.out.println(">>> " + ChannelBuffers.hexDump(message));
        outboundChannel.write(message);
    }
    
    //means inboundChannel.closed, so leaves outboundChannel to close
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        closeOnFlush(e.getChannel());
    }
    
    //===================================
    
    private static class OutboundHandler extends SimpleChannelUpstreamHandler {
        private Channel inboundChannel;

        public OutboundHandler(Channel inboundChannel) {
            this.inboundChannel = inboundChannel;
        }
        
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
                throws Exception {
            ChannelBuffer message = (ChannelBuffer)e.getMessage();
            System.out.println("<<< " + ChannelBuffers.hexDump(message));
            
            //文本格式：
            //System.out.println("<<< " + message.toString(Charset.forName("UTF-8")));
            inboundChannel.write(message);
        }
        
        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
                throws Exception {
            closeOnFlush(inboundChannel);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
                throws Exception {
            e.getCause().printStackTrace();
            closeOnFlush(e.getChannel());
        }
    }

    static void closeOnFlush (Channel ch) {
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener((ChannelFutureListener.CLOSE));
        }
    }
}
