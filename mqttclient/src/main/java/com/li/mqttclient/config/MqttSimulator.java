package com.li.mqttclient.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MqttSimulator {
    private static Logger logger = LoggerFactory.getLogger(MqttSimulator.class);
    private String ClientId;
    private String userName;
    private String password;
    private String deviceKey;
    private String deviceToken;
    private AtomicBoolean connected = new AtomicBoolean(false);

    public void disconnect() {
        ClientId = null;
        userName = null;
        password = null;
        deviceKey = null;
        connected.set(false);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        MqttSimulator.logger = logger;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public AtomicBoolean getConnected() {
        return connected;
    }

    public void setConnected(AtomicBoolean connected) {
        this.connected = connected;
    }
}
