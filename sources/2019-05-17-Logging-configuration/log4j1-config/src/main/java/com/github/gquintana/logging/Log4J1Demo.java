package com.github.gquintana.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4J1Demo {
    private final Logger logger = LoggerFactory.getLogger(Log4J1Demo.class);

    public void logSomething() {
        logger.info("Something");
    }

    public void logFlood(int iterations) {
        String bigString = createString(1000);
        for (int i = 0; i < iterations; i++) {
            logger.info("Flood {} {}", i, bigString);
        }
    }

    private String createString(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append((char) (' ' + (i % 90)));
        }
        return sb.toString();
    }
}
