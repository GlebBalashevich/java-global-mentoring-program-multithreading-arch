package com.epam.mentoring.multithreading.architecture.task3;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import lombok.Getter;

@Getter
public final class Topic {

    private final Queue<String> queue;

    private final Lock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    private final int queueCapacity;

    public Topic(Queue<String> queue, Lock lock, int queueCapacity){
        this.queue = queue;
        this.lock = lock;
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
        this.queueCapacity = queueCapacity;
    }

}
