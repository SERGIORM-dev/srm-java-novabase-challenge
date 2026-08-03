package dev.serm.novabase_challenge.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource");
		problemDetail.setTitle("Authentication required");
		problemDetail.setInstance(java.net.URI.create(request.getRequestURI()));
		problemDetail.setProperty("timestamp", Instant.now());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), problemDetail);
	}

}
