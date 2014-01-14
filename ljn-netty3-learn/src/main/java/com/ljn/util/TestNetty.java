package com.ljn.util;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMessageDecoder;

/**
 * @author lijinnan
 * @date:2013-12-27
 */
public class TestNetty {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

class X extends HttpMessageDecoder {

    @Override
    protected boolean isDecodingRequest() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
}