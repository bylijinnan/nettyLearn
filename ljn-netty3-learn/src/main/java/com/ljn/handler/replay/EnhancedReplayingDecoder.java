package com.ljn.handler.replay;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

/**
 * 参考http://biasedbit.com/an-enhanced-version-of-replayingdecoder-for-netty/
 * 加入一些我自己的理解
 * @author lijinnan
 * @date:2013-12-13
 */
public abstract class EnhancedReplayingDecoder<T extends Enum<T>> extends
        ReplayingDecoder<T> {

    // internal vars ----------------------------------------------------------

    private final T initialState;

    // constructors -----------------------------------------------------------

    public EnhancedReplayingDecoder(T initialState) {
        this(initialState, false);
    }

    public EnhancedReplayingDecoder(T initialState, boolean unfold) {
        super(initialState, unfold);
        this.initialState = initialState;
    }

    // ReplayingDecoder -------------------------------------------------------

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer, T state) throws Exception {
        for (;;) {
            // Request a decode with provided buffer and current state. It can
            // only end in one of three ways:
            // 1. There is still data missing (CONTINUE), so a checkpoint will
            // be set and buffer draining will continue;
            // 2. There was enough data to build a message (FINISHED), so reset
            // the decoder and return the result;
            // 3. There was insufficient data, in which case an exception will
            // be thrown and handled by the ReplayingDecoder.
            DecodeResult<T> result = this.decode(buffer, this.getState());
            if (result == null) {
                throw new IllegalArgumentException("decode() returned null");
            }
            switch (result.getType()) {
            case FINISHED:
                // Final state, with composed object.
                try {
                    return ((FinishedDecodeResult<T>) result).getResult();
                } finally {
                    this.reset();
                }
            case CONTINUE:
                // Keep processing the message, setting a checkpoint to the
                // next state.
                this.checkpoint(((ContinueDecodeResult<T>) result)
                        .getNextState());
                break;
            default:
                // Never actually falls here...
                throw new IllegalArgumentException("Unsupported result: "
                        + result.getType());
            }
        }
    }

    // protected helpers ------------------------------------------------------

    protected DecodeResult<T> continueDecoding(T nextState) {
        return new ContinueDecodeResult<T>(nextState);
    }

    protected DecodeResult<T> finishedDecoding(Object result) {
        return new FinishedDecodeResult<T>(result);
    }

    protected void reset() {
        this.cleanup();
        this.setState(this.initialState);
    }

    protected abstract DecodeResult<T> decode(ChannelBuffer buffer,
            T currentState) throws Exception;

    protected abstract void cleanup();
}
