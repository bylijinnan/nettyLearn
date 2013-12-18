package com.ljn.msg.delimiter;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerHandler extends SimpleChannelUpstreamHandler{

    /**
     * 把接收到的字符串消息去掉前面多余的0以及表示消息长度的数字
     * e.g. "0003data"--> data
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        String str = (String) e.getMessage();
        String msg = null;
        if (StringUtils.isNotBlank(str)) {
            msg = StringUtils.stripStart(str, Constant.PAD_CHAR).substring(1);
            System.out.println("[server]" + msg);
        }
    }
}
