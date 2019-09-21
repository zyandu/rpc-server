package me.zy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RPC服务代理类，用来对外暴露服务
 */
public class RpcProxyServer {
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 发布方法
     */
    public void publisher(Object service,int port){
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            while (true){//不断接收请求
                Socket socket = serverSocket.accept();

                //每一个Socker交给一个processHandler来处理
                executorService.execute(new ProcessorHandler(socket,service));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
