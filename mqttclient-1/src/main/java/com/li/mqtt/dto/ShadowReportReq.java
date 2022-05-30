package com.li.mqtt.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ShadowReportReq {
    private String sn;
    private String deviceKey;
    private String deviceToken;
    private Map<String, String> measurements;
}
