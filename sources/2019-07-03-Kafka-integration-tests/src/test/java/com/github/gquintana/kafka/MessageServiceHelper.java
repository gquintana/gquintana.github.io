package com.github.gquintana.kafka;

import org.apache.kafka.common.errors.TopicExistsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageServiceHelper {

    public static void sendAndConsume(String bootstrapServers, String topic) throws Exception {
        try(MessageService service = new MessageService(bootstrapServers, topic)) {
            // Create topic
            service.createTopic();

            // Start consumer
            MockMessageListener listener = new MockMessageListener();
            service.consumeStart(listener);

            // Producer some messages
            service.sendSync(1, "One");
            service.sendSync(2, "Two");
            service.sendSync(3, "Three");

            // Wait for messages to be consumed
            listener.wait(3, 10000L);
        }
    }

    private static class MockMessageListener implements MessageListener {
        private final List<String> values = Collections.synchronizedList(new ArrayList<>());
        @Override
        public void onMessage(Integer key, String value) {
            values.add(value);
        }

        public void wait(int messages, long timeout) throws InterruptedException {
            for (int i = 0; i < 10; i++) {
                if (values.size()>=messages) {
                    return;
                }
                Thread.sleep(timeout/10L);
            }
            assertThat(values.size()).as("Received message count").isGreaterThanOrEqualTo(messages);
        }
    }
}