
package org.uengine.bpm.eventconsumer.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ExecutorService es = Executors.newFixedThreadPool(1);

        Consumer consumer = null;

        consumer = new Consumer();
        es.execute(consumer);

    }

}
