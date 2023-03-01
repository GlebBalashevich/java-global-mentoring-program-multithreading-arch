package com.epam.mentoring.multithreading.architecture.task2;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SumCalculator implements Callable<Integer> {

    private final List<Integer> numbers;

    private final AtomicInteger iterations;

    @Override
    public Integer call() {
        int hits = 0;
        while (iterations.get() > 0 && !Thread.currentThread().isInterrupted()) {
            hits++;
            iterations.decrementAndGet();
            int sum = 0;
            synchronized (numbers) {
                for (Integer number : numbers) {
                    sum += number;
                }
            }
            System.out.printf("sum of collection's numbers: %d", sum);
            log.debug("Sum calculation, total hits: {}", hits);
        }
        return hits;
    }
}
