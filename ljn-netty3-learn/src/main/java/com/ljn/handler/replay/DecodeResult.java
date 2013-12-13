package com.ljn.handler.replay;

/**
 * 参考http://biasedbit.com/an-enhanced-version-of-replayingdecoder-for-netty/
 * 加入一些我自己的理解
 * @author lijinnan
 * @date:2013-12-13
 */
public interface DecodeResult<t> {
    
    enum Type {
        FINISHED,
        CONTINUE
    }
 
    Type getType();
}
