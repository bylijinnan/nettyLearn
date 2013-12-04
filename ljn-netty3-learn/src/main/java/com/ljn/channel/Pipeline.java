package com.ljn.channel;

/**
 * ChannelPipeline采用的是Intercepting Filter 模式
 * 但由于用到两个双向链表和内部类，这个模式看起来不是那么明显，需要仔细查看调用过程才发现
 * 
 * 下面对ChannelPipeline作一个模拟，只模拟关键代码：
 */
public class Pipeline {
    
    //netty里面为了能处理Upstream和Downstream，采用的是双向链表；
    //现在只关注Upstream的处理，因此单向链表就可以了
    private  DefaultHandlerContext head;
    //private  DefaultHandlerContext tail;  
    
    public static void main(String[] args) {
        Pipeline pipeline= new Pipeline();
        pipeline.addLast(new ClientSimpleHandlerA());
        pipeline.addLast(new ClientSimpleHandlerB());
        
        String event = "";
        pipeline.sendUpstream(event);
    }
    
    private void sendUpstream(String event) {
        HandlerContext ctx = head;
        if (ctx != null) {
            sendUpstream(ctx, event);
        }
    }

    //调用Handler的入口。但是否继续调用下一个Handler，取决于Handler的handleUpstream方法
    //是否调用sendUpstream
    //把HandlerContext传递给handleUpstream方法，以便接口回调
    public void sendUpstream(HandlerContext ctx, String event) {
        ((DefaultHandlerContext)ctx).getHandler().handleUpstream(ctx, event);
    }
    
    private class DefaultHandlerContext implements HandlerContext {
        
        //HandlerContext pre;     //双向链表。现在我们只关注一个方向
        HandlerContext next;
        Handler handler;
        
        
        //这里体现了Intercepting Filter中的next调用
        @Override
        public void sendUpstream(String event) {
            HandlerContext next = this.next;
            if (next != null) {
                Pipeline.this.sendUpstream(next, event);    //调用的是外部类的方法
            }
        }
        
        public Handler getHandler() {
            return handler;
        }
        
        public void setHandler(Handler handler) {
            this.handler = handler;
        }
        
        /*
         * omit other getter/setter
         */
    }
    
    public void addLast(Handler handler) {
        DefaultHandlerContext ctx = new DefaultHandlerContext();
        ctx.setHandler(handler);
        if (head == null) {
            head = ctx;
        } else {
            head.next = ctx;
        }
    }
    
}


interface Handler {
    void handleUpstream(HandlerContext ctx, String event);
}

 interface HandlerContext {
     void sendUpstream(String event);
}
 
class ClientSimpleHandlerA implements Handler {

    @Override
    public void handleUpstream(HandlerContext ctx, String event) {
        System.out.println("ClientSimpleHandlerA");
        ctx.sendUpstream(event);    // 继续调用下一个Handler
    }
     
 }

class ClientSimpleHandlerB implements Handler {
    
    @Override
    public void handleUpstream(HandlerContext ctx, String event) {
        System.out.println("ClientSimpleHandlerB");
        //ctx.sendUpstream(event);    //假设到了这里处理就结束了，不调用下一个Handler
    }
    
}