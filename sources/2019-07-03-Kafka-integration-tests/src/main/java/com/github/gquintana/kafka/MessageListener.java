package com.github.gquintana.kafka;

public interface MessageListener {
    void onMessage(Integer key, String value);
}
