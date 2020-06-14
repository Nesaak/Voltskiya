package com.voltskiya.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This is meant to be very efficient on memory with no regard for anything else
 *
 * @param <K>
 * @param <V>
 */
public class CheapMap<K, V> implements Map<K, V> {
    private Pair<K, V>[] contents = null;

    @Override
    public int size() {
        return contents.length;
    }

    @Override
    public boolean isEmpty() {
        return contents.length == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (contents == null || key == null)
            return false;
        for (Pair<K, V> content : contents) {
            if (content.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (contents == null || value == null)
            return false;
        for (Pair<K, V> content : contents) {
            if (content.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (contents != null)
            for (Pair<K, V> content : contents) {
                if (content.getKey().equals(key)) {
                    return content.getValue();
                }
            }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings({"unchecked"})
    public V put(K key, V value) {
        if (key == null)
            return null;

        Pair<K, V> newPair = new Pair<>(key, value);
        if (contents == null) {
            contents = (Pair<K, V>[]) new Pair[1];
            contents[0] = newPair;
        } else {// check if the key already exists
            for (Pair<K, V> entry : contents) {
                if (entry.getKey().equals(key)) {
                    entry.setValue(value);
                    return value;
                }
            }
            final int length = contents.length;
            Pair<K, V>[] newContents = (Pair<K, V>[]) new Pair[length + 1];
            System.arraycopy(contents, 0, newContents, 0, length);
            newContents[length] = newPair;
            contents = newContents;
        }
        return value;
    }

    @Nullable
    @Override
    @SuppressWarnings({"unchecked"})
    public V remove(Object key) {
        if (contents == null) {
            return null;
        } else {
            int indexToRemove = -1;
            final int length = contents.length;
            for (int i = 0; i < length; i++) {
                if (contents[i].getKey().equals(key)) {
                    indexToRemove = i;
                    break;
                }
            }
            Pair<K, V>[] newContents = (Pair<K, V>[]) new Pair[length - 1];
            for (int newI = 0, oldI = 0; oldI < length; oldI++, newI++) {
                if (oldI == indexToRemove) {
                    newI--;
                } else {
                    newContents[newI] = contents[oldI];
                }
            }
            contents = newContents;
        }
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void putAll(@NotNull Map<? extends K, ? extends V> other) {
        final int oldLength = contents.length;
        Pair<K, V>[] newContents = (Pair<K, V>[]) new Pair[oldLength + other.size()];
        // deal with first map
        System.arraycopy(contents, 0, newContents, 0, oldLength);
        // deal with other map
        int currentIndex = oldLength;
        for (Entry<? extends K, ? extends V> entry : other.entrySet()) {
            newContents[currentIndex++] = new Pair<>(entry.getKey(), entry.getValue());
        }
        contents = newContents;
    }

    @Override
    public void clear() {
        contents = null;
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Pair<K, V> entry : contents) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        Set<V> values = new HashSet<>();
        for (Pair<K, V> entry : contents) {
            values.add(entry.getValue());
        }
        return values;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return contents == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(contents));
    }
}
