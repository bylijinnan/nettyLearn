package com.ljn.buffer;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;

import com.ljn.util.Helper;

/**
 * 1.slice也好，duplicate也好，都是共享同一个byte[]，但ridx、widx等参数，各自独立
 * 2.copy则是新开一个byte[]，并复制数据
 * @author lijinnan
 * @date:2013-12-10 下午3:05:41  
 */
public class ChannelBufferTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        byte[] content = "abcd".getBytes();
        BigEndianHeapChannelBuffer buff = new BigEndianHeapChannelBuffer(content);
        ChannelBuffer slicedBuff = buff.slice(0, content.length);
        
        Helper.printAsBytesWithoutChange(buff); //abcd
        Helper.printAsBytesWithoutChange(slicedBuff); //abcd
        
        content[0] = 'z';
        Helper.printAsBytesWithoutChange(buff); //zbcd
        Helper.printAsBytesWithoutChange(slicedBuff); //zbcd
        
        slicedBuff.readByte();
        System.out.println(buff);   //(ridx=0, widx=4, cap=4)
        System.out.println(slicedBuff); //(ridx=1, widx=4, cap=4)
        
        
        //discardReadBytes实际上就是把整个buffer的内容（ridx与widx之间的内容），
        //前移到buffer的开头（ridx=0），源码里面这个方法是通过System.arraycopy完成的：
        //从本数组的这端复制到本数组的另一端
        slicedBuff.discardReadBytes();
        System.out.println(buff);   //(ridx=0, widx=4, cap=4)
        System.out.println(slicedBuff); //(ridx=0, widx=3, cap=4)
        Helper.printAsBytesWithoutChange(buff); //bcdd
        Helper.printAsBytesWithoutChange(slicedBuff);   //bcdd
        
    }

}
