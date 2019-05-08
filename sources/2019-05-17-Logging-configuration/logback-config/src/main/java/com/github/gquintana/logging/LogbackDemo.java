package com.github.gquintana.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackDemo {
    private final Logger logger = LoggerFactory.getLogger(LogbackDemo.class);

    public void logSomething() {
        logger.info("Something");
    }
}
