package com.raiden.homework.rpcapi;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
@Data
public class RpcRequest implements Serializable {
    String className;
    String method;
    Object[] param;

}
