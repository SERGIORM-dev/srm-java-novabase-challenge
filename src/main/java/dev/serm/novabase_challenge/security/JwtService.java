package dev.serm.novabase_challenge.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtService {

	private final Key signingKey;
	private final long expirationMillis;

	public JwtService(
			@Value("${security.jwt.secret}") String secret,
			@Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMillis = expirationMinutes * 60 * 1000;
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationMillis);

		return Jwts.builder()
				.subject(username)
				.issuedAt(now)
				.expiration(expiration)
				.signWith(signingKey)
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean isValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith((javax.crypto.SecretKey) signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

}
