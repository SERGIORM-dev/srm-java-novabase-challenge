package dev.serm.novabase_challenge.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private final JwtService jwtService = new JwtService(
			"unit-test-secret-please-replace-32-bytes-minimum-1234567890",
			60);

	@Test
	void generateToken_producesTokenContainingTheUsernameAsSubject() {
		String token = jwtService.generateToken("alice");

		assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
	}

	@Test
	void isValid_returnsTrueForATokenJustGenerated() {
		String token = jwtService.generateToken("bob");

		assertThat(jwtService.isValid(token)).isTrue();
	}

	@Test
	void isValid_returnsFalseForATamperedToken() {
		String token = jwtService.generateToken("carol");

		assertThat(jwtService.isValid(token + "tampered")).isFalse();
	}

	@Test
	void isValid_returnsFalseForATokenSignedWithADifferentSecret() {
		JwtService otherService = new JwtService(
				"a-completely-different-secret-32-bytes-minimum-0987654321",
				60);

		String token = otherService.generateToken("alice");

		assertThat(jwtService.isValid(token)).isFalse();
	}

	@Test
	void isValid_returnsFalseForAnExpiredToken() {
		JwtService shortLivedService = new JwtService(
				"unit-test-secret-please-replace-32-bytes-minimum-1234567890",
				-1);

		String token = shortLivedService.generateToken("alice");

		assertThat(shortLivedService.isValid(token)).isFalse();
	}

}
