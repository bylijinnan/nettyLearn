package com.ljn.handler.replay;

/**
 * 参考http://biasedbit.com/an-enhanced-version-of-replayingdecoder-for-netty/
 * 加入一些我自己的理解
 * 1.type(continue)
 * 2.nextState
 * @author lijinnan
 * @date:2013-12-13
 * @param <T>
 */
public class ContinueDecodeResult<T extends Enum<T>>
        implements DecodeResult<T> {
 
    // internal vars ----------------------------------------------------------
 
    private final T nextState;
 
    // constructors -----------------------------------------------------------
 
    public ContinueDecodeResult(T nextState) {
        this.nextState = nextState;
    }
 
    // DecodeResult -----------------------------------------------------------
 
    @Override
    public Type getType() {
        return Type.CONTINUE;
    }
 
    // getters & setters ------------------------------------------------------
 
    public T getNextState() {
        return nextState;
    }
}
 