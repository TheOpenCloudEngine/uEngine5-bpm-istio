package org.uengine.kernel;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.uengine.five.ProcessServiceApplication;
import org.uengine.kernel.bpmn.HttpHeader;
import org.uengine.kernel.bpmn.SequenceFlow;
import org.uengine.kernel.bpmn.ServiceTask;
import org.uengine.kernel.bpmn.StartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2018. 11. 14..
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
        classes = ProcessServiceApplication.class)

@TestPropertySource(
        locations = "classpath:application-test.yml")
public class ServiceTaskTest extends AbstractTestNGSpringContextTests {


    @Autowired
    private TestRestTemplate template;


    @BeforeClass
    public static void init(){
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }


    @LocalServerPort
    int randomPort;

    @Test
    public void testService() throws Exception {

        DefaultProcessInstance.USE_CLASS = DefaultProcessInstance.class;

        ProcessDefinition definition = new ProcessDefinition();

        StartEvent startEvent = new StartEvent();
        definition.addChildActivity(startEvent);

        ServiceTask serviceTask = new ServiceTask();

        HttpHeader[] headers = new HttpHeader[1];
        headers[0].setName("access_token");
        headers[0].setValue("AAA");
        serviceTask.setHeaders(headers);

        serviceTask.setUriTemplate("https://localhost:" + randomPort + "/tests/service");
        serviceTask.setMethod("POST");
        serviceTask.setNoValidationForSSL(true);


        definition.addChildActivity(serviceTask);

        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setSourceActivity(startEvent);
        sequenceFlow.setTargetActivity(serviceTask);

        definition.addSequenceFlow(sequenceFlow);

        definition.afterDeserialization();

        ProcessInstance instance = definition.createInstance();

        try {
            instance.execute();
        } catch (Exception e) {
            e.printStackTrace();

            assert false;
        }

        assert true;

    }

    @TestConfiguration
    static class Config {

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().setConnectTimeout(1)
                    .setReadTimeout(1);
        }

    }
}
