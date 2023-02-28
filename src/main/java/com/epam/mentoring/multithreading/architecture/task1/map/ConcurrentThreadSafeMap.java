package com.epam.mentoring.multithreading.architecture.task1.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentThreadSafeMap<K, V> extends HashMap<K, V> {

    private final Lock lock = new ReentrantLock();

    @Override
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            lock.lock();
            return super.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        try {
            lock.lock();
            super.putAll(m);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            lock.lock();
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            lock.lock();
            return super.containsValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        try {
            lock.lock();
            return new CopyOnWriteArraySet<>(super.keySet());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        try {
            lock.lock();
            return new CopyOnWriteArrayList<>(super.values());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        try {
            lock.lock();
            return new CopyOnWriteArraySet<>(super.entrySet());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        try {
            lock.lock();
            return super.getOrDefault(key, defaultValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        try {
            lock.lock();
            return super.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        try {
            lock.lock();
            return super.remove(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        try {
            lock.lock();
            return super.replace(key, oldValue, newValue);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        try {
            lock.lock();
            return super.replace(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        try {
            lock.lock();
            return super.computeIfAbsent(key, mappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try {
            lock.lock();
            return super.computeIfPresent(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        try {
            lock.lock();
            return super.compute(key, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        try {
            lock.lock();
            return super.merge(key, value, remappingFunction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        try {
            lock.lock();
            super.forEach(action);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        try {
            lock.lock();
            super.replaceAll(function);
        } finally {
            lock.unlock();
        }
    }

}
