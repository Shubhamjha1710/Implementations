package ParkingSystem;


import java.util.*;

// separate thread which will manage the parking and departing

public class TestParking {

    static class ParkingSimulatorThread extends Thread {
        String threadName;
        Vehicle vehicle;
        ParkingSpace parkingSpace;
        ParkingSimulatorThread(String name, Vehicle vehicle, ParkingSpace space) {
            this.threadName = name;
            this.vehicle = vehicle;
            this.parkingSpace = space;
        }

        private void customSleep(long millis) {
            try {
                sleep(millis);
            } catch (InterruptedException _) {
            }
        }

        @Override
        public void run() {
            Random rand = new Random();

            if (vehicle.parkVehicle(parkingSpace)) {

                customSleep(1000);
                vehicle.departVehicle(parkingSpace);
            }

            customSleep(500);

            int num = rand.nextInt(parkingSpace.maxSlotSize-1);
            if (vehicle.bookSlot(parkingSpace, num)) {

                customSleep(1000);
                vehicle.departVehicle(parkingSpace);
            }

            customSleep(500);

            if (vehicle.parkVehicle(parkingSpace)) {

                customSleep(1000);
                vehicle.departVehicle(parkingSpace);
            }
        }
    }

    public static void main(String []args) {
        int threadCnt = 2000;
        ParkingSpace station1 = new ParkingSpace(40, 25);

        List<Vehicle> vehicleList = new ArrayList<Vehicle>();
        List<ParkingSimulatorThread> threadPool = new ArrayList<ParkingSimulatorThread>();
        for(int i = 0;i<threadCnt;i++) {
            String threadName = "Thread " + i;
            Vehicle v_i = new Vehicle("V:" + (i+1));
            vehicleList.add(v_i);
            threadPool.add(new ParkingSimulatorThread(threadName,v_i, station1));
        }

        for(int i = 0;i<threadCnt;i++) {
            threadPool.get(i).start();
        }

        for(int i = 0;i<threadCnt;i++) {
            try {
                threadPool.get(i).join();

            } catch (InterruptedException ex) {
                System.out.println("Exception in joining thread: " + threadPool.get(i).threadName);
            }
        }


        Map<String ,List<String>> mp = new HashMap<>();
        for(int i = 0;i<threadCnt;i++) {
            String slotId = vehicleList.get(i).getSlotId();
            int slotNo = vehicleList.get(i).getSlotNo();
            if (slotNo != -1) {
                if(!mp.containsKey(slotId)) {
                    mp.put(slotId, new ArrayList<>());
                }
                mp.get(slotId).add(vehicleList.get(i).name);
            }
        }
        for (Map.Entry<String, List<String>> entry : mp.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }
        System.out.println("Concurrency testing done...");
    }

}
