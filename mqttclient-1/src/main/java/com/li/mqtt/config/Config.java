package com.li.mqtt.config;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Config {

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(500);
    @Autowired
    private MqttHandler mqttHandler;

    @Bean
    public Cache channelCache(){
        return CacheUtil.newFIFOCache(100);
    }

    @Bean
    public Cache deviceCache(){
        return CacheUtil.newFIFOCache(100);
    }

    @Bean
    public Bootstrap bootstrap(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addFirst("idleStateHandler", new IdleStateHandler(0, 0, 20));
                        channelPipeline.addAfter("idleStateHandler", "idleEventHandler", new ClientIdleStateHandler());
                        channelPipeline.addLast("decoder", new MqttDecoder());
                        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        channelPipeline.addLast("handler", mqttHandler);
                    }
                });
        return bootstrap;
    }
}
