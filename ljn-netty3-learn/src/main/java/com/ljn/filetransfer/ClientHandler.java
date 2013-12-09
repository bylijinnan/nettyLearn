package com.ljn.filetransfer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler {


    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        ChannelBuffer buff = formBuffer();
        e.getChannel().write(buff);
        System.out.println("in channelConnected: write ok");
    }

    private ChannelBuffer formBuffer() {
        ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
        try {
            File file = new File("C:/Users/lijinnan/Desktop/tmp.txt");
            byte[] bytes = FileUtils.readFileToByteArray(file);
            buff.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buff;
    }
}
