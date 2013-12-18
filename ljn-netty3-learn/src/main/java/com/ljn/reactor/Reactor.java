package com.ljn.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
参考文章：
http://jeewanthad.blogspot.com/2013/02/reactor-pattern-explained-part-1.html
http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf

单线程的实现
Recator用一个线程（在main方法里面start）来响应所有Client的请求：
1.accept时创建对应的handler（称为handlerA），并将handler的interestOps初始为READ
2.当读事件ready，handlerA被选中（dispatch），并执行它的run方法
因此，每accept就创建一个handler（也就是为每一个Client设置一个handler），但都在同一线程里面处理

Selection Key   Channel                 Handler     Interested Operation
------------------------------------------------------------------------
SelectionKey 0  ServerSocketChannel     Acceptor    Accept
SelectionKey 1  SocketChannel 1         Handler 1   Read and Write
SelectionKey 2  SocketChannel 2         Handler 2   Read and Write
SelectionKey 3  SocketChannel 3         Handler 3   Read and Write

如果采用多个selector，那就是所谓的“Multiple Reactor Threads”，大体思路如下：

Selector[] selectors; // also create threads
int next = 0;
class Acceptor { // ... 
     public synchronized void run() { ...
         Socket connection = serverSocket.accept();
         if (connection != null)
             new Handler(selectors[next], connection);
         if (++next == selectors.length) next = 0;
     }
}

 */
public class Reactor implements Runnable {
 
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;
    final boolean isWithThreadPool;
 
    /*Reactor的主要工作：
     * 1.维护一个acceptor，接收请求
     * 2.把请求分发给handler
     * 要注意其实acceptor也是一个handler（只是与它关联的channel是ServerSocketChannel而不是SocketChannel）
     */
    Reactor(int port, boolean isWithThreadPool) throws IOException {
 
        this.isWithThreadPool = isWithThreadPool;
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey0 = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey0.attach(new Acceptor());
    }
 
 
    public void run() {
        System.out.println("Server listening to port: " + serverSocketChannel.socket().getLocalPort());
        try {
            while (!Thread.interrupted()) {
                int readySelectionKeyCount = selector.select();
                if (readySelectionKeyCount == 0) {
                    continue;
                }
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) (it.next()));
                }
                
                //不会自动remove，因此要手动清；下次事件到来会自动添加
                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //handler作为SellectionKey的attachment。这样，handler就与SelectionKey也就是interestOps对应起来了
    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        } 
    }
    
    //主要工作是为每一个连接成功后返回的SocketChannel关联一个handler
    class Acceptor implements Runnable {
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    if (isWithThreadPool)
                        new HandlerWithThreadPool(selector, socketChannel);
                    else
                        new Handler(selector, socketChannel);
                }
                System.out.println("Connection Accepted by Reactor2");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws IOException{
        
        int port = 9900;
        boolean withThreadPool = false;
        Reactor reactor  = new Reactor(port, withThreadPool);
        new Thread(reactor).start();
    }
}