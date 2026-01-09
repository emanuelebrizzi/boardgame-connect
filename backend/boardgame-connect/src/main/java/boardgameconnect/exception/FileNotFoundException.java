package boardgameconnect.exception;

public class FileNotFoundException extends ResourceNotFoundException {

	static final long serialVersionUID = 8062799534342087287L;

	public FileNotFoundException(String message) {
		super(message);
	}
}