package com.ljn.netty.hexproxy;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

/**
 * @author lijinnan
 * @date:2013-12-3 上午9:58:50  
 */
public class HexDumpProxyPipelineFactory implements ChannelPipelineFactory {

    private ClientSocketChannelFactory factory;
    private String remoteHost;
    private int remotePort;
    
    public HexDumpProxyPipelineFactory(ClientSocketChannelFactory factory,
            String remoteHost, int remotePort) {
        this.factory = factory;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("handler", new HexDumpProxyInboundHandler(factory, remoteHost, remotePort));
        return pipeline;
    }
    
}
