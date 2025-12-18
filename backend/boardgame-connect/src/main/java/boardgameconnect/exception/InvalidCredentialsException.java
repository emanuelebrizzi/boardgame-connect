package boardgameconnect.exception;

public class InvalidCredentialsException extends RuntimeException {
    private static final long serialVersionUID = -7494404692484726790L;

    public InvalidCredentialsException(String message) {
	super(message);
    }
}
