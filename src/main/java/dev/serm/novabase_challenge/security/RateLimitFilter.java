package dev.serm.novabase_challenge.security;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.Refill;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class RateLimitFilter extends OncePerRequestFilter {

	private static final String LOGIN_PATH = "/auth/login";
	private static final String RETRY_AFTER = "Retry-After";
	private static final long MAX_BUCKETS = 10_000;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

	private final Bandwidth loginBandwidth;
	private final Bandwidth apiBandwidth;
	private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	public RateLimitFilter(long loginLimit, long loginRefillMinutes, long apiLimit, long apiRefillMinutes) {
		this.loginBandwidth = Bandwidth.classic(loginLimit,
				Refill.greedy(loginLimit, Duration.ofMinutes(loginRefillMinutes)));
		this.apiBandwidth = Bandwidth.classic(apiLimit,
				Refill.greedy(apiLimit, Duration.ofMinutes(apiRefillMinutes)));
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String clientIp = resolveClientIp(request);
		boolean isLogin = request.getRequestURI().equals(LOGIN_PATH)
				|| request.getRequestURI().startsWith(LOGIN_PATH + "/");

		Bucket bucket = bucketFor(clientIp, isLogin);
		if (bucket.tryConsume(1)) {
			filterChain.doFilter(request, response);
			return;
		}

		long retryAfterSeconds = retryAfterSeconds(bucket);
		response.setHeader(RETRY_AFTER, String.valueOf(retryAfterSeconds));
		writeTooManyRequests(response, request, retryAfterSeconds);
	}

	private Bucket bucketFor(String clientIp, boolean isLogin) {
		String key = (isLogin ? "login:" : "api:") + clientIp;

		if (buckets.containsKey(key)) {
			return buckets.get(key);
		}
		if (buckets.size() >= MAX_BUCKETS) {
			// Fail open if the map is saturated: better to serve than to OOM.
			return Bucket.builder().addLimit(isLogin ? loginBandwidth : apiBandwidth).build();
		}

		return buckets.computeIfAbsent(key,
				k -> Bucket.builder().addLimit(isLogin ? loginBandwidth : apiBandwidth).build());
	}

	private String resolveClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank()) {
			return forwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private long retryAfterSeconds(Bucket bucket) {
		EstimationProbe probe = bucket.estimateAbilityToConsume(1);
		return (long) Math.ceil(probe.getNanosToWaitForRefill() / 1_000_000_000.0);
	}

	private void writeTooManyRequests(
			HttpServletResponse response, HttpServletRequest request, long retryAfterSeconds) throws IOException {

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
				HttpStatus.TOO_MANY_REQUESTS,
				"Too many requests from this IP. Try again in " + retryAfterSeconds + " seconds.");
		problemDetail.setTitle("Too many requests");
		problemDetail.setInstance(URI.create(request.getRequestURI()));
		problemDetail.setProperty("timestamp", Instant.now());

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), problemDetail);
	}

}
