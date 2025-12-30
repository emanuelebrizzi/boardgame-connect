package boardgameconnect.exception;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(String id) {
	super("Reservation with ID " + id + " not found");
    }
}