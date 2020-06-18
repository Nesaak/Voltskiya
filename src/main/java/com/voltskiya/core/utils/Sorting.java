package com.voltskiya.core.utils;

public class Sorting {
    // good for already sorted arrays
    public static void insertionSort(short[] array) {
        int size = array.length;
        for (int i = 1; i < size; i++) {
            short current = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > current) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = current;
        }
    }
}
