package com.li.mqtt.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Device {
    private String version;
    private Map<String, String> measurements;

}
