package com.github.gquintana.logging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Log4J1DemoTest {

    @BeforeAll
    static void setUp() {
        System.setProperty("log.dir", "target/log");
    }
    @Test
    void logSomething() {
        new Log4J1Demo().logSomething();
    }

    @Test
    void logFlood() {
        new Log4J1Demo().logFlood(1_000_000);
    }

}