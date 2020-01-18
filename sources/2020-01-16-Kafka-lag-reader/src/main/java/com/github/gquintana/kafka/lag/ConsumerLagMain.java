package com.github.gquintana.kafka.lag;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class ConsumerLagMain implements AutoCloseable {
    private final String bootstrapServers;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerLagMain.class);
    private AdminClient adminClient;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(ConsumerLagMain main = new ConsumerLagMain("localhost:9092")) {
            main.run();
        }
    }

    public ConsumerLagMain(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }


    public void run() throws ExecutionException, InterruptedException {
        initAdminClient();
        List<String> groupIds = getConsumerGroupIds();
        for (String groupId : groupIds) {
            StringBuilder stringBuilder = new StringBuilder().append(groupId).append(":\n");
            // tag::topicPartitionJoin[]
            Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets = getConsumerGroupOffsets(groupId);
            Map<TopicPartition, Long> topicEndOffsets = getTopicEndOffsets(groupId, consumerGroupOffsets.keySet());
            Map<Object, Object> consumerGroupLag = consumerGroupOffsets.entrySet().stream()
                    .map(entry -> mapEntry(entry.getKey(), new OffsetAndLag(topicEndOffsets.get(entry.getKey()), entry.getValue().offset())))
                    // end::topicPartitionJoin[]
                    .peek(entry -> stringBuilder.append(formatLag(entry.getKey(), entry.getValue())).append('\n'))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            LOGGER.info(stringBuilder.toString());
        }

    }


    private void initAdminClient() {
        Map<String, Object> adminClientConfig = new HashMap<>();
        adminClientConfig.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        adminClient = AdminClient.create(adminClientConfig);
    }

    private List<String> getConsumerGroupIds() throws ExecutionException, InterruptedException {
        // tag::consumerGroupIds[]
        return adminClient
                .listConsumerGroups()
                .valid()
                .thenApply(r -> r.stream()
                        .map(ConsumerGroupListing::groupId)
                        .collect(toList())
                ).get();
        // end::consumerGroupIds[]
    }

    private Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String groupId) throws InterruptedException, ExecutionException {
        // tag::consumerGroupOffsets[]
        return adminClient
                .listConsumerGroupOffsets(groupId)
                .partitionsToOffsetAndMetadata().get();
        // end::consumerGroupOffsets[]
    }

    private Map<TopicPartition, Long> getTopicEndOffsets(String groupId, Collection<TopicPartition> partitions) {
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(GROUP_ID_CONFIG, groupId);
        consumerConfig.put(KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        consumerConfig.put(VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        try (Consumer<byte[], byte[]> consumer = new KafkaConsumer<byte[], byte[]>(consumerConfig)) {
            // tag::topicEndOffsets[]
            return consumer.endOffsets(partitions);
            // end::topicEndOffsets[]
        }
    }

    private static class OffsetAndLag {
        private final long endOffset;
        private final long currentOffset;

        public OffsetAndLag(long endOffset, long currentOffset) {
            this.endOffset = endOffset;
            this.currentOffset = currentOffset;
        }

        public long computeLag() {
            // tag::computeLag[]
            long lag = endOffset - currentOffset;
            if (lag < 0) {
                lag = 0;
            }
            return lag;
            // end::computeLag[]
        }
    }

    private static <K, V> Map.Entry<K, V > mapEntry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }


    private String formatLag(TopicPartition topicPartition, OffsetAndLag offsetAndLag) {
        return String.format(String.format("%s %2d %8d %8d %8d", topicPartition.topic(), topicPartition.partition(), offsetAndLag.currentOffset, offsetAndLag.endOffset, offsetAndLag.computeLag()));
    }

    @Override
    public void close() {
        if (adminClient != null) {
            adminClient.close();
        }
    }
}
