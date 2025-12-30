package boardgameconnect.exception;

public class BoardgameNotFoundException extends ResourceNotFoundException {
    public BoardgameNotFoundException(String message) {
	super(message);
    }
}