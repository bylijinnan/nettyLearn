package com.ljn.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 多线程版本的Handler
 * 思路就是把耗时的操作（非IO操作）放到其他线程里面跑，
 * 使得Handler只专注与Channel之间的IO操作；
 * Handler快速地从Channel中读或写，可以使Channel及时地、更快地响应其他请求
 * 耗时的操作完成后，产生一个事件（改变state），再“通知”（由Handler轮询这个状态是否有改变）
 * Handler执行Channel的读写操作
 */
public class HandlerWithThreadPool extends Handler {
 
    static ExecutorService pool = Executors.newFixedThreadPool(2);
    static final int PROCESSING = 2;
 
    public HandlerWithThreadPool(Selector sel, SocketChannel c) throws IOException {
        super(sel, c);
    }
 
    //Handler从SocketChannel中读到数据后，把“数据的处理”这个工作扔到线程池里面执行
    void read() throws IOException {
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            state = PROCESSING;
            
            //execute是非阻塞的，所以要新增一个state（PROCESSING），表示数据在处理当中，Handler还不能执行send操作
            pool.execute(new Processer(readCount)); 
        }
        //We are interested in writing back to the client soon after read processing is done.
        //这时候虽然设置了OP_WRITE，但下一次本Handler被选中时不会执行send()方法，因为state=PROCESSING
        //或者可以把这个设置放到Processer里面，等process完成后再设为OP_WRITE
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }
 
    //Start processing in a new Processer Thread and Hand off to the reactor thread.
    synchronized void processAndHandOff(int readCount) {
        readProcess(readCount);
        //Read processing done. Now the server is ready to send a message to the client.
        state = SENDING;
    }
 
    class Processer implements Runnable {
        int readCount;
        Processer(int readCount) {
            this.readCount =  readCount;
        }
        public void run() {
            processAndHandOff(readCount);
        }
    }
}