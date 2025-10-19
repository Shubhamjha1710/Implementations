package Heap;

import Heap.Comparators.MaxHeapComparator;
import Heap.Exception.HeapException;

import java.util.Comparator;
import java.util.Random;

public class HeapTest {
    public static void main(String[] args) throws InterruptedException {
        Heap<Integer> heap = new Heap<>(new MaxHeapComparator()); // Min heap

        Runnable inserter = () -> {
            Random rand = new Random();
            for (int i = 0; i < 100000; i++) {
                int num = rand.nextInt(100000) + 1;
                heap.insert(num);
                System.out.println("Inserted: " + num);
            }
        };

        Runnable remover = () -> {
            for (int i = 0; i < 99990; i++) {
                try {
                    System.out.println("Extracted: " + heap.extract());
                } catch (HeapException e) {
                    // Ignore empty heap errors
                }
            }
        };

        long start = System.nanoTime();

        Thread t1 = new Thread(inserter);
        Thread t2 = new Thread(inserter);
        t1.start();
        t2.start();
        t1.join();
        t2.join();


        Thread t3 = new Thread(remover);
        Thread t4 = new Thread(remover);
        t3.start();
        t4.start();
        t3.join();
        t4.join();

        long end = System.nanoTime();
        System.out.println("Final heap");
        heap.printTree();
        System.out.println("Final heap size: " + heap.size());
        System.out.println("Function Execution time: " + (end - start)/1_000_000 + " ms");
    }

}
