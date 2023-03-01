package com.epam.mentoring.multithreading.architecture.task2;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void executeCollectionProcessing(List<Integer> numbers, int iterations) throws Throwable {
        final AtomicInteger atomicIterations = new AtomicInteger(iterations);
        final FillCollectionProducer fillCollectionProducer = new FillCollectionProducer(numbers, atomicIterations);
        final SumCalculator sumCalculator = new SumCalculator(numbers, atomicIterations);
        final SqrtCalculator sqrtCalculator = new SqrtCalculator(numbers, atomicIterations);

        final Future<Integer> fillCollectionResult = executorService.submit(fillCollectionProducer);
        final Future<Integer> calculateSumResult = executorService.submit(sumCalculator);
        final Future<Integer> calculateSqrtResult = executorService.submit(sqrtCalculator);

        try {
            Integer insertionIterations = fillCollectionResult.get();
            Integer sumIterations = calculateSumResult.get();
            Integer sqrtIterations = calculateSqrtResult.get();
            log.info("insertion iterations: {}, sum iterations: {}, sqrt iterations: {}", insertionIterations,
                    sumIterations, sqrtIterations);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e.getCause();
        }
    }

}
