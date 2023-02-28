package com.epam.mentoring.multithreading.architecture.task1;

import java.util.Map;
import java.util.concurrent.Callable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FillMapProducer implements Callable<Integer> {

    private final Map<Integer, Integer> numbers;

    private final int iterations;

    @Override
    public Integer call() {
        int i = 0;
        while (i < iterations) {
            i++;
            log.debug("inserting number");
            numbers.putIfAbsent(i, 1);
        }
        return i;
    }

}
