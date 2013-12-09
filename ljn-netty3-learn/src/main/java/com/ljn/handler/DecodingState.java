package com.ljn.handler;

/**
 * @author lijinnan
 * @date:2013-11-20 下午3:45:15  
 */
public enum DecodingState {
    VERSION,
    TYPE,
    PAYLOAD_LENGTH,
    PAYLOAD,
}
