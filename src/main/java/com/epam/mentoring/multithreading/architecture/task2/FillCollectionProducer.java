package com.epam.mentoring.multithreading.architecture.task2;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FillCollectionProducer implements Callable<Integer> {

    private final Random random = new Random();

    private final List<Integer> numbers;

    private final AtomicInteger iterations;

    @Override
    public Integer call() {
        int hits = 0;
        while (iterations.get() > 0 && !Thread.currentThread().isInterrupted()) {
            iterations.decrementAndGet();
            hits++;
            int number = random.nextInt();
            synchronized (numbers) {
                numbers.add(number);
            }
            log.debug("added number to the collection: {}", number);
        }
        return hits;
    }
}
