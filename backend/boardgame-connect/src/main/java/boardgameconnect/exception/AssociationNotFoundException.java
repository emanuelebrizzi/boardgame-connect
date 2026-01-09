package boardgameconnect.exception;

public class AssociationNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 2548923231562436161L;

	public AssociationNotFoundException(String message) {
		super(message);
	}
}