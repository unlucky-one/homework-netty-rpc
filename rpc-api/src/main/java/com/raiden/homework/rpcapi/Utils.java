package com.raiden.homework.rpcapi;


import java.io.Closeable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class Utils {
    public static void closeStream(Closeable stream){
        if(stream!=null){
            try {
                stream.close();
            }catch (Exception e){

            }

        }
    }
}
