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
    
    //为什么要用volatile同步？分析见下面
    private volatile Channel outboundChannel;
    
    
    public HexDumpProxyInboundHandler(ClientSocketChannelFactory factory,
            String remoteHost, int remotePort) {
        this.factory = factory;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    
    /*
     Client连接代理成功，准备发起请求之前，代理服务器要建立到真实服务器的Channel
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        
        System.out.println("channelOpen");
        final Channel inboundChannel = e.getChannel();
            
        //暂时不返回响应，连接到真正的server后再返回
        inboundChannel.setReadable(false);  
        ClientBootstrap clientBootstrap = new ClientBootstrap(factory);
        clientBootstrap.getPipeline().addLast("handler", new OutboundHandler(inboundChannel));
        
        //connect成功后，会从Executor里面取出一个线程（假设为threadA）来对应outboundChannel
        //这个线程，与执行当前这个方法的线程不一定是同一个，因此“outboundChannel”需要同步
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(remoteHost, remotePort));
        outboundChannel = future.getChannel();
        outboundChannel.setAttachment("ljn");
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
    
    //把接收到的Client的信息转发给真实服务器。为了做到这一点，需要建立一个连接到真实
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
    

    /**
     * 单纯的转发
     * 把变量“inboundChannel”起名为target或者destination可能更好理解
     * 在本例，作用是把接收到的真实服务器的消息，回写给Client
     */
    private static class OutboundHandler extends SimpleChannelUpstreamHandler {
        private Channel inboundChannel;

        public OutboundHandler(Channel inboundChannel) {
            this.inboundChannel = inboundChannel;
        }
        
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
                throws Exception {
            
            /*会输出“ljn”。由此可见，event.getChannel就是上面的“outboundChannel”
              从另一方面来看，本方法是“接收到真实服务器消息”时触发，那么与本事件相关联的channel
              当然是“outboundChannel”，因为proxy就是通过“outboundChannel”与真实服务器相连的
              也就是说，“threadA”从“outboundChannel”里读数据，而HexDumpProxyInboundHandler会往里面
              写数据，因此，需要同步
             */
            System.out.println(event.getChannel().getAttachment());
            
            ChannelBuffer message = (ChannelBuffer)event.getMessage();
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
