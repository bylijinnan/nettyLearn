package com.ljn.filetransfer;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerHandler extends SimpleChannelUpstreamHandler{

    private AtomicInteger count = new AtomicInteger(0);
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        File file = new File("C:/Users/lijinnan/Desktop/tmpReceived.txt");
        ChannelBuffer buff = (ChannelBuffer)e.getMessage();
        byte[] byteArray = new byte[buff.readableBytes()];
        buff.readBytes(byteArray);
        FileUtils.writeByteArrayToFile(file, byteArray, true);
        System.out.println(count.addAndGet(1) + " messageReceived(bytes):" + byteArray.length);
    }
}
