package com.epam.mentoring.multithreading.architecture.task3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Broker {

    private final Map<String, Topic> topics = new HashMap<>();

    public void publishMessage(String topicName, String message) {
        final Topic topic = topics.getOrDefault(topicName, addTopic(topicName, 15));
        final Queue<String> queue = topic.getQueue();
        final Lock lock = topic.getLock();
        final Condition notEmpty = topic.getNotEmpty();
        final Condition notFull = topic.getNotFull();
        final int queueCapacity = topic.getQueueCapacity();
        lock.lock();
        try {
            while (queue.size() >= queueCapacity) {
                notFull.await();
            }
            queue.add(message);
            notEmpty.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Producer thread was interrupted: {}", Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }

    public String issueMessage(String topicName) {
        final Topic topic = findTopic(topicName);
        final Queue<String> queue = topic.getQueue();
        final Lock lock = topic.getLock();
        final Condition notEmpty = topic.getNotEmpty();
        final Condition notFull = topic.getNotFull();
        String message = null;
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            message = queue.poll();
            notFull.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Consumer thread was interrupted: {}", Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
        return message;
    }

    public Topic addTopic(String topicName, int queueCapacity) {
        final Topic topic = new Topic(new LinkedList<>(), new ReentrantLock(), queueCapacity);
        return topics.putIfAbsent(topicName, topic);
    }

    private Topic findTopic(String topicName) {
        return Optional.ofNullable(topics.get(topicName))
                .orElseThrow(() -> {
                    log.error("Topic with name: {} not found", topicName);
                    return new RuntimeException(String.format("Topic with name %s not found", topicName));
                });
    }

}
