package com.ljn.handler.replay;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

/**
参考http://biasedbit.com/an-enhanced-version-of-replayingdecoder-for-netty/
加入一些我自己的理解

原作者定义的消息结构：

TYPE (1B)
ID_SIZE (1B)
ID (xB)
PARAM_COUNT (1B)
[PARAM_LENGTH (1B) | PARAM_VALUE (xB)]*

但查看程序，TYPE是int类型，因此应该占4Byte，也就是消息的结构为：

TYPE (4B)
ID_SIZE (1B)
ID (xB)
PARAM_COUNT (1B)
[PARAM_LENGTH (1B) | PARAM_VALUE (xB)]*

 */
public class SampleDecoder
        extends EnhancedReplayingDecoder<SampleDecoder.DecodingState> {
 
    // internal vars ---------------------------------------------------------
 
    private int type;
    private byte[] id;
    private int nParams;
    private List<String> params;
    private byte[] param;
 
    // public classes --------------------------------------------------------
    
    public static enum DecodingState {
        TYPE,
        ID_SIZE,
        ID,
        PARAM_COUNT,
        PARAM_SIZE,
        PARAM_VALUE
    }
    
    // constructors ----------------------------------------------------------
 
    public SampleDecoder() {
        super(DecodingState.TYPE);
    }
 
    // EnhancedReplayingDecoder ----------------------------------------------
 
    @Override
    protected DecodeResult<DecodingState> decode(ChannelBuffer buffer,
                                                 DecodingState state)
            throws Exception {
        switch (state) {
            case TYPE:
                this.type = buffer.readInt();
                return this.continueDecoding(DecodingState.ID_SIZE);
 
            case ID_SIZE:
                // Should be protected for 0 or negative sizes.
                this.id = new byte[buffer.readByte()];
                return this.continueDecoding(DecodingState.ID);
 
            case ID:
                buffer.readBytes(this.id);
                if (this.type == 1) {
                    // Lets assume type 1 messages only need id.
                    Message m = new Type1Message(new String(this.id));
                    return this.finishedDecoding(m);
                } else {
                    // Otherwise continue decoding.
                    return this.continueDecoding(DecodingState.PARAM_COUNT);
                }
 
            case PARAM_COUNT:
                this.nParams = buffer.readByte();
                // If there are parameters continue decoding, otherwise bail.
                if (this.nParams > 0) {
                    this.params = new ArrayList<String>(this.nParams);
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                } else {
                    Message m = new OtherMessage(new String(this.id));
                    return this.finishedDecoding(m);
                }
 
            case PARAM_SIZE:
                this.param = new byte[buffer.readByte()];
                return this.continueDecoding(DecodingState.PARAM_VALUE);
 
            case PARAM_VALUE:
                buffer.readBytes(this.param);
                this.params.add(new String(this.param));
                if (this.params.size() >= this.nParams) {
                    // This was the last parameter, exit.
                    Message m = new OtherMessage(new String(this.id));
                    m.setParams(this.params);
                    return this.finishedDecoding(m);
                } else {
                    
                    //关键在于这里。继续读取下一个param
                    // Continue reading parameters.
                    return this.continueDecoding(DecodingState.PARAM_SIZE);
                }
 
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }
 
    @Override
    protected void cleanup() {
        // cleanup pending resources allocated for decoding
        this.id = null;
        this.nParams = 0;
        this.type = -1;
        this.param = null;
        this.params = null;
    }
 
    
}