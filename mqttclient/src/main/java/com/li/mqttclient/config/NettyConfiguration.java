package com.li.mqttclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("netty")
public class NettyConfiguration {
    private String host;
    private int port;
    private int keepAliveSecond;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getKeepAliveSecond() {
        return keepAliveSecond;
    }

    public void setKeepAliveSecond(int keepAliveSecond) {
        this.keepAliveSecond = keepAliveSecond;
    }
}
