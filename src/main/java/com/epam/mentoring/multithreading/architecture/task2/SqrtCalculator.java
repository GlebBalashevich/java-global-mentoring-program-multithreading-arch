package com.epam.mentoring.multithreading.architecture.task2;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SqrtCalculator implements Callable<Integer> {

    private final List<Integer> numbers;

    private final AtomicInteger iterations;

    @Override
    public Integer call() {
        int hits = 0;
        while (iterations.get() > 0 && !Thread.currentThread().isInterrupted()) {
            hits++;
            iterations.decrementAndGet();
            double squaresSum = 0;
            synchronized (numbers) {
                for (Integer number : numbers) {
                    squaresSum += Math.pow(number, 2);
                }
            }
            System.out.printf("square root of squares of collection's numbers is %f", Math.sqrt(squaresSum));
            log.debug("Sqrt calculation, total hits: {}", hits);
        }
        return hits;
    }
}
