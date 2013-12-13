package com.ljn.handler.replay;

/**
 * 参考http://biasedbit.com/an-enhanced-version-of-replayingdecoder-for-netty/
 * 加入一些我自己的理解
 * 1.type(finished)
 * 2.result
 * @author lijinnan
 * @date:2013-12-13
 * @param <t>
 */
public class FinishedDecodeResult<t> implements DecodeResult<t> {
 
    // internal vars ----------------------------------------------------------
 
    private final Object result;
 
    // constructors -----------------------------------------------------------
 
    public FinishedDecodeResult(Object result) {
        this.result = result;
    }
 
    // DecodeResult -----------------------------------------------------------
 
    @Override
    public Type getType() {
        return Type.FINISHED;
    }
 
    // getters & setters ------------------------------------------------------
 
    public Object getResult() {
        return result;
    }
}