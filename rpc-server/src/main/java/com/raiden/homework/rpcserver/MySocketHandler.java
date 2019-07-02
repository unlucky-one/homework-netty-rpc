package com.raiden.homework.rpcserver;

import com.raiden.homework.rpcapi.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/28
 */
public class MySocketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        ProcessHandler handler=new ProcessHandler();
        RpcRequest request= (RpcRequest) msg;
        Object o = handler.invoke(request);
        ctx.write(o);
        ctx.flush();
        ctx.close();

    }
}
