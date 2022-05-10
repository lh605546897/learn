//package com.li.flowable.controller;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.flowable.ui.common.service.exception.InternalServerErrorException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/app")
//public class FlowableStencilSetResource {
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(FlowableStencilSetResource.class);
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    @RequestMapping(value = "/rest/stencil-sets/editor", method = RequestMethod.GET, produces =
//            "application/json")
//    public JsonNode getStencilSetForEditor() {
//        try {
//            JsonNode stencilNode =
//                    objectMapper.readTree(this.getClass().getClassLoader().
//                            getResourceAsStream("stencilset/stencilse/t_bpmn.json"));
//            return stencilNode;
//        } catch (Exception e) {
//            LOGGER.error("Error reading bpmn stencil set json", e);
//            throw new InternalServerErrorException("Error reading bpmn stencil set json");
//        }
//    }
//
//    @RequestMapping(value = "/rest/stencil-sets/cmmneditor", method = RequestMethod.GET,
//            produces = "application/json")
//    public JsonNode getCmmnStencilSetForEditor() {
//        try {
//            JsonNode stencilNode =
//                    objectMapper.readTree(this.getClass().getClassLoader().
//                            getResourceAsStream("stencilset/stencilse/t_cmmn.json"));
//            return stencilNode;
//        } catch (Exception e) {
//            LOGGER.error("Error reading bpmn stencil set json", e);
//            throw new InternalServerErrorException("Error reading bpmn stencil set json");
//        }
//    }
//
//}
