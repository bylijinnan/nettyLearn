/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ljn.msg.lengthfield;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.io.Charsets;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;

/**
 * 传送的消息：
 * 具体数据的长度＋具体数据（如4data）
 * 符合length+content，decode时可用LengthFieldBasedFrameDecoder
 * 在代码中，“具体数据的长度”用一个byte表示
 */
public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                
                int maxFrameLength = 8192;
                int lengthFieldOffset = 0;
                int lengthAdjustment = 0;
                int lengthFieldLength = 1;
                int initialBytesToStrip = 1;
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip));
                pipeline.addLast("stringDecoder", new StringDecoder(Charsets.UTF_8));
                pipeline.addLast("serverHandler", new ServerHandler());
                
                return pipeline;
            }
        });

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new Server(port).run();
    }
}
