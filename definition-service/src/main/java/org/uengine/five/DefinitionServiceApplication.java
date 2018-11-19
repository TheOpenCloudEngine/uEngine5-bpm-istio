package org.uengine.five;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.*;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.cloud.services.AppService;
import org.uengine.five.overriding.EventSendingDeployFilter;
import org.uengine.five.service.*;
import org.uengine.kernel.DeployFilter;
import org.uengine.modeling.resource.*;

import javax.sql.DataSource;

@SpringBootApplication
@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = {DefinitionServiceImpl.class, AppGenerationService.class})
@EnableFeignClients(basePackageClasses = {AppService.class})
public class DefinitionServiceApplication extends Metaworks4BaseApplication {

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManagerProvider
     * @param transactionManagerCustomizers
     */
    protected DefinitionServiceApplication(DataSource dataSource, JpaProperties properties,
                                           ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
                                           ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        SpringApplication.run(DefinitionServiceApplication.class, args);
    }


    @Bean
    public ResourceManager resourceManager() {
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.setStorage(storage());
        return resourceManager;
    }


    @Bean
    /**
     *
     * <bean class="CouchbaseStorage">
     *    <property name="basePath" value="/"/>
     <property name="bucketName" value="default"/>
     <property name="serverIp" value="localhost"/>
     </bean>
     */
    public Storage storage() {
        LocalFileStorage storage = new LocalFileStorage();
        storage.setBasePath("/oce/repository");

        try {
            System.out.println("-------------------> " + storage.exists(new DefaultResource(".")) + " ---> file system is mounted.");
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        };

        return storage;
    }


    @Bean
    @Scope("prototype")
    public VersionManager versionManager(){
        SimpleVersionManager simpleVersionManager = new SimpleVersionManager();
        simpleVersionManager.setAppName("codi");
        //simpleVersionManager.setModuleName("definition");

        return simpleVersionManager;
    }


    //-------------------------------

    @Bean
    public DeployFilter serviceRegisterDeployFilter(){
        return new EventSendingDeployFilter();
    }



}