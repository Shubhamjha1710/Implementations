package ParkingSystem;

import ParkingSystem.Exceptions.ParkingSpaceFullException;
import ParkingSystem.Exceptions.SlotUnavailableException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Thread safety option
// 1. Making complete method synchronized
// 2. Using individual locks for each slot
// 3. Using separate locks for each row
// 4. Zone bas locking (separate locks for each zone)
public class ParkingSpace {
    int rows;
    int cols;
    boolean[][] slots;
    AtomicInteger totalAvailableSpots;
    final int maxSlotSize;
    private ReentrantReadWriteLock[]locks;

    ParkingSpace(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maxSlotSize = rows * cols;
        this.slots = new boolean[rows][cols];
        this.totalAvailableSpots = new AtomicInteger(rows * cols);
        this.locks = new ReentrantReadWriteLock[rows];

        for(int i = 0;i<rows;i++) {
            this.locks[i] = new ReentrantReadWriteLock();
            for(int j = 0;j<cols;j++) {
                slots[i][j] = true;
            }
        }
    }

    String getSlotId(int slotNo) {
        int row_no = (slotNo/this.cols);
        int col_no = (slotNo%this.cols);
        return "R" + row_no + ":C" + col_no;
    }

    public int getParkingSlot(Vehicle vehicle) throws ParkingSpaceFullException{

        if(this.totalAvailableSpots.get() <= 0) {
            throw new ParkingSpaceFullException("Parking space is completely full...");
        } else {
            for(int i = 0;i<rows;i++) {
                locks[i].writeLock().lock();
                try {
                    for(int j = 0;j<cols;j++) {
                        if(slots[i][j]) {
                            slots[i][j] = false;
                            totalAvailableSpots.decrementAndGet();
                            return ((i*this.cols) + j);
                        }
                    }
                } finally {
                    locks[i].writeLock().unlock();
                }
            }
        }
        return -1;
    }

    public boolean bookSlot(int queriedSlot) throws SlotUnavailableException {
        int row_no = (queriedSlot/this.cols);
        int col_no = (queriedSlot%this.cols);

        locks[row_no].writeLock().lock();
        try {
            if(slots[row_no][col_no]) {
                totalAvailableSpots.decrementAndGet();
                slots[row_no][col_no] = false;
                return true;
            } else {
                throw new SlotUnavailableException("Queries slot is not available right now...");
            }
        } finally {
            locks[row_no].writeLock().unlock();
        }

    }

    public void freeSlot(int slotNo) {
        int row_no = (slotNo/this.cols);
        int col_no = (slotNo%this.cols);
        locks[row_no].writeLock().lock();
        try {
            slots[row_no][col_no] = true;
            totalAvailableSpots.incrementAndGet();
        } finally {
            locks[row_no].writeLock().unlock();
        }
    }
}
