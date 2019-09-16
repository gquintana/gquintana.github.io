package com.github.gquintana.kafka;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static com.github.gquintana.kafka.MessageServiceHelper.sendAndConsume;

//tag::start[]
@Testcontainers //<1>
public class ContainersMessageServiceIT {
    private static final String TOPIC = "containers";
    @Container //<2>
    public KafkaContainer kafka = new KafkaContainer("5.2.1");

    @Test
    public void testSendAndConsume() throws Exception {
        sendAndConsume(kafka.getBootstrapServers(), TOPIC);
    }
//end::start[]
}
