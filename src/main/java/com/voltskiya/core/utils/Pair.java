package com.voltskiya.core.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {
    private static final int MAX_INT_VAL = Integer.MAX_VALUE;
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public K setKey(K key) {
        return this.key = key;
    }

    @Override
    public V setValue(V value) {
        return this.value = value;
    }

    @Override
    public String toString() {
        return '<' +
                key.toString() +
                ',' +
                value.toString() +
                '>';
    }

    @Override
    public int hashCode() {
        return 31 * (17 + key.hashCode() + value.hashCode()) % MAX_INT_VAL;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair pair = (Pair) other;
            return key.equals(pair.key) && value.equals(pair.value);
        }
        return false;
    }
}