package com.ljn.handler.replay;

import java.util.List;

/**
 * stupid but simple example
 * @author lijinnan
 * @date:2013-12-13
 */
public abstract class Message {

    private int type;
    private String id;
    private List<String> params;
    
    public Message(int type, String id, List<String> params) {
        this.type = type;
        this.id = id;
        this.params = params;
    }
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getParams() {
        return params;
    }
    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", id=" + id + ", params=" + params
                + "]";
    }
    
    
}

//type=1000
class OtherMessage extends Message {
    public OtherMessage (String id) {
        super(1000, id, null);
    }
    
}

//type=1
class Type1Message extends Message {
    
    public Type1Message (String id) {
        super(1, id, null);
    }
    
}
