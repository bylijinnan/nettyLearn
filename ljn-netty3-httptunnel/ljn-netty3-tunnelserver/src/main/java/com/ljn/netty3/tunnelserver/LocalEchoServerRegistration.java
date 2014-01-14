package com.ljn.netty3.tunnelserver;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;

/**
 * @author lijinnan
 * @date:2014-1-3
 */
public class LocalEchoServerRegistration {
    private final ChannelFactory factory = new DefaultLocalServerChannelFactory();
    private volatile Channel serverChannel;

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap(factory);
        EchoServerHandler handler = new EchoServerHandler();
        serverBootstrap.getPipeline().addLast("handler", handler);

        // Note that "myLocalServer" is the endpoint which was specified in
        // web.xml.
        serverChannel = serverBootstrap.bind(new LocalAddress("myLocalServer"));
        
    }

    public void stop() {
        serverChannel.close();
    }
    
    public void test() {
        System.out.println("test");
    }
}