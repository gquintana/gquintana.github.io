package com.github.gquintana.logging;

import org.junit.jupiter.api.Test;

class Log4J2DemoTest {

    @Test
    void logSomething() {
        new Log4J2Demo().logSomething();
    }

    @Test
    void logFlood() {
        new Log4J2Demo().logFlood(1_000_000);
    }

}