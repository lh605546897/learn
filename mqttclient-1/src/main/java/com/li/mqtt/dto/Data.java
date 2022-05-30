package com.li.mqtt.dto;

public class Data {
    private DataStream dataStream;

    private Object data;

    public DataStream getDataStream() {
        return dataStream;
    }

    public void setDataStream(DataStream dataStream) {
        this.dataStream = dataStream;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
