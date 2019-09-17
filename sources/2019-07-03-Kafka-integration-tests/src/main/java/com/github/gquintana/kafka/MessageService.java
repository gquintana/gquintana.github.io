package com.github.gquintana.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageService implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private final Producer<Integer, String> producer;
    private final String bootstrapServers;
    private final String topic;
    private final ExecutorService executorService;
    private ConsumerTask task;

    public MessageService(String bootstrapServers, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.executorService = Executors.newFixedThreadPool(4, this::createThread);

        Map<String, Object> producerConfig = new HashMap<>();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 500L);
        producer = new KafkaProducer<Integer, String>(producerConfig);

    }

    private Thread createThread(Runnable runnable) {
        return new Thread(runnable,"message-consumer-" + System.currentTimeMillis());
    }

    public void createTopic() {
        Map<String, Object> adminConfig = new HashMap<>();
        adminConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try(AdminClient adminClient = AdminClient.create(adminConfig)) {
            NewTopic topicRequest = new NewTopic(topic, 1, (short) 1);
            CreateTopicsResult topicResponse = adminClient.createTopics(Collections.singletonList(topicRequest));
            topicResponse.all().get(1L, TimeUnit.SECONDS);
            LOGGER.info("Topic {} created", topic);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException|TimeoutException e) {
            if (e.getCause() instanceof TopicExistsException) {
                return;
            } else if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new MessageException("Failed to create topic", e.getCause());
            }
        }
    }

    public void sendSync(Integer key, String value) {
        try {
            LOGGER.info("Producer send {} to {}", value, topic);
            producer.send(new ProducerRecord<>(topic, key, value)).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new MessageException("Failed to send message", e.getCause());
            }
        }
    }

    public void consumeStart(MessageListener messageListener) throws InterruptedException {
        if (task != null) {
            throw new MessageException("Listener already running");
        }
        task = new ConsumerTask(messageListener);
        executorService.submit(task);
        task.waitPartitionsAssigned();

    }

    public void consumeStop() {
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    private class ConsumerTask implements Runnable, ConsumerRebalanceListener {
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final CountDownLatch latch = new CountDownLatch(1);
        private final MessageListener listener;

        public ConsumerTask(MessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {

        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
            if (!collection.isEmpty()) {
                LOGGER.info("Consumer partitions assigned {}", collection);
                latch.countDown();
            }
        }

        public void waitPartitionsAssigned() throws InterruptedException {
            latch.await(3L, TimeUnit.SECONDS);
        }

        @Override
        public void run() {
            Map<String, Object> consumerConfig = new HashMap<>();
            consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
            consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "message_group");
            try (Consumer<Integer, String> consumer = new KafkaConsumer<Integer, String>(consumerConfig)) {
                consumer.subscribe(Collections.singletonList(topic), this);
                LOGGER.info("Consumer started on {}", topic);
                while (running.get()) {
                    ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(500L));
                    for (ConsumerRecord<Integer, String> record : records) {
                        listener.onMessage(record.key(), record.value());
                    }
                }
            }
        }

        public void stop() {
            running.set(false);
        }
    }

    @Override
    public void close() throws Exception {
        consumeStop();
        producer.close();
        executorService.shutdown();
    }
}
