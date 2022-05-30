package com.li.mqtt.controller;

import cn.hutool.cache.Cache;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.li.mqtt.dto.DataStream;
import com.li.mqtt.dto.Device;
import com.li.mqtt.util.TopicConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class DataPushTask extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(DataPushTask.class);
    private String sn;
    private String deviceKey;
    private String deviceToken;
    private String topic;
    private List<DataStream> dataStreams;

    private AtomicInteger messageId = new AtomicInteger(1);
    private Channel channel;
    @Autowired
    private Cache deviceCache;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<DataStream> getDataStreams() {
        return dataStreams;
    }

    public void setDataStreams(List<DataStream> dataStreams) {
        this.dataStreams = dataStreams;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DataPushTask(String deviceKey, String deviceToken, String topic, Channel channel, List<DataStream> dataStreams) {
        this.deviceKey = deviceKey;
        this.deviceToken = deviceToken;
        this.topic = topic;
        this.channel = channel;
        this.dataStreams = dataStreams;
    }

    @Override
    public void run() {
        try {
            ByteBuf byteBuf = null;
            if (topic.startsWith(TopicConstants.topicDataStart)) {
                byteBuf = generateData(1);
            } else if (topic.startsWith(TopicConstants.topicShadowReport)) {
                JSONObject shadow = new JSONObject();
                Device device = (Device) deviceCache.get(sn);
                Integer oldVersion = Integer.valueOf(device.getVersion());
                Integer newVersion = oldVersion + 1;
                device.setVersion(newVersion.toString());
                deviceCache.put(deviceKey, device);
                shadow.putOpt("version", oldVersion + 1);
                JSONObject data = new JSONObject();
                shadow.putOpt("data", data);
                data.putOpt("measurements", device.getMeasurements());
//                    String json = "{\n" +
//                            "    \"version\":\"11\",\n" +
//                            "    \"data\":{\n" +
//                            "        \"measurements\":{\n" +
//                            "            \"B000019\":\"on\"\n" +
//                            "        },\n" +
//                            "        \"timestamp\":1653032242066\n" +
//                            "    }\n" +
//                            "}";
                String json = shadow.toString();
                byteBuf = Unpooled.buffer();
                byteBuf.writeBytes(json.getBytes());
            } else if (topic.startsWith(TopicConstants.topicShadowFetch)) {
                byteBuf = Unpooled.buffer();
            } else {
                JSONObject jsonObject = new JSONObject();
                JSONArray data = new JSONArray();
                jsonObject.putOpt("data", data);
                for (DataStream dataStream : dataStreams) {
                    JSONObject object = new JSONObject();
                    object.putOpt("stream", dataStream.getStream());
                    object.putOpt("encodeType", dataStream.getEncodeType());
                    object.putOpt("data", String.valueOf(NumberUtil.generateRandomNumber(10, 40, 1)[0]));
                    data.add(object);
                }
                byteBuf = Unpooled.buffer();
                byteBuf.writeBytes(jsonObject.toString().getBytes());
            }

            publish(topic, 0, byteBuf, channel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error ");
        }
    }

    private ByteBuf generateData(Integer size) {
        byte[] d = new byte[8];
        ByteBuffer byteBuffer = ByteBuffer.wrap(d);
        int[] generateRandomNumber = NumberUtil.generateRandomNumber(10, 40, size);
        byteBuffer.asDoubleBuffer().put(generateRandomNumber[0]);
        return Unpooled.wrappedBuffer(byteBuffer);
    }

    public boolean publish(String topic, int qos, ByteBuf byteBuf, Channel channel) {

        MqttQoS mqttQoS = MqttQoS.valueOf(qos);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQoS, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, nextMessageId());
        MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
        if (channel.isWritable()) {
            logger.info("publish data topic={},", topic);
            channel.writeAndFlush(publishMessage);
            return true;
        } else {
            return false;
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
