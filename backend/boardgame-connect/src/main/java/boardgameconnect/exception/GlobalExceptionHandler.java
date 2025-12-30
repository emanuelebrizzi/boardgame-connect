package boardgameconnect.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import boardgameconnect.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle 400: Invalid Arguments
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
	    HttpServletRequest request) {

	log.warn("Validation failed at path {}: {}", request.getRequestURI(), ex.getBindingResult());

	var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
		HttpStatus.BAD_REQUEST.getReasonPhrase(), "Invalid data", request.getRequestURI());

	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle 401: Invalid Credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
	    HttpServletRequest request) {
	log.warn("Login failed: {} at path {}", ex.getMessage(), request.getRequestURI());

	var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
		HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Handle 409: Email already in use
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(EmailAlreadyInUseException ex,
	    HttpServletRequest request) {
	log.warn("registration failed: {} at path {}", ex.getMessage(), request.getRequestURI());

	var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
		HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

	return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Handle 500: Generic Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
	log.error("Unexpected error occurred at path {}: ", request.getRequestURI(), ex);

	var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
		HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "An internal error occurred",
		request.getRequestURI());

	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Handle 404: Reservation Not Found
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ReservationNotFoundException ex, HttpServletRequest request) {
	log.warn("Resource not found: {} at path {}", ex.getMessage(), request.getRequestURI());

	var errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
		HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
