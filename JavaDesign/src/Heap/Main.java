package Heap;

import Heap.Comparators.MaxHeapComparator;

class Main{


    public static void main(String []args) {

        Heap<Integer> maxHeap = new Heap<>(new MaxHeapComparator());

        for (int i = 0;i<10;i++) {
            maxHeap.insert(i);
        }
        for (int i = 10;i>=0;i--) {
            maxHeap.insert(i);
        }

        while (maxHeap.isEmpty() == false) {
            System.out.println(maxHeap.top());
            maxHeap.pop();
        }
    }
}