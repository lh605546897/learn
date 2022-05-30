package com.li.mqtt.controller;

import cn.hutool.cache.Cache;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.li.mqtt.config.ClientIdleStateHandler;
import com.li.mqtt.config.MqttHandler;
import com.li.mqtt.config.NettyConfiguration;
import com.li.mqtt.dto.BatchDataStream;
import com.li.mqtt.dto.DataStream;
import com.li.mqtt.dto.Device;
import com.li.mqtt.dto.ShadowReportReq;
import com.li.mqtt.util.TopicConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("data")
public class DataController {
    private static Logger logger = LoggerFactory.getLogger(DataController.class);
    @Autowired
    private NettyConfiguration configuration;
    @Autowired
    private Cache channelCache;
    @Autowired
    private Cache deviceCache;
    @Autowired
    private Bootstrap bootstrap;

    @GetMapping("push")
    public void publishData(String sn,
                            String deviceKey,
                            String deviceToken,
                            String stream,
                            String encodeType) throws Exception {
        Timer timer = new Timer();
        String password = "D:" + deviceToken;
        Channel channel = tcpConnect(false, sn, deviceKey, password);
        Thread.sleep(3000);
        String topic = "sys/data/" + stream + "/" + encodeType;
        timer.schedule(new DataPushTask(deviceKey, deviceToken, topic, channel, null), 0, 3 * 60 * 1000);
    }

    @PostMapping("batchPush")
    public void batchPushData(@RequestBody BatchDataStream batchDataStream) throws Exception {
        Timer timer = new Timer();
        String deviceKey = batchDataStream.getDeviceKey();
        String deviceToken = batchDataStream.getDeviceToken();
        String password = "D:" + deviceToken;
        String topic = "sys/batchStream";
        Channel channel = tcpConnect(false, batchDataStream.getSn(), deviceKey, password);
        Thread.sleep(3000);
        timer.schedule(new DataPushTask(deviceKey, deviceToken, topic, channel, batchDataStream.getDataStreams()), 0, 3 * 60 * 1000);
    }

    @PostMapping("reportShadow")
    public void reportShadow(@RequestBody ShadowReportReq req) throws Exception {
        String deviceKey = req.getDeviceKey();
        String deviceToken = req.getDeviceToken();
        Timer timer = new Timer();
        String topic = "sys/shadow/report";
        String password = "D:" + deviceToken;
        Channel channel = tcpConnect(false, req.getSn(), deviceKey, password);
        Thread.sleep(3000);
        Device device = (Device) deviceCache.get(req.getSn());
        if (device == null) {
            device = new Device();
            device.setVersion("1");
        }
        device.setMeasurements(req.getMeasurements());
        deviceCache.put(deviceKey, device);
        new DataPushTask(deviceKey, deviceToken, topic, channel, null).run();
//        timer.schedule(new DataPushTask(deviceKey, deviceToken, topic, channel, null), 0, 3 * 60 * 1000);

    }

    @PostMapping("getShadow")
    public void getShadow(String sn,
                          String deviceKey,
                          String deviceToken) throws Exception {
        String topic = "sys/shadow/fetch";
        String password = "D:" + deviceToken;
        Channel channel = tcpConnect(false, sn, deviceKey, password);
        Thread.sleep(3000);
        DataPushTask dataPushTask = new DataPushTask(deviceKey, deviceToken, topic, channel, null);
        dataPushTask.run();

    }


    public Channel mqttConnect(String clientId, String userName, String password, int qos, Channel channel1) {
        //mqtt 固定报头
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT
                , false, MqttQoS.valueOf(qos), false, 0);
        //mqtt可变报头
        MqttConnectVariableHeader variableHeader = new MqttConnectVariableHeader("MQTT", MqttVersion.MQTT_3_1_1.protocolLevel(),
                true, true, false, 0, false, true, configuration.getKeepAliveSecond());
        //mqtt有效载荷
        MqttConnectPayload payload = new MqttConnectPayload(clientId, null, "", userName, password);
        MqttConnectMessage message = new MqttConnectMessage(fixedHeader, variableHeader, payload);
        if (channel1.isWritable()) {
            channel1.writeAndFlush(message);
            logger.info("connect success");
            return channel1;
        } else {
            logger.error("the channel is full,connect failed,clientId={}", clientId);
            return null;
        }
    }

    public Channel tcpConnect(boolean isSubscribe, String clientId, String userName, String password) throws Exception {
        Object channel = channelCache.get(clientId);
        if (channel != null) {
            Channel channel1 = (Channel) channel;
            if (channel1.isWritable()) {
                return channel1;
            }
        }
        ChannelFuture channelFuture = bootstrap.connect(configuration.getHost(), configuration.getPort());
        AttributeKey<String> CLIENT_ID = AttributeKey.valueOf("clientId");
        AttributeKey<String> USERNAME = AttributeKey.valueOf("userName");
        AttributeKey<String> PASSWORD = AttributeKey.valueOf("password");
        channelFuture.channel().attr(CLIENT_ID).set(clientId);
        channelFuture.channel().attr(USERNAME).set(userName);
        channelFuture.channel().attr(PASSWORD).set(password);
        channelFuture.sync();
        channel = channelFuture.channel();
        channelCache.put(clientId, channel);
        return mqttConnect(clientId, userName, password, 0, (Channel) channel);
    }


}
