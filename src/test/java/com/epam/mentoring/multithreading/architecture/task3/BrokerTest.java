package com.epam.mentoring.multithreading.architecture.task3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrokerTest {

    private Broker broker;

    @BeforeEach
    void init() {
        broker = new Broker();
    }

    @Test
    void testPublishMessage() {
        assertThatNoException().isThrownBy(() -> broker.publishMessage("test-topic", "message"));
    }

    @Test
    void testIssueMessage() {
        String message = "testMessage";
        broker.publishMessage("test-topic", message);

        String actual = broker.issueMessage("test-topic");

        assertThat(actual).isEqualTo(message);
    }

    @Test
    void testIssueMessageTopicNotExists() {
        assertThatThrownBy(() -> broker.issueMessage("undefined-topic")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testIssueMessagesFromMultipleTopics() {
        String message1 = "testMessage1";
        String message2 = "testMessage2";
        broker.publishMessage("test-topic-1", message1);
        broker.publishMessage("test-topic-2", message2);

        String actual1 = broker.issueMessage("test-topic-1");
        String actual2 = broker.issueMessage("test-topic-2");

        assertThat(actual1).isEqualTo(message1);
        assertThat(actual2).isEqualTo(message2);
    }

}
