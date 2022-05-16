package com.li.mqttclient.config;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIdleStateHandler extends ChannelDuplexHandler {
    private static Logger logger = LoggerFactory.getLogger(ClientIdleStateHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState e = ((IdleStateEvent) evt).state();
            if (e == IdleState.ALL_IDLE) {
                MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttMessage mqttMessage = new MqttMessage(fixedHeader);
                if (ctx.channel().isWritable()) {
                    ctx.channel().writeAndFlush(mqttMessage);
                } else {
                    logger.info("the channel is full,send heart beat failed");
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

}
