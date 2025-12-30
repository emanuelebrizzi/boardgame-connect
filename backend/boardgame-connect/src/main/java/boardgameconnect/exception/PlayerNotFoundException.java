package boardgameconnect.exception;

public class PlayerNotFoundException extends ResourceNotFoundException {
    public PlayerNotFoundException(String message) {
	super(message);
    }
}