package com.ljn.netty.hexproxy;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * 这个代理服务器的作用就是：
 * 1.当Client向它发请求时，它先不做响应，而是连接到真实服务器并将请求转发
 * 2.当真实服务器返回响应时，它会先接收到，然后将响应转发给Client
 * 3.在这过程中，请求和响应都会经过代理，代理就可以打印请求或响应的hex
 * 
 * 例如运行时：java HexDumpProxy 8888 127.0.0.1 8080
 * 然后在浏览器中输入：
 * http://127.0.0.1:8888
 * 则浏览器会重定向到127.0.0.1 8080（需要在127.0.0.1 8080运行一个tomcat或其他web服务器）
 * 同时，在HexDumpProxy会打印Client和真实服务器传递的数据（hex形式）
 * @author lijinnan
 * @date:2013-12-4 上午9:43:40
 */
public class HexDumpProxy {
    public static void main(String[] args) throws Exception {
        // Validate command line options.
        if (args.length != 3) {
            System.err.println(
                    "Usage: " + HexDumpProxy.class.getSimpleName() +
                    " <local port> <remote host> <remote port>");
            return;
        }
        // Parse command line options.
        int localPort = Integer.parseInt(args[0]);
        String remoteHost = args[1];
        int remotePort = Integer.parseInt(args[2]);

        System.err.println(
                "Proxying *:" + localPort + " to " +
                remoteHost + ':' + remotePort + " ...");
        // Configure the bootstrap.
        Executor executor = Executors.newCachedThreadPool();
        
        /*
         注意区分Bootstrap里面的两个“Factory”
         1.ChannelFactory：决定了创建的Channel是ServerSocketChannel还是SocketChannel
         2.ChannelPipelineFactory：开发时重写getPipeline方法，注册必要的Handler
         */
        
        //1.ChannelFactory：ServerSocketChannelFactory
        NioServerSocketChannelFactory serverChannelFactory = new NioServerSocketChannelFactory(executor, executor);
        ServerBootstrap serverBootstrap = new ServerBootstrap(serverChannelFactory);
        
        //为了共享Executor，在这里创建ClientSocketChannelFactory，并传递过去，以便在Handler里面创建ClientBootstrap
        ClientSocketChannelFactory clientFactory =
                new NioClientSocketChannelFactory(executor, executor);
        
        HexDumpProxyPipelineFactory pipelineFactory = new HexDumpProxyPipelineFactory(clientFactory, remoteHost, remotePort);
        
        //2.ChannelPipelineFactory
        serverBootstrap.setPipelineFactory(pipelineFactory);
        
        // Start up the server.
        serverBootstrap.bind(new InetSocketAddress(localPort));
    }
}
