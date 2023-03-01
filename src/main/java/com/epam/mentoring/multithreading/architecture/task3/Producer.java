package com.epam.mentoring.multithreading.architecture.task3;

import java.util.Random;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final Random random = new Random();

    private final String topicName;

    private final Broker broker;

    @Override
    public void run() {
        String producerId = UUID.randomUUID().toString();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = UUID.randomUUID().toString();
                broker.publishMessage(topicName, message);
                log.info("Producer id: {}, message: {}", producerId, message);
                Thread.sleep(random.nextInt(250) + 50L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("thread was interrupted: {}", Thread.currentThread().getName());
            }
        }
    }
}
