package com.li.mqttclient.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.li.mqttclient.config.ClientIdleStateHandler;
import com.li.mqttclient.config.MqttHandler;
import com.li.mqttclient.config.MqttSimulator;
import com.li.mqttclient.config.NettyConfiguration;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DataUpload {
    private static Logger logger = LoggerFactory.getLogger(DataUpload.class);
    private AtomicInteger messageId = new AtomicInteger(1);
    private Channel channel;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private NioEventLoopGroup workGroup = new NioEventLoopGroup(500);
    @Autowired
    private MqttSimulator mqttSimulator;
    @Autowired
    private MqttHandler mqttHandler;
    @Value("${data.deviceKey}")
    private String deviceKey;
    @Value("${data.deviceToken}")
    private String deviceToken;
    @Autowired
    private NettyConfiguration configuration;

    //    @Scheduled(cron = "0/5 * * * * ? ")
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void uploadData() throws Exception {
        System.out.println(DateUtil.formatDateTime(new Date()) + "开始上传");
        String password = "D:" + deviceToken;
        Channel channel1 = tcpConnect(false, deviceKey, deviceKey, password);
        mqttConnect(deviceKey, deviceKey, password, 0, channel1);
        byte[] d = new byte[8];
        ByteBuffer byteBuffer = ByteBuffer.wrap(d);
        int[] generateRandomNumber = NumberUtil.generateRandomNumber(10, 40, 2);
//        for (int i = 0; i < generateRandomNumber.length; i++) {
        Thread.sleep(5000);
        System.out.println("generateRandomNumber[i] = " + generateRandomNumber[0]);
        byteBuffer.asDoubleBuffer().put(generateRandomNumber[0]);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        publish("sys/data/airH/double", 0, byteBuf, channel1);
        Thread.sleep(5000);
        byteBuffer.asDoubleBuffer().put(generateRandomNumber[1]);
        byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        publish("sys/data/airT/double", 0, byteBuf, channel1);
//        }
//        mqttDisconnect();
        System.out.println(DateUtil.formatDateTime(new Date()) + "结束上传");
    }

    public boolean publish(String stream, int qos, ByteBuf byteBuf, Channel channel) {
        if (!mqttSimulator.getConnected().get()) {
            logger.error("the client has not connected to the server");
            return false;
        }
        StringBuilder builder = new StringBuilder(128);
        builder.append(stream);
        String topic = builder.toString();

        MqttQoS mqttQoS = MqttQoS.valueOf(qos);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQoS, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, nextMessageId());
        MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
        if (channel.isWritable()) {
            logger.info("publish data topic={},", topic);
            channel.writeAndFlush(publishMessage);
            //messageId++;
            return true;
        } else {
            return false;
        }
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
            mqttSimulator.setClientId(clientId);
            mqttSimulator.setUserName(userName);
            mqttSimulator.setPassword(password);
            mqttSimulator.setDeviceKey(userName);
            mqttSimulator.getConnected().set(true);
            logger.info("connect success");
            return channel1;
        } else {
            logger.error("the channel is full,connect failed,clientId={}", clientId);
            //return "send connect message failed";
            return null;
        }
    }

    public Channel tcpConnect(boolean isSubscribe, String clientId, String userName, String password) throws Exception {
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
        ChannelFuture channelFuture = bootstrap.connect(configuration.getHost(), configuration.getPort());
        AttributeKey<String> CLIENT_ID = AttributeKey.valueOf("clientId");
        AttributeKey<String> USERNAME = AttributeKey.valueOf("userName");
        AttributeKey<String> PASSWORD = AttributeKey.valueOf("password");
        channelFuture.channel().attr(CLIENT_ID).set(clientId);
        channelFuture.channel().attr(USERNAME).set(userName);
        channelFuture.channel().attr(PASSWORD).set(password);
        channelFuture.sync();
        channel = channelFuture.channel();
        return channelFuture.channel();
    }

    public String mqttDisconnect() {
        if (!mqttSimulator.getConnected().get()) {
            return "the client do not online";
        }

        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage disconnectMessage = new MqttMessage(fixedHeader);
        if (channel.isWritable()) {
            channel.writeAndFlush(disconnectMessage);
            try {
                channel.closeFuture().sync();
                channel.close();
                mqttSimulator.disconnect();
                channel = null;
                System.out.println("disconnect ok!");
                return "disconnect ok!";
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("error occur when disconnect! {}", e);
                return "failed";
            } finally {
            }
        } else {
            return "failed";
        }
    }

    private int nextMessageId() {
        int id = messageId.incrementAndGet();
        while (id > 65000) {
            messageId.compareAndSet(0, id);
            id = messageId.incrementAndGet();
        }
        return id;
    }

}
