package com.epam.mentoring.multithreading.architecture.task4;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BlockingObjectPoolTest {

    @Test
    void testTakeGet(){
        BlockingObjectPool blockingObjectPool = new BlockingObjectPool(1);
        String object = "testObject";

        blockingObjectPool.take(object);
        Object actual = blockingObjectPool.get();

        assertThat(actual).isEqualTo(object);
    }

    @Test
    void testTakeGetMoreThanCapacity(){
        BlockingObjectPool blockingObjectPool = new BlockingObjectPool(2);
        String object1 = "testObject1";
        String object2 = "testObject2";
        String object3 = "testObject3";

        blockingObjectPool.take(object1);
        blockingObjectPool.take(object2);
        Object actual1 = blockingObjectPool.get();
        blockingObjectPool.take(object3);
        Object actual2 = blockingObjectPool.get();
        Object actual3 = blockingObjectPool.get();

        assertThat(actual1).isEqualTo(object1);
        assertThat(actual2).isEqualTo(object2);
        assertThat(actual3).isEqualTo(object3);
    }

}
