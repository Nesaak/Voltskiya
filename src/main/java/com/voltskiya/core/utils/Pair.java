package com.voltskiya.core.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {
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

}