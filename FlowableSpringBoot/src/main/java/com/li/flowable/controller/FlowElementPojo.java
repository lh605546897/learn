package com.li.flowable.controller;

public class FlowElementPojo {

    private String id;
    private String targetFlowElementId;
    private String resourceFlowElementId;
    private String flowElementType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetFlowElementId() {
        return targetFlowElementId;
    }

    public void setTargetFlowElementId(String targetFlowElementId) {
        this.targetFlowElementId = targetFlowElementId;
    }

    public String getResourceFlowElementId() {
        return resourceFlowElementId;
    }

    public void setResourceFlowElementId(String resourceFlowElementId) {
        this.resourceFlowElementId = resourceFlowElementId;
    }

    public String getFlowElementType() {
        return flowElementType;
    }

    public void setFlowElementType(String flowElementType) {
        this.flowElementType = flowElementType;
    }
}
