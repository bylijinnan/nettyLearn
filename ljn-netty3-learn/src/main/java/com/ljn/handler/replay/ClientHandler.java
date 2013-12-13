package com.ljn.handler.replay;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler {


    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        Message msg = generateMessage();
        
        ChannelBuffer typeBuff = ChannelBuffers.buffer(4);
        typeBuff.writeInt(msg.getType());
        
        //“id的长度”只用一个字节存储，因此id的长度只能是（-128~127）
        //实际程序中应该对此做检查
        //同样的还有PARAM_COUNT、PARAM_LENGTH
        int idSize = msg.getId().getBytes().length;
        ChannelBuffer idSizeBuff = ChannelBuffers.buffer(1);
        idSizeBuff.writeByte(idSize);
        
        ChannelBuffer idBuff = ChannelBuffers.buffer(idSize);
        idBuff.writeBytes(msg.getId().getBytes());
        
        int paramCount = msg.getParams().size();
        ChannelBuffer paramCountBuff = ChannelBuffers.buffer(1);
        paramCountBuff.writeByte(paramCount);
        
        List<ChannelBuffer> paramsBuff = new ArrayList<ChannelBuffer>(paramCount * 2);
        for (String param : msg.getParams()) {
            ChannelBuffer oneParamBuffLength = ChannelBuffers.buffer(1);
            oneParamBuffLength.writeByte(param.getBytes().length);
            
            ChannelBuffer oneParamBuffValue = ChannelBuffers.buffer(param.getBytes().length);
            oneParamBuffValue.writeBytes(param.getBytes());
            
            paramsBuff.add(oneParamBuffLength);
            paramsBuff.add(oneParamBuffValue);
        }
        
        ChannelBuffer first = ChannelBuffers.wrappedBuffer(typeBuff, idSizeBuff, idBuff, paramCountBuff);
        ChannelBuffer second = ChannelBuffers.wrappedBuffer(toArray(paramsBuff));
        ChannelBuffer all = ChannelBuffers.wrappedBuffer(first, second);
        e.getChannel().write(all);
        System.out.println("in channelConnected: write ok");
    }

    private ChannelBuffer[] toArray(List<ChannelBuffer> list) {
        ChannelBuffer[] arr = new ChannelBuffer[list.size()];
        list.toArray(arr);
        return arr;
    }
    
    private OtherMessage generateMessage() {
        String id = "A0001";
        OtherMessage msg = new OtherMessage(id);
        List<String> params  = new ArrayList<String>(3);
        params.add("a");
        params.add("bb");
        params.add("ccc");
        msg.setParams(params);
        return msg;
    }
    
}
