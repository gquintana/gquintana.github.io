package com.github.gquintana.kafka;

import com.github.charithe.kafka.KafkaHelper;
import com.github.charithe.kafka.KafkaJunitExtension;
import com.github.charithe.kafka.KafkaJunitExtensionConfig;
import com.github.charithe.kafka.StartupMode;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.gquintana.kafka.MessageServiceHelper.sendAndConsume;
import static org.assertj.core.api.Assertions.assertThat;

//tag::start[]
@ExtendWith(KafkaJunitExtension.class) //<1>
@KafkaJunitExtensionConfig(startupMode = StartupMode.WAIT_FOR_STARTUP)
public class CharitheMessageServiceIT {

    private static final String TOPIC = "kafka_junit";

    @Test
    void testSendAndConsume(KafkaHelper kafkaHelper) throws Exception { //<2>
        String bootstrapServers = kafkaHelper.producerConfig().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG).toString();
        sendAndConsume(bootstrapServers, TOPIC);
    }
    //end::start[]

    @Test
    void testAPI(KafkaHelper kafkaHelper) throws Exception {
        //tag::api[]
        ListenableFuture<List<String>> futureMessages = kafkaHelper.consumeStrings(TOPIC, 3); //<1>
        kafkaHelper.produceStrings(TOPIC, "one", "two", "three"); //<2>
        List<String> messages = futureMessages.get(5, TimeUnit.SECONDS);
        assertThat(messages).contains("one", "two", "three");
        //end::api[]
    }
}
