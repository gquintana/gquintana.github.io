package com.github.gquintana.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogbackDemoTest {

    @Test
    void logSomething() {
        new LogbackDemo().logSomething();
    }
}