package com.raiden.homework.rpcclient;

import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class RpcProxyClient {


    public <T> T clientProxy(Class<T> clazz) {

        return clientProxy(clazz, 8811);
    }

    public <T> T clientProxy(Class<T> clazz, int port) {
        try {
            return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new RpcInvocationHandler(port));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
