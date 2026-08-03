package dev.serm.novabase_challenge.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
		String detail = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
				.reduce((a, b) -> a + "; " + b)
				.orElse(ex.getMessage());

		return problemDetail(HttpStatus.BAD_REQUEST, "Validation failed", detail);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ProblemDetail handleMissingParameter(MissingServletRequestParameterException ex) {
		return problemDetail(HttpStatus.BAD_REQUEST, "Missing request parameter", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String detail = "Parameter '%s' should be of type %s".formatted(
				ex.getName(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

		return problemDetail(HttpStatus.BAD_REQUEST, "Type mismatch", detail);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		String detail = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.reduce((a, b) -> a + "; " + b)
				.orElse(ex.getMessage());

		return problemDetail(HttpStatus.BAD_REQUEST, "Validation failed", detail);
	}

	@ExceptionHandler({ NoResourceFoundException.class, NoHandlerFoundException.class })
	public ProblemDetail handleNotFound(Exception ex) {
		return problemDetail(HttpStatus.NOT_FOUND, "Not found", "The requested route does not exist");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
		return problemDetail(HttpStatus.UNAUTHORIZED, "Authentication failed", "Invalid username or password");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
		return problemDetail(HttpStatus.FORBIDDEN, "Access denied", "You do not have permission to access this resource");
	}

	private ProblemDetail problemDetail(HttpStatus status, String title, String detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
		problemDetail.setTitle(title);
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}

}
