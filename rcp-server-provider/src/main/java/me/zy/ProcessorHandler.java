package me.zy;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class ProcessorHandler implements Runnable{
    private Socket socket;
    //private Object service;
    private Map<String,Object> handlerMap;

    public ProcessorHandler(Socket socket, /*Object service*/Map<String,Object> handlerMap) {
        this.socket = socket;
        //this.service = service;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            //请求类相关信息：哪个类、方法名称、参数
            RpcRequest rcpRequest = (RpcRequest) objectInputStream.readObject();
            Object result = invoke(rcpRequest);

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            try {
                if(objectInputStream != null){
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(objectOutputStream != null){
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object invoke(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException,     InvocationTargetException, IllegalAccessException {
        //反射调用
        String serviceName = request.getClassName();
        String version = request.getVersion();
        //版本号判断
        if(!StringUtils.isEmpty(version)){
            serviceName += "-" + version;
        }
        Object service = handlerMap.get(serviceName);
        if(service == null){
            throw new RuntimeException("service not found:" + serviceName);
        }

        Object[] args = request.getParameters();
        Class<?>[] types = new Class[args.length];//每个参数对应的类型
        for (int i = 0; i < types.length; i++) {
            types[i] = args[i].getClass();
        }

        //根据请求的类进行加载，IHelloService
        Class clazz = Class.forName(request.getClassName());
        //根据请求方法找到类中的方法，sayHello、saveUser
        Method method = clazz.getMethod(request.getMethodName(),types);
        Object result = method.invoke(service,args);//HelloServiceImpl
        return result;

    }
}
