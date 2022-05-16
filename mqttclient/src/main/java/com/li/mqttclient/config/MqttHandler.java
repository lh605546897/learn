package com.li.mqttclient.config;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class MqttHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(MqttHandler.class);
    @Autowired
    private MqttSimulator mqttSimulator;
    @Autowired
    private TopicConfig topicConfig;

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive!");
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive!!!!");
        if (mqttSimulator.getConnected().get()) {
            mqttSimulator.getConnected().set(false);
            mqttSimulator.setClientId(null);
            mqttSimulator.setUserName(null);
            mqttSimulator.setPassword(null);
            mqttSimulator.setDeviceKey(null);
            logger.info("tcp连接断开");
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收服务端消息
        MqttMessage mqttMessage = (MqttMessage) msg;
        MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
        //System.out.println(mqttMessageType.value());
        switch (mqttMessageType) {
            case CONNACK: {
                MqttConnAckMessage ackMessage = (MqttConnAckMessage) msg;
                MqttConnectReturnCode returnCode = ackMessage.variableHeader().connectReturnCode();
                MqttQoS qos = ackMessage.fixedHeader().qosLevel();
                Object object = ackMessage.payload();
                logger.info("returnCode={},qos={},payload={}", returnCode, qos, object);
            }
            break;
            case PUBLISH: {
                MqttPublishMessage getPublishMessage = (MqttPublishMessage) msg;
                MqttQoS qos = getPublishMessage.fixedHeader().qosLevel();
                String topic = getPublishMessage.variableHeader().topicName();
                ByteBuf byteBuf = getPublishMessage.payload();
                //int length=byteBuf.readableBytes();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                String data = new String(bytes);
                logger.info("topic={},qos={},data={}", topic, qos, data);
                if (topic.startsWith(topicConfig.getTopicWelcomeStart())) {
                    messageArrivedWelcome(topic, data, ctx.channel());
                    //System.out.println(deviceInfo.getDeviceKey()+":"+deviceInfo.getTime());
                } else if (topic.startsWith(topicConfig.getTopicCmdStart())) {
                    System.out.println("cmd");
//                    messageArrivedCmd(topic, data);
                } else if (topic.startsWith(topicConfig.getTopicCtlStart())) {
                    System.out.println("TopicCtl");
//                    messageArrivedCtl(topic, data);
                } else if (topic.startsWith(topicConfig.getTopicDataStart())) {

                } else if (topic.startsWith(topicConfig.getTopicProxyNotifyStart())) {
                    System.out.println("Proxy");
//                    messageArrivedProxyNotify(topic, data);
                } else if (topic.startsWith(topicConfig.getTopicError())) {
                    logger.info("receive error: {}", new String(data.getBytes()));
                }
                //logger.info("topic={},qos={},data={}",topic,qos,data);
                //System.out.println(deviceInfo.getDeviceKey()+":"+deviceInfo.getTime());
                switch (qos) {
                    case AT_MOST_ONCE: {
                        //logger.info("topic={},qos={},data={}",topic,qos,data);
                    }
                    break;
                    case AT_LEAST_ONCE: {
                        //对qos1 等级的publish报文响应，剩余可变报文长度即剩余长度字段为2，无有效载荷
                        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, qos, false, 2);
                        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(getPublishMessage.variableHeader().messageId());
                        MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, variableHeader);
                        if (ctx.channel().isWritable()) {
                            logger.info("publishAck data topic={},", topic);
                            ctx.channel().writeAndFlush(pubAckMessage);
                        }
                    }
                    break;
                    case EXACTLY_ONCE: {
                        //对qos2等级的publish报文的响应，是qos2等级协议的第二个报文，剩余长度字段为2
                        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, qos, false, 2);
                        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(getPublishMessage.variableHeader().messageId());
                        MqttMessage pubRecMqttMessage = new MqttMessage(fixedHeader, variableHeader);
                        if (ctx.channel().isWritable()) {
                            logger.info(" pubRec data topic={},", topic);
                            ctx.channel().writeAndFlush(pubRecMqttMessage);
                        }
                    }
                    break;
                    case FAILURE: {

                    }
                    default: {

                    }
                }
            }
            break;
            case PUBACK: {
                MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) msg;
                MqttQoS qos = pubAckMessage.fixedHeader().qosLevel();
                int messageId = pubAckMessage.variableHeader().messageId();
                Object object = pubAckMessage.payload();
                logger.info("publishAck message,the qos={},messageId={},payload={}", qos, messageId, object);

            }
            break;
            //发布收到，qos=2，第一步
            case PUBREC: {
                logger.info("pubRec");
            }
            break;
            //发布释放，qos=2,第二步
            case PUBREL: {
                logger.info("pubRel");
            }
            break;
            //发布完成，qos=2，第三步
            case PUBCOMP: {
                logger.info("puComp");
            }
            break;
            case PINGREQ: {
                logger.info("pingReq");
            }
            break;
            case PINGRESP: {
                //logger.info("pingResp");
            }
            break;
            case SUBSCRIBE: {
                logger.info("subscribe");
            }
            break;
            case SUBACK: {
                logger.info("subAck");
            }
            break;
            case UNSUBACK: {
                logger.info("unSuBack");
            }
            break;
            case DISCONNECT: {
                logger.info("disconnect");
            }
            break;
            case UNSUBSCRIBE: {
                logger.info("unsubscribe");
            }
            break;
            case CONNECT: {
                logger.info("connect");
            }
            default: {
                logger.error("unkown error");
            }
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//         System.out.println("read complete！");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("has exception!");
        System.out.println(cause.getMessage());
    }

    /**
     *
     * @param topic
     * @param data
     */
    private void messageArrivedWelcome(String topic, String data, Channel channel) {
        logger.info("topic = {}", topic);
    }


}
