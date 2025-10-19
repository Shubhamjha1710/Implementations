package ParkingSystem.Exceptions;

public class ParkingSpaceFullException extends RuntimeException {
    public ParkingSpaceFullException(String message) {
        super(message);
    }
}
