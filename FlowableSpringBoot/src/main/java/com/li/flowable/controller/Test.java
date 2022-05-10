package com.li.flowable.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
//import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    @Autowired
    private ProcessEngine engine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;


//    public String test(){
//        Deployment deploy=null;
//        try {
//
//            BpmnModel bpmnModel=new BpmnModel();
//            //设置流程信息
//            //此信息都可以通过前期自定义数据,使用时再查询
//            org.flowable.bpmn.model.Process process=new org.flowable.bpmn.model.Process();
//            process.setId("test_model_3");
//            process.setName("测试流程图三");
//            //添加流程节点信息---start
//            String startId="startEvent_id_1";
//            String startName="开始_1";
//            String endId="endEvent_id_1";
//            String endName="结束_1";
//            //创建数组存储所有流程节点信息
//            List<FlowElement> elementList=new ArrayList<>();
//            //创建开始节点
//            FlowElement startFlowElement=createStartFlowElement(startId,startName);
//            FlowElement endFlowElement=createEndFlowElement(endId,endName);
//            elementList.add(startFlowElement);
//            elementList.add(endFlowElement);
//
//            //查询普通任务节点信息
//            elementList.addAll(findUserTaskElements());
//
//            //把节点放入process
//            elementList.stream().forEach(item -> process.addFlowElement(item));
//
//            //查询各个节点的关系信息,并添加进流程
//            List<FlowElementPojo> flowElementPojoList =createCirculationSequence();
//            for (FlowElementPojo flowElementPojo:flowElementPojoList){
//                SequenceFlow sequenceFlow=createSequeneFlow(flowElementPojo.getId(),"流转",flowElementPojo.getResourceFlowElementId(),
//                        flowElementPojo.getTargetFlowElementId(),"${a==\"f\"}");
//                process.addFlowElement(sequenceFlow);
//            }
//
//            bpmnModel.addProcess(process);
//
//            //校验bpmModel
//            ProcessValidator processValidator=new ProcessValidatorFactory().createDefaultProcessValidator();
//            List<ValidationError> validationErrorList=processValidator.validate(bpmnModel);
//            if (validationErrorList.size()>0){
//                throw new RuntimeException("流程有误，请检查后重试");
//            }
//
//            String fileName="model_"+activity+"bpmn20.xml";
//
//            //生成自动布局
//            new BpmnAutoLayout(bpmnModel).execute();
//            deploy =repositoryService.createDeployment().addBpmnModel(fileName,bpmnModel)
//                    .tenantId("intelligentAsset")
//                    .deploy();
//        }catch (Exception e){
//
//
//        }finally {
//
//        }
//        return "true";
//    }
//
//    /**
//     * 创建开始节点信息
//     * @return
//     */
//    public FlowElement createStartFlowElement(String id,String name){
//        StartEvent startEvent=new StartEvent();
//        startEvent.setId(id);
//        startEvent.setName(name);
//        return startEvent;
//    }
//
//
//    /**
//     * 创建结束节点信息
//     * @param id
//     * @param name
//     * @return
//     */
//    public FlowElement createEndFlowElement(String id,String name){
//        EndEvent endEvent=new EndEvent();
//        endEvent.setId(id);
//        endEvent.setName(name);
//        return endEvent;
//    }
//
//    /**
//     * 创建普通任务节点信息
//     * @param id
//     * @param name
//     * @param assignee
//     * @return
//     */
//    public FlowElement createCommonUserTask(String id,String name,String assignee){
//        UserTask userTask=new UserTask();
//        userTask.setId(id);
//        userTask.setName(name);
//        userTask.setAssignee(assignee);
//        return userTask;
//    }
//
//
//    /**
//     * 创建会签节点信息
//     * @param id
//     * @param name
//     * @return
//     */
//    public FlowElement createMultiUserTask(String id,String name){
//        UserTask userTask=new UserTask();
//        userTask.setId(id);
//        userTask.setName(name);
//        //分配用户
//        userTask.setAssignee("${assignee}");
//        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=new MultiInstanceLoopCharacteristics();
////        multiInstanceLoopCharacteristics.setCollectionString("${collectionList}");
//        //完成条件,默认所有人都完成
//        multiInstanceLoopCharacteristics.setCompletionCondition("${completionCondition}");
//        //元素变量多实例,一般和设置的assignee变量是对应的
//        multiInstanceLoopCharacteristics.setElementVariable("assignee");
//        //集合多实例,用于接收集合数据的表达式
//        multiInstanceLoopCharacteristics.setInputDataItem("${itemList}");
//        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
//        return userTask;
//
//
//    }
//
//    /**
//     * 查询各节点关联流转信息,即流转线
//     *FlowElementPojo 是自定义类
//     */
//    public List<FlowElementPojo> createCirculationSequence(){
//
//
//        List<FlowElementPojo> list=new ArrayList<>();
//        FlowElementPojo flowElementPojo_start=new FlowElementPojo();
//        flowElementPojo_start.setId("sequence_id_1");
//        flowElementPojo_start.setTargetFlowElementId("userTask_0");
//        flowElementPojo_start.setResourceFlowElementId("startEvent_id_1");
//        flowElementPojo_start.setFlowElementType("sequence");
//
//        FlowElementPojo flowElementPojo_user_0=new FlowElementPojo();
//        flowElementPojo_user_0.setId("sequence_id_2");
//        flowElementPojo_user_0.setTargetFlowElementId("userTask_1");
//        flowElementPojo_user_0.setResourceFlowElementId("userTask_0");
//        flowElementPojo_user_0.setFlowElementType("sequence");
//
//        FlowElementPojo flowElementPojo_user_1=new FlowElementPojo();
//        flowElementPojo_user_1.setId("sequence_id_3");
//        flowElementPojo_user_1.setTargetFlowElementId("userTask_2");
//        flowElementPojo_user_1.setResourceFlowElementId("userTask_1");
//        flowElementPojo_user_1.setFlowElementType("sequence");
//
//        FlowElementPojo flowElementPojo_user_2=new FlowElementPojo();
//        flowElementPojo_user_2.setId("sequence_id_4");
//        flowElementPojo_user_2.setTargetFlowElementId("endEvent_id_1");
//        flowElementPojo_user_2.setResourceFlowElementId("userTask_2");
//        flowElementPojo_user_2.setFlowElementType("sequence");
//
//        list.add(flowElementPojo_start);
//        list.add(flowElementPojo_user_0);
//        list.add(flowElementPojo_user_1);
//        list.add(flowElementPojo_user_2);
//
//        return list;
//
//    }
//
//    /**
//     * 绘制节点流转顺序
//     * @param id
//     * @param name
//     * @param targetId
//     * @param sourceId
//     * @param conditionExpression
//     * @return
//     */
//    public SequenceFlow createSequeneFlow(String id,String name,String sourceId,String targetId,String conditionExpression){
//        SequenceFlow sequenceFlow=new SequenceFlow();
//        sequenceFlow.setId(id);
//        sequenceFlow.setName(name);
//        if (ObjectUtils.isNotEmpty(targetId)){
//            sequenceFlow.setTargetRef(targetId);
//        }
//        if (ObjectUtils.isNotEmpty(sourceId)){
//            sequenceFlow.setSourceRef(sourceId);
//        }
//        if (ObjectUtils.isNotEmpty(conditionExpression)){
//            sequenceFlow.setConditionExpression(conditionExpression);
//        }
//        return sequenceFlow;
//    }

    @GetMapping("createFlow")
    public String createFlow() {
        StringBuilder taskId = new StringBuilder("task_").append("001");
        int taskIdStart = 0;
        //开始事件
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        startEvent.setName("开始");

        //添加任务节点Listener
        //非spring下可以这样用
        List<FlowableListener> taskListeners = new ArrayList<>();
        FlowableListener listener = new FlowableListener();
        listener.setEvent("all");
        listener.setImplementationType("delegateExpression");
        listener.setImplementation("${permissinTaskListener}");
        //listener.setId("CATEGORYID_FILE_AUDIT_listener");
//        taskListeners.add(listener);

        //主管审批节点
        UserTask supervisorApproval = new UserTask();
        supervisorApproval.setId(taskId.append(taskIdStart).toString());
        taskIdStart++;
        supervisorApproval.setAssignee("${superviso}");
//        supervisorApproval.setCandidateUsers(Arrays.asList("2422042842433986573"));
        supervisorApproval.setName("主管审批");
        supervisorApproval.setTaskListeners(taskListeners);
        //
//        ExclusiveGateway fileGateway = new ExclusiveGateway();
//        fileGateway.setId(gateWayId.append(gateWayIdStart).toString());
//        gateWayIdStart++;

        //  经理审批
        UserTask managerTask = new UserTask();
        managerTask.setId(taskId.append(taskIdStart).toString());
        taskIdStart++;
        managerTask.setAssignee("${manager}");
//        managerTask.setCandidateUsers(Arrays.asList("2198741168097853440"));
        managerTask.setName("经理审批");
//        ExclusiveGateway sealGateway = new ExclusiveGateway();
//        sealGateway.setId(gateWayId.append(gateWayIdStart).toString());
//        gateWayIdStart++;


        // 总经理
        UserTask generalManagerTask = new UserTask();
        generalManagerTask.setAssignee("${generalManager}");
//        generalManagerTask.setCandidateUsers(Arrays.asList("${sealUsers}"));
        generalManagerTask.setId(taskId.append(taskIdStart).toString());
        taskIdStart++;
        generalManagerTask.setCategory("category001");
        generalManagerTask.setName("总经理审批");

        //结束--拒绝情况下
        EndEvent endEvent = new EndEvent();
        endEvent.setId("reject");
        endEvent.setName("拒绝");

        //结束事件--任务正常完成
        EndEvent completeEvent = new EndEvent();
        completeEvent.setId("complete");
        completeEvent.setName("结束");

        // 会签
        UserTask countersignTask = createUserTaskCounterSign(taskId.append(taskIdStart).toString(), "会签");
        taskIdStart++;

        //审批节点
        UserTask approvalTask = new UserTask();
        approvalTask.setId(taskId.append(taskIdStart).toString());
        taskIdStart++;

        // 设置审批人
        approvalTask.setAssignee("${superviso}");
        // 设置候选人。。
//        supervisorApproval.setCandidateUsers(Arrays.asList("2422042842433986573"));
        approvalTask.setName("主管审批");
        approvalTask.setTaskListeners(taskListeners);
        approvalTask.setAssignee("${approval}");


        // 到第一个审批节点
        SequenceFlow sequenceFlow = new SequenceFlow(startEvent.getId(), supervisorApproval.getId());
        // 网关
        SequenceFlow sequenceFlow2 = new SequenceFlow(supervisorApproval.getId(), managerTask.getId());
        sequenceFlow2.setConditionExpression("${approve}");
        sequenceFlow2.setName("同意");

        // 设置拒绝条件及连接任务信息
        SequenceFlow sequenceFlow4 = new SequenceFlow(supervisorApproval.getId(), endEvent.getId());
        sequenceFlow4.setConditionExpression("${!approve}");
        sequenceFlow4.setName("拒绝");

        // 设置同意
        SequenceFlow sequenceFlow5 = new SequenceFlow(managerTask.getId(), countersignTask.getId());
        sequenceFlow5.setConditionExpression("${approve}");
        sequenceFlow5.setName("同意");
        // 设置拒绝
        SequenceFlow sequenceFlow7 = new SequenceFlow(managerTask.getId(), endEvent.getId());
        sequenceFlow7.setConditionExpression("${!approve}");
        sequenceFlow7.setName("拒绝");
        // 设置同意下一节点信息



        // 设置同意
        SequenceFlow sequenceFlow11 = new SequenceFlow(countersignTask.getId(), generalManagerTask.getId());
        sequenceFlow11.setConditionExpression("${nrOfCompletedInstances/nrOfInstances >= 0.5}");
        sequenceFlow11.setName("条件同意");
//        // 设置完成条件 nrOfInstances--实例总数、 nrOfActiveInstances--还没有完成的数量   nrOfCompletedInstances--完成审批的数量
//        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances >= 0.5}");
        // 设置拒绝
        SequenceFlow sequenceFlow12 = new SequenceFlow(countersignTask.getId(), endEvent.getId());
        sequenceFlow12.setConditionExpression("${!approve}");
        sequenceFlow12.setName("条件拒绝");


        SequenceFlow sequenceFlow9 = new SequenceFlow(generalManagerTask.getId(), completeEvent.getId());
        sequenceFlow9.setConditionExpression("${approve}");
        sequenceFlow9.setName("同意");
        SequenceFlow sequenceFlow10 = new SequenceFlow(generalManagerTask.getId(), endEvent.getId());
        sequenceFlow10.setConditionExpression("${!approve}");
        sequenceFlow10.setName("拒绝");

        /*
         * 整合节点和连线成为一个 process
         */
        Process process = new Process();

        // 流程标识
        process.setId("self_serviceSettings");
        process.setName("功能性流程1");
        process.addFlowElement(startEvent);
        process.addFlowElement(supervisorApproval);
        process.addFlowElement(managerTask);
        process.addFlowElement(countersignTask);
        process.addFlowElement(generalManagerTask);
        process.addFlowElement(endEvent);
        process.addFlowElement(completeEvent);
        process.addFlowElement(sequenceFlow);
        process.addFlowElement(sequenceFlow2);
        process.addFlowElement(sequenceFlow4);
        process.addFlowElement(sequenceFlow5);
        process.addFlowElement(sequenceFlow7);
        process.addFlowElement(sequenceFlow11);
        process.addFlowElement(sequenceFlow12);
        process.addFlowElement(sequenceFlow9);
        process.addFlowElement(sequenceFlow10);

        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.addProcess(process);

        // 自动生成布局   布局节点位置
//        new BpmnAutoLayout(bpmnModel).execute();

        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String bytes = new String(convertToXML);
        logger.info("该流程的流程xml为：{}", bytes);

        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
        // 验证失败信息的封装ValidationError
        List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);
        logger.error("获取到的验证信息为：{}", JSONObject.toJSONString(validate));
        //  流程部署
        Deployment deploy = repositoryService.createDeployment()
                .tenantId("tenantId001")
                .addString("分类流程.bpmn", bytes)
                .category("category001").deploy();
        logger.info("======部署id:" + deploy.getId());
        return bytes;
    }

    //会签
    protected UserTask createUserTaskCounterSign(String id, String name) {
        UserTask userTask = new UserTask();
        // 流程名称
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee("${assignee}");

        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        // 设置并行执行(每个审批人可以同时执行）
        multiInstanceLoopCharacteristics.setSequential(false);
        // 设置完成条件 nrOfInstances--实例总数、 nrOfActiveInstances--还没有完成的数量   nrOfCompletedInstances--完成审批的数量
        multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances >= 0.5}");
//        multiInstanceLoopCharacteristics.setLoopCardinality("assigneeList");
        //审批人集合参数
        multiInstanceLoopCharacteristics.setInputDataItem("assigneeList");
        //迭代集合
        multiInstanceLoopCharacteristics.setElementVariable("assignee");


        //设置多实例属性
        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
        //设置监听器
//        userTask.setExecutionListeners(countersignTaskListener());
        //设置审批人
//        userTask.setCandidateUsers(candidateUser);
        return userTask;
    }


}
