package com.epam.mentoring.multithreading.architecture.task4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockingObjectPool {

    private final BlockingQueue<Object> queue;


    public BlockingObjectPool(int size) {
        this.queue = new ArrayBlockingQueue<>(size);
    }


    public Object get() {
        Object object = null;
        try {
            object = queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread: {} was interrupted", Thread.currentThread().getName());
        }
        return object;
    }


    public void take(Object object) {
        queue.offer(object);
    }
}

