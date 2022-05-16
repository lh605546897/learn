package com.li.mqttclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("topic")
public class TopicConfig {
    private String topicWelcomeStart;

    private String topicCmdStart;

    private String topicDataStart;

    private String topicCtlStart;

    private String topicError;

    //子设备相关topic
    private String topicProxyNotifyStart;

    public String getTopicWelcomeStart() {
        return topicWelcomeStart;
    }

    public void setTopicWelcomeStart(String topicWelcomeStart) {
        this.topicWelcomeStart = topicWelcomeStart;
    }

    public String getTopicCmdStart() {
        return topicCmdStart;
    }

    public void setTopicCmdStart(String topicCmdStart) {
        this.topicCmdStart = topicCmdStart;
    }

    public String getTopicDataStart() {
        return topicDataStart;
    }

    public void setTopicDataStart(String topicDataStart) {
        this.topicDataStart = topicDataStart;
    }

    public String getTopicCtlStart() {
        return topicCtlStart;
    }

    public void setTopicCtlStart(String topicCtlStart) {
        this.topicCtlStart = topicCtlStart;
    }

    public String getTopicError() {
        return topicError;
    }

    public void setTopicError(String topicError) {
        this.topicError = topicError;
    }

    public String getTopicProxyNotifyStart() {
        return topicProxyNotifyStart;
    }

    public void setTopicProxyNotifyStart(String topicProxyNotifyStart) {
        this.topicProxyNotifyStart = topicProxyNotifyStart;
    }
}
