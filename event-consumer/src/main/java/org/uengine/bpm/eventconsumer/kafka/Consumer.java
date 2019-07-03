package org.uengine.bpm.eventconsumer.kafka;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class Consumer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Consumer.class.getName());
    private static final String CONSUMER_GROUP = "a-group";
    private final AtomicBoolean CONSUMER_STOPPED = new AtomicBoolean(false);
    private KafkaConsumer consumer = null;
    private List<String> topicNames;
    Map<String, List<String>> topicAndDefinitionIds;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    EventToBeProcessedRepository eventToBeProcessedRepository;

    @PostConstruct
    public void init() {

        Iterable<Subscription> subscriptionSet = subscriptionRepository.findAll();

        topicNames = new ArrayList<>();
        topicAndDefinitionIds = new HashMap<>();

        for(Subscription subscription: subscriptionSet){
            topicNames.add(subscription.getTopic());

            if(!topicAndDefinitionIds.containsKey(subscription.getTopic()))
                topicAndDefinitionIds.put(subscription.getTopic(), new ArrayList<String>());

            List<String> definitionIds = topicAndDefinitionIds.get(subscription.getTopic());

            definitionIds.add(subscription.getDefId());
        }

        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer(kafkaProps);

    }

    /**
     * invoke this to stop this consumer
     */
    public void stop() {
        LOGGER.log(Level.INFO, "signalling shut down for consumer");
        if (consumer != null) {
            CONSUMER_STOPPED.set(true);

            consumer.wakeup();
        }

        LOGGER.log(Level.INFO, "initiating shut down for consumer");
    }

    @Override
    public void run() {
        consume();
    }

    /**
     * poll the topic
     */
    private void consume() {

        consumer.subscribe(topicNames);

        try {
            while (!CONSUMER_STOPPED.get()) {
                LOGGER.log(Level.INFO, "Polling broker");
                ConsumerRecords<String, String> msg = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : msg) {
                    List<String> subscribedDefIds = topicAndDefinitionIds.get(record.topic());

                    for(String defId : subscribedDefIds){
                        //BPM으로 REST 호출... 실패시엔? 재시도?? 재시도를 위한 테이블 구현필요?
                        EventToBeProcessed eventToBeProcessed = new EventToBeProcessed();
                        eventToBeProcessed.setTopic(record.topic());

                        JsonNode jsonNode = objectMapper.readValue(record.value(), Map.class);

                        String corrKeyValue = jsonNode.get(corrKeyByDefId.get(defId));

                        eventToBeProcessed.setCorrKey(corrKeyValue);
                        eventToBeProcessed.setDone(false);
                        eventToBeProcessed.setDefId(defId);
                        eventToBeProcessed.setMessage(record.value());

                        eventToBeProcessedRepository.save(eventToBeProcessed);  //insert event to be processed

                    }

                    LOGGER.log(Level.INFO, "Key: {0}", record.key());
                    LOGGER.log(Level.INFO, "Value: {0}", record.value());
                    LOGGER.log(Level.INFO, "Partition: {0}", record.partition());
                    LOGGER.log(Level.INFO, "---------------------------------------");
                }

            }
            LOGGER.log(Level.INFO, "Poll loop interrupted");
        } catch (Exception e) {
            //LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (!CONSUMER_STOPPED.get()) {
                throw e;
            }
        } finally {
            consumer.close();
            LOGGER.log(Level.INFO, "consumer shut down complete");
        }
    }
}
