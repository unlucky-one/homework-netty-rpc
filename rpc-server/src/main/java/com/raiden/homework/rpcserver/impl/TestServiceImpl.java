package com.raiden.homework.rpcserver.impl;

import com.raiden.homework.rpcapi.TestService;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class TestServiceImpl implements TestService {
    public String hello(String name) {
        return "hello " + name;
    }
}
