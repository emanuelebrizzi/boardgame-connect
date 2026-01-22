package boardgameconnect.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import boardgameconnect.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Error 400
	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
			PlayerAlreadyJoinedException.class })
	public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {

		log.warn("Bad Request at path {}: {}", request.getRequestURI(), ex.getMessage());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	// Error 401: Invalid login credentials
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
			HttpServletRequest request) {
		log.warn("Login failed: {} at path {}", ex.getMessage(), request.getRequestURI());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
				HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	// Error 403
	@ExceptionHandler({ ForbiddenActionException.class, AccessDeniedException.class })
	public ResponseEntity<ErrorResponse> handleForbidden(Exception ex, HttpServletRequest request) {
		log.warn("Forbidden action at path {}: {}", request.getRequestURI(), ex.getMessage());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
				HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	// Error 404: Requested resource not found
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		log.warn("Resource not found at path {}: {}", request.getRequestURI(), ex.getMessage());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	// Error 409
	@ExceptionHandler({ EmailAlreadyInUseException.class, BoardgameInUseException.class,
			GameTableInUseException.class })
	public ResponseEntity<ErrorResponse> handleConflict(Exception ex, HttpServletRequest request) {
		log.warn("Conflict at path {}: {}", request.getRequestURI(), ex.getMessage());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	// Error 500: Unexpected internal server error
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error occurred at path {}: ", request.getRequestURI(), ex);

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "An internal error occurred",
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	// Error 500: Data import specific error
	@ExceptionHandler(DataImportException.class)
	public ResponseEntity<ErrorResponse> handleDataImport(DataImportException ex, HttpServletRequest request) {
		log.error("Data import failed at path {}: {}", request.getRequestURI(), ex.getMessage());

		var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
