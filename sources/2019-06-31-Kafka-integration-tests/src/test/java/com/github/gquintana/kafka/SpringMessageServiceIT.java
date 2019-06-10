package com.github.gquintana.kafka;

import kafka.Kafka;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;

import static com.github.gquintana.kafka.MessageServiceHelper.sendAndConsume;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.value;

//tag::start[]
public class SpringMessageServiceIT {
    private static final String TOPIC = "spring";
    @ClassRule //<1>
    public static EmbeddedKafkaRule kafka = new EmbeddedKafkaRule(1,
            false, TOPIC);
    @Test
    public void testSendAndConsume() throws Exception {
        sendAndConsume(kafka.getEmbeddedKafka().getBrokersAsString(), TOPIC);
    }
    //end::start[]

    @Test
    public void testAPI() throws Exception {
        //tag::api[]
        try(Consumer<Integer, String> consumer = new KafkaConsumer<Integer, String>( //<1>
                KafkaTestUtils.consumerProps("spring_group", "true", kafka.getEmbeddedKafka()))) {
            KafkaTemplate<Integer, String> template = new KafkaTemplate<>( //<2>
                    new DefaultKafkaProducerFactory<>(
                            KafkaTestUtils.producerProps(kafka.getEmbeddedKafka())));
            consumer.subscribe(Collections.singleton(TOPIC));

            template.send(TOPIC, "one");
            template.send(TOPIC, "two");

            ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer); //<3>
            assertThat(records).are(value("one")); //<4
            //end::api[]
        }

    }

}
