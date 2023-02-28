package com.epam.mentoring.multithreading.architecture.task1;

import java.time.Instant;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.epam.mentoring.multithreading.architecture.task1.map.ConcurrentThreadSafeMap;
import com.epam.mentoring.multithreading.architecture.task1.map.SynchronizedThreadSafeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MapServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapServiceTest.class);

    private MapService mapService;

    @BeforeEach
    void init() {
        mapService = new MapService();
    }

    @Test
    void testHashMapProcessing_ConcurrentModificationException() {
        final Map<Integer, Integer> map = new HashMap<>();
        final int iterations = 1000;

        Assertions.assertThrows(ConcurrentModificationException.class,
                () -> mapService.executeMapProcessing(map, iterations));
    }

    @ParameterizedTest
    @MethodSource("provideMapsAndIterations")
    void testMapProcessing_NoException(Map<Integer, Integer> map, int iterations) {
        final Instant startProcessingTime = Instant.now();
        Assertions.assertDoesNotThrow(() -> mapService.executeMapProcessing(map, iterations));
        final Instant endProcessingTime = Instant.now();

        LOGGER.info("map class: {}, number of iterations: {}, processing time: {}ms", map.getClass().getSimpleName(),
                iterations, endProcessingTime.toEpochMilli() - startProcessingTime.toEpochMilli());
    }

    private static Stream<Arguments> provideMapsAndIterations() {
        return Stream.of(
                Arguments.of(new ConcurrentHashMap<>(), 1000),
                Arguments.of(new ConcurrentHashMap<>(), 10000),
                Arguments.of(new ConcurrentHashMap<>(), 100000),

                Arguments.of(Collections.synchronizedMap(new HashMap<>()), 1000),
                Arguments.of(Collections.synchronizedMap(new HashMap<>()), 10000),
                Arguments.of(Collections.synchronizedMap(new HashMap<>()), 100000),

                Arguments.of(new SynchronizedThreadSafeMap<>(), 1000),
                Arguments.of(new SynchronizedThreadSafeMap<>(), 10000),
                Arguments.of(new SynchronizedThreadSafeMap<>(), 100000),

                Arguments.of(new ConcurrentThreadSafeMap<>(), 1000),
                Arguments.of(new ConcurrentThreadSafeMap<>(), 10000),
                Arguments.of(new ConcurrentThreadSafeMap<>(), 100000));
    }

}
