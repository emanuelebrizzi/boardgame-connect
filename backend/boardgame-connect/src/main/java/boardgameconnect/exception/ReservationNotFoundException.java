package boardgameconnect.exception;

public class ReservationNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 2044977058993106229L;

	public ReservationNotFoundException(String id) {
		super("Reservation with ID " + id + " not found");
	}
}