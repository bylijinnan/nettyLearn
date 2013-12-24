package com.ljn.util;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author lijinnan
 * @date:2013-11-28 下午3:33:08  
 */
public class Helper {

    private Helper() {
        throw new IllegalStateException();
    }
    
    
    public static void main(String[] args) {
        int i = 1;
        ChannelBuffer buff = ChannelBuffers.buffer(4);
        buff.writeInt(i);
        int j = buff.readInt();
        System.out.println(j);
        System.out.println(buff.readableBytes());
    }
    
    public static String toString(ChannelBuffer buff) {
        return buff.toString(CharsetUtil.UTF_8);
    }
    
    public static void printAsBytesWithoutChange(ChannelBuffer buff) {
        for (int i = 0, len = buff.capacity(); i < len; i++) {
            System.out.print((char) buff.getByte(i));
        }
        System.out.println();
    }
    
    public static void print(ChannelPipeline pipeline) {
        if (pipeline == null) {
            throw new NullPointerException();
        }
        Map<String, ChannelHandler> map = pipeline.toMap();
        if (map.isEmpty()) {
            System.out.println("pipeline is empty");
            return;
        }
        for (Entry<String, ChannelHandler> item : map.entrySet()) {
            String name = item.getKey();
            ChannelHandler handler = item.getValue();
            System.out.println(name + ":" + handler);
        }
    }
    
}
