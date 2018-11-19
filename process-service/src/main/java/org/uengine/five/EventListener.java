package org.uengine.five;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.uengine.five.events.*;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.five.overriding.ServiceRegisterDeployFilter;
import org.uengine.five.service.DefinitionServiceUtil;
import org.uengine.five.service.InstanceServiceImpl;
import org.uengine.kernel.DefaultProcessInstance;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.kernel.UEngineException;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class EventListener {

    @Autowired
    InstanceServiceImpl instanceService;

    @Autowired
    Streams streams;

    @StreamListener(Streams.INPUT)
    public void handleDone(@Payload ActivityDone activityDone) {

        if(!activityDone.checkMyEvent()) return;

        System.out.println("Received: ");
    }

    @StreamListener(Streams.INPUT)
    @ProcessTransactional
    public void handleFailed(@Payload ActivityFailed activityFailed) throws Exception {

        if(!activityFailed.checkMyEvent()) return;

        try {
            ProcessInstance instance = instanceService.getProcessInstanceLocal(activityFailed.getActivityInfo().getInstanceId());

            instance.fireFault(activityFailed.getActivityInfo().getTracingTag(), new UEngineException(activityFailed.getMessage()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(Streams.INPUT)
    @ProcessTransactional
    public void handleQueued(@Payload ActivityQueued activityQueued) throws Exception {

        if(!activityQueued.checkMyEvent()) return;

        ProcessInstance instance = instanceService.getProcessInstanceLocal(activityQueued.getActivityInfo().getInstanceId());

        try {
            instance.execute(activityQueued.getActivityInfo().getTracingTag());

            ActivityDone activityDone = new ActivityDone();
            activityDone.setActivityInfo(new ActivityInfo());
            activityDone.getActivityInfo().setInstanceId(activityQueued.getActivityInfo().getInstanceId());
            activityDone.getActivityInfo().setTracingTag(activityQueued.getActivityInfo().getTracingTag());

            MessageChannel messageChannel = streams.outboundChannel();
            messageChannel.send(MessageBuilder
                    .withPayload(activityDone)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


        }catch(Exception e){

            ActivityFailed activityFailed = new ActivityFailed();
            activityFailed.setActivityInfo(new ActivityInfo());
            activityFailed.getActivityInfo().setInstanceId(activityQueued.getActivityInfo().getInstanceId());
            activityFailed.getActivityInfo().setTracingTag(activityQueued.getActivityInfo().getTracingTag());

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            activityFailed.setMessage("[" + e.getClass().getName() + "]" + e.getMessage() + ":" + stringWriter.toString());

            MessageChannel messageChannel = streams.outboundChannel();
            messageChannel.send(MessageBuilder
                    .withPayload(activityFailed)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


        }

    }

    @StreamListener(Streams.INPUT)
    @ProcessTransactional
    public void handleDeployed(@Payload DefinitionDeployed definitionDeployed) {
        if(!definitionDeployed.checkMyEvent()) return;

        String definitionPath = definitionDeployed.getDefintionId();

        if(definitionPath!=null)
            try {
                Object definition = definitionServiceUtil.getDefinition(definitionPath);
                serviceRegisterDeployFilter.beforeDeploy((ProcessDefinition) definition,null, definitionPath, true);
            } catch (Exception e) {
                throw new RuntimeException("failed to register a service for :"+ definitionDeployed.getDefintionId(),e);
            }

    }

    @Autowired
    ServiceRegisterDeployFilter serviceRegisterDeployFilter;

    @Autowired
    DefinitionServiceUtil definitionServiceUtil;

}
