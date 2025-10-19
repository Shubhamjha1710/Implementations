package Heap;

import Heap.Exception.HeapException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// When we represent the heap in form of array remeber following points
// 1. Heap.Main.Heap.Heap.Main.Heap array should always remain complete binary tree because
// 1.1. Efficient storage and retrieval as complete binary tree doesn't has gap and
// indexing on complete binary tree will work more effectively.
// 1.2. Guarantee of Logarithmic Height so insert and delete operation remains logn
// 2. if i-th index is parent then (2*i + 1, 2*i + 2) should be child


// Assuming standard comparator behaviour i.e.
// If comparison returns > 0 , then swap, otherwise don't
public class Heap<T> {

    private ArrayList<T> heap = new ArrayList<>();;
    private final Comparator<? super T> comparator;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    Heap() {
        comparator = null;
    }

    Heap(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    Heap(ArrayList<T> vt, Comparator<? super T> comparator) {
        this.comparator = comparator;
        for(T element: vt) {
            this.insert(element);
        }
    }

    // comparison logic using comparator
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        // If no comparator → use natural ordering
        if (a instanceof Comparable) {
            return ((Comparable<? super T>) a).compareTo(b);
        } else {
            throw new HeapException("No comparator provided and elements are not Comparable");
        }
    }

    // Generic function
    // 1. Heapify: Convert the substree starting from i to heap
    public void heapify(int idx) {
        int leftIdx = 2*idx+1;
        int rightIdx = 2*idx+2;

        int swapWith = idx;
        if (leftIdx < heap.size() && (compare(heap.get(swapWith), heap.get(leftIdx)) > 0)) {
            swapWith = leftIdx;
        }

        if (rightIdx < heap.size() && (compare(heap.get(swapWith), heap.get(rightIdx))) > 0) {
            swapWith = rightIdx;
        }

        if (swapWith != idx) {
            Collections.swap(this.heap, idx, swapWith);
            heapify(swapWith);
        } else {
            return;
        }
    }

    // 2. Insert: Insert the new element in heap and rebalance the heap structure
    public void insert(T num) {
        rwLock.writeLock().lock();

        try {
            this.heap.add(num);

            int idx = heap.size() - 1;
            while (idx > 0 && compare(heap.get((idx - 1) / 2), heap.get(idx)) > 0) {
                Collections.swap(heap, ((idx - 1) / 2), idx);
                idx = ((idx - 1) / 2);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 3. Pop: Remove the top element of heap and afterward rebalances the heap
    public void pop() {

        rwLock.writeLock().lock();
        try {
            if (heap.isEmpty()) {
                throw new HeapException("No element present inside heap");
            }

            int last = heap.size() - 1;
            Collections.swap(this.heap, 0, last);
            heap.remove(last);

            heapify(0);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 4. Top: Returns the top element of heap
    public T top() {
        rwLock.readLock().lock();
        try {
            if (heap.isEmpty()) {
                throw new HeapException("No element present inside heap");
            }
            return heap.getFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // 5. extract: return and removes the top element of heap
    public T extract() {
        rwLock.writeLock().lock();
        try {
            if (heap.isEmpty()) {
                throw new HeapException("No element present inside heap");
            }

            T topElement = heap.getFirst();
            int last = heap.size() - 1;
            Collections.swap(this.heap, 0, last);
            heap.remove(last);
            heapify(0);
            return topElement;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // 6. isEmpty: Specify weather heap is empty or not
    public boolean isEmpty() {
        rwLock.readLock().lock();

        try {
            if (heap.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // 7. Size: returns size of heap
    public int size() {
        rwLock.readLock().lock();
        try {
            return heap.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void printTree() {
        if (heap == null || heap.isEmpty()) {
            System.out.println("Empty tree");
            return;
        }

        int n = heap.size();
        int height = (int) Math.ceil(Math.log(n + 1) / Math.log(2));

        int maxLevel = height;
        int maxWidth = (int) Math.pow(2, maxLevel) * 2; // spacing scale factor
        int index = 0;

        for (int level = 0; level < maxLevel && index < n; level++) {
            int levelCount = (int) Math.pow(2, level);
            int spaceBetween = maxWidth / levelCount;
            int firstSpace = spaceBetween / 2;

            // ---- Print nodes ----
            for(int sp = 0;sp<firstSpace;sp++) {
                System.out.print(" ");
            }

            List<Integer> thisLevel = new ArrayList<>();
            for (int i = 0; i < levelCount && index < n; i++) {
                if (heap.get(index) == null)
                    System.out.print(" ");
                else
                    System.out.print(heap.get(index));
                thisLevel.add(index);
                index++;

                for(int sp = 0;sp<spaceBetween - 1;sp++) {
                    System.out.print(" ");
                }
            }
            System.out.println();

            // ---- Print connecting edges ----
            if (level < maxLevel - 1) {
                int edgeLines = spaceBetween / 4;
                if (edgeLines < 1) edgeLines = 1;

                for (int e = 1; e <= edgeLines; e++) {

                    for(int sp = 0;sp<firstSpace - e;sp++) {
                        System.out.print(" ");
                    }
                    for (int i = 0; i < thisLevel.size(); i++) {
                        int parent = thisLevel.get(i);
                        int left = 2 * parent + 1;
                        int right = 2 * parent + 2;

                        if (left < n && heap.get(left) != null)
                            System.out.print("/");
                        else
                            System.out.print(" ");


                        for(int sp = 0;sp< 2*e;sp++) {
                            System.out.print(" ");
                        }
                        if (right < n && heap.get(right) != null)
                            System.out.print("\\");
                        else
                            System.out.print(" ");

                        // spacing between separate parents’ branches
                        for(int sp = 0;sp< spaceBetween - e * 2 - 1;sp++) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }
}
