package com.epam.mentoring.multithreading.architecture.task3;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    private final String topicName;

    private final Broker broker;

    @Override
    public void run() {
        String consumerId = UUID.randomUUID().toString();
        while (!Thread.currentThread().isInterrupted()) {
            String message = broker.issueMessage(topicName);
            log.info("Consumer id: {}, message: {}", consumerId, message);
        }
    }
}
