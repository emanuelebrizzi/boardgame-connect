package boardgameconnect.exception;

public class ReservationFullException extends RuntimeException {

	private static final long serialVersionUID = 5775010070860499771L;

	public ReservationFullException(String message) {
		super(message);
	}

}