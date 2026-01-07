package boardgameconnect.exception;

public class PlayerNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = -4462121653906623210L;

	public PlayerNotFoundException(String message) {
		super(message);
	}
}