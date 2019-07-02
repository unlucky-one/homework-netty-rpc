package com.raiden.homework.rpcclient;

import com.raiden.homework.rpcapi.TestService;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class ClientTest {
    public static void main(String[] args) {
        RpcProxyClient client=new RpcProxyClient();
        TestService testService=client.clientProxy(TestService.class);
        String s = testService.hello("world");
        System.out.println(s);
    }

}
