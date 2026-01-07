package boardgameconnect.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4381879559589516272L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}