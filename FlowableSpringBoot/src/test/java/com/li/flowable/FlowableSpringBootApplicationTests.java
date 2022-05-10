package com.li.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class FlowableSpringBootApplicationTests {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() {
        System.out.println("processEngine = " + processEngine);
    }

    @Test
    void startFlow(){
        Map<String,Object> map = new HashMap<>();
        map.put("superviso","zhangsan");
        map.put("manager","lisi");
        map.put("assignee","wangwu");
        map.put("generalManager","liuliu");
        runtimeService.startProcessInstanceById("self_serviceSettings:2:f53ca181-cf74-11ec-996d-165afc0cdf03",map);
    }

    @Test
    void completeTask(){
        Task task = taskService.createTaskQuery()
                .processInstanceId("26ee7e0b-cf75-11ec-b795-165afc0cdf03")
                .taskAssignee("zhangsan")
                .singleResult();
        if(task != null){
            Map<String,Object> map = new HashMap<>();
            map.put("approve",true);
            taskService.complete(task.getId(),map);
            System.out.println("complete");
        }
    }

    @Test
    void completeTask1(){
        Task task = taskService.createTaskQuery()
                .processInstanceId("26ee7e0b-cf75-11ec-b795-165afc0cdf03")
                .taskAssignee("lisi")
                .singleResult();
        if(task != null){
            Map<String,Object> map = new HashMap<>();
            map.put("approve",false);
            taskService.complete(task.getId(),map);
            System.out.println("complete");
        }
    }
}
