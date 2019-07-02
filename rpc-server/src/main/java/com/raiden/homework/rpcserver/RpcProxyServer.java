package com.raiden.homework.rpcserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.raiden.homework.rpcserver.Constants.classMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class RpcProxyServer {

    ExecutorService service = Executors.newCachedThreadPool();

    public RpcProxyServer() {
        scanClass("com.raiden.homework.rpcserver.impl");
    }

    public void scanClass(String packageName) {
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    addClasses(packageName, filePath, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addClasses(String packageName,
                           String packagePath, final boolean recursive) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                addClasses(packageName + "."
                        + file.getName(), file.getAbsolutePath(), recursive);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    String fullName = packageName + '.' + className;
                    Class clazz = Class.forName(fullName);
                    Constants.classMap.put(className, clazz);
                    Constants.classMap.put(fullName, clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int port = 8811;

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                        protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            int size = 2048;
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(size, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(size, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new MySocketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("端口:" + port + "监听已启动.");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
