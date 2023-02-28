package com.epam.mentoring.multithreading.architecture.task1;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MapService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void executeMapProcessing(Map<Integer, Integer> numbers, int iterations) throws Throwable {
        final FillMapProducer fillMapProducer = new FillMapProducer(numbers, iterations);
        final SumMapProcessor sumMapProcessor = new SumMapProcessor(numbers, iterations);
        final Future<Integer> fillMapResult = executorService.submit(fillMapProducer);
        final Future<Integer> sumMapResult = executorService.submit(sumMapProcessor);

        try {
            Integer insertionIterations = fillMapResult.get();
            Integer sumIterations = sumMapResult.get();
            log.debug("insertion iterations: {}, sum iterations: {}", insertionIterations, sumIterations);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e.getCause();
        }
    }

}
