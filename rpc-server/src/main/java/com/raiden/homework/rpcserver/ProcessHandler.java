package com.raiden.homework.rpcserver;

import com.raiden.homework.rpcapi.RpcRequest;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
@NoArgsConstructor
public class ProcessHandler implements Runnable {
    Socket socket;

    public ProcessHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Object object = inputStream.readObject();
            RpcRequest rpcRequest = null;
            if (object instanceof RpcRequest) {
                rpcRequest = (RpcRequest) object;
                Object result = invoke(rpcRequest);
                if (result != null) {
                    outputStream.writeObject(result);
                    outputStream.writeObject(null);
                    outputStream.flush();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Object invoke(RpcRequest request) {
        try {
            Class clazz = Class.forName(request.getClassName());
            Class classImpl = getClassImpl(request);

            if (request.getParam() == null) {
                return clazz.getMethod(request.getMethod()).invoke(classImpl);
            }
            Class[] paramTypes = new Class[request.getParam().length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramTypes[i] = request.getParam()[i].getClass();
            }
            Object instance = classImpl.newInstance();
            return classImpl.getMethod(request.getMethod(), paramTypes).invoke(instance, request.getParam());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    Class getClassImpl(RpcRequest request) {
        try {
            Class classImpl = Constants.classMap.get(request.getClassName());
            if (classImpl == null) {
                Class clazz = Class.forName(request.getClassName());
                if (clazz.isInterface()) {
                    for (Map.Entry<String, Class> entry : Constants.classMap.entrySet()) {
                        if (clazz.isAssignableFrom(entry.getValue())) {
                            Constants.classMap.put(request.getClassName(), entry.getValue());
                            classImpl = entry.getValue();

                        }
                    }
                }
            }
            return classImpl;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
