package Heap.Comparators;

import java.util.Comparator;
public class MinHeapComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer a, Integer b) {
        if (a > b) {
            return -1;
        } else {
            return 1;
        }
    }
}