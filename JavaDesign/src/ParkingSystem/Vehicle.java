package ParkingSystem;

import ParkingSystem.Exceptions.ParkingSpaceFullException;
import ParkingSystem.Exceptions.SlotUnavailableException;

public class Vehicle {
    public final String name;
    private ParkingStatus status; // can be parked, prebooked, unparked
    private int slotNo;
    private String slotId;


    Vehicle(String name) {
        this.name = name;
        this.status = ParkingStatus.CREATED;
        this.slotNo = -1;
        this.slotId = "";
    }

    String getSlotId() {
        return this.slotId;
    }

    int getSlotNo() {
        return this.slotNo;
    }

    void departVehicle(ParkingSpace space) {
        this.status = ParkingStatus.LEFT;
        space.freeSlot(this.slotNo);
        System.out.println("Vehicle " + this.name + " gets departed from slot " + slotId);
        this.slotNo = -1;
        this.slotId = "";
    }

    boolean parkVehicle(ParkingSpace space) {
        try {
            int assignedSpot = space.getParkingSlot(this);
            if(assignedSpot != -1) {
                this.slotNo = assignedSpot;
                this.slotId = space.getSlotId(this.slotNo);
                this.status = ParkingStatus.PARKED;
                System.out.println("Vehicle " + this.name + " gets parked in slot " + slotId);
                return true;
            } else {
                return false;
            }
        } catch(ParkingSpaceFullException ex) {
//            System.out.println(this.name + " " + ex.getMessage());
            return false;
        }
    }

    boolean bookSlot(ParkingSpace space, int slotNo) {
        try {
            if(space.bookSlot(slotNo)) {
                this.slotNo = slotNo;
                this.slotId = space.getSlotId(this.slotNo);
                this.status = ParkingStatus.PARKED;
                System.out.println("Vehicle " + this.name + " gets parked in slot " + slotId);
                return true;
            }
            return false;
        } catch (SlotUnavailableException ex) {
//            System.out.println(this.name + " " + ex.getMessage());
            return false;
        }
    }
}
