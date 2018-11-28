package org.uengine.msa;

import com.fasterxml.jackson.databind.JsonNode;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.bitbucket.eunjeon.seunjeon.LNode;
import org.metaworks.dwr.MetaworksRemoteService;
import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by uengine on 2017. 10. 5..
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {SemanticEntity.class})
public class Application extends Metaworks4BaseApplication {

    @Autowired
    private Environment environment;

    private static Log logger = LogFactory.getLog(Application.class);

    protected Application(DataSource dataSource, JpaProperties properties, ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        SemanticEntityRepository semanticEntityRepository = applicationContext.getBean(SemanticEntityRepository.class);
        SemanticValueRepository semanticValueRepository = applicationContext.getBean(SemanticValueRepository.class);

        {
            SemanticEntity semanticEntity = new SemanticEntity();
            semanticEntity.setId("boolean");
            semanticEntity.setDescription("Logical Boolean");
            semanticEntityRepository.save(semanticEntity);

            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("Yes");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("Y");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("예");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("네");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("옙");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("넵");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("넹");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("그럴게요");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("그렇습니다");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("좋습니다");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("좋아요");
                semanticValue.setValue("true");
                semanticValueRepository.save(semanticValue);
            }

            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("No");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("Nope");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("Nop");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("아뇨");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("아니오");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("아니");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("아니예요");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("싫어");
                semanticValue.setValue("false");
                semanticValueRepository.save(semanticValue);
            }
        }

        {
            SemanticEntity semanticEntity = new SemanticEntity();
            semanticEntity.setId("number");
            semanticEntity.setDescription("Natural Number");
            semanticEntityRepository.save(semanticEntity);

            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("1");
                semanticValue.setValue("1");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("첫");
                semanticValue.setValue("1");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("1번");
                semanticValue.setValue("1");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("2");
                semanticValue.setValue("2");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("2번");
                semanticValue.setValue("2");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("두");
                semanticValue.setValue("2");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("3");
                semanticValue.setValue("3");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("세");
                semanticValue.setValue("3");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("4");
                semanticValue.setValue("4");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("네");
                semanticValue.setValue("4");
                semanticValueRepository.save(semanticValue);
            }

            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("5");
                semanticValue.setValue("5");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("다섯");
                semanticValue.setValue("5");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("6");
                semanticValue.setValue("6");
                semanticValueRepository.save(semanticValue);
            }
            {
                SemanticValue semanticValue = new SemanticValue();
                semanticValue.setSynonym("여섯");
                semanticValue.setValue("6");
                semanticValueRepository.save(semanticValue);
            }
        }


    }



    @RequestMapping("/mean")
    public int mean(@RequestParam("expression") String expression, @RequestParam("value") String value){

        SemanticValueRepository semanticValueRepository = MetaworksRemoteService.getComponent(SemanticValueRepository.class);

        List<String> texts = new ArrayList<String>(); //expression.split(" ");


        for (LNode node : Analyzer.parseJava(expression)) {
            texts.add(node.morpheme().surface());
            //System.out.println(node);
        }

        Map<String, AtomicInteger> weightForValues = new HashMap<String, AtomicInteger>();

        for(String theText : texts){
            List<SemanticValue> semanticValues = semanticValueRepository.findBySynonym(theText);

            String theValue = null;

            if(semanticValues!=null && semanticValues.size()>0){
                theValue = semanticValues.get(0).getValue();

                AtomicInteger curr = weightForValues.get(theValue);

                if(curr==null) curr = new AtomicInteger(0);
                curr.incrementAndGet();

                weightForValues.put(theValue, curr);
            }

        }

        int total = 0;
        int max = 0, latterMax = 0;
        String maxAnswer = null;

        for(String answer: weightForValues.keySet()){

            AtomicInteger weight = weightForValues.get(answer);

            total = total + weight.get();

            if(weight.get() > max){
                latterMax = max;
                max = weight.get();

                maxAnswer = answer;
            }
        }

        if(total == 0) return 0;

        int percentage = max * 100 / total;

        if(maxAnswer!=null && percentage > 0){
            if(maxAnswer.equals(value)) return percentage;
        }

        return 0;
    }

    @RequestMapping(value = "/entity-value", produces = "application/json;charset=UTF-8")
    public String entityValue(@RequestParam("expression") String expression, @RequestParam("entity-type") String entityType){

        SemanticValueRepository semanticValueRepository = MetaworksRemoteService.getComponent(SemanticValueRepository.class);
        SemanticEntityRepository semanticEntityRepository = MetaworksRemoteService.getComponent(SemanticEntityRepository.class);

        List<String> texts = new ArrayList<String>(); //expression.split(" ");

        SemanticEntity semanticEntity = semanticEntityRepository.findOne(entityType);


        for (LNode node : Analyzer.parseJava(expression)) {
            if(semanticEntity==null){
                if(node.morpheme().feature().contains(entityType))
                    return node.morpheme().surface();
            }

            texts.add(node.morpheme().surface());
            //System.out.println(node);
        }

        if(semanticEntity==null) return "";//throw new ResourceNotFoundException();

        Map<String, AtomicInteger> weightForValues = new HashMap<String, AtomicInteger>();

        for(String theText : texts){
            List<SemanticValue> semanticValues = semanticValueRepository.findBySynonym(theText);

            String theValue = null;

            if(semanticValues!=null && semanticValues.size()>0){
                theValue = semanticValues.get(0).getValue();

                AtomicInteger curr = weightForValues.get(theValue);

                if(curr==null) curr = new AtomicInteger(0);
                curr.incrementAndGet();

                weightForValues.put(theValue, curr);
            }

        }

        int total = 0;
        int max = 0, latterMax = 0;
        String maxAnswer = null;

        for(String answer: weightForValues.keySet()){

            AtomicInteger weight = weightForValues.get(answer);

            total = total + weight.get();

            if(weight.get() > max){
                latterMax = max;
                max = weight.get();

                maxAnswer = answer;
            }
        }

        if(total == 0) return "";//throw new org.springframework.data.rest.webmvc.ResourceNotFoundException();

        int percentage = max * 100 / total;

        if(maxAnswer!=null && percentage > 0){
            return maxAnswer;
        }

        return "";//throw new org.springframework.data.rest.webmvc.ResourceNotFoundException();
    }


}