package com.epam.mentoring.multithreading.architecture.task2;

import java.time.Instant;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CollectionServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionServiceTest.class);

    private CollectionService collectionService;

    @BeforeEach
    void init() {
        collectionService = new CollectionService();
    }

    @ParameterizedTest
    @ValueSource(ints = { 100, 1000, 10000})
    void testCollectionProcessing_NoDeadLock(int iterations) {
        final Instant startProcessingTime = Instant.now();
        Assertions.assertDoesNotThrow(
                () -> collectionService.executeCollectionProcessing(new ArrayList<>(), iterations));
        final Instant endProcessingTime = Instant.now();

        LOGGER.info("number of iterations: {}, processing time: {}ms", iterations,
                endProcessingTime.toEpochMilli() - startProcessingTime.toEpochMilli());
    }

}
