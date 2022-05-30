package com.li.mqtt.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchDataStream {
    private String sn;

    private String deviceKey;

    private String deviceToken;

    private List<DataStream> dataStreams;
}
