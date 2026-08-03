package dev.serm.novabase_challenge.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTest {

	private final RateLimitFilter filter = new RateLimitFilter(2, 1, 3, 1);

	private MockHttpServletResponse perform(String uri, String ip) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
		request.setRemoteAddr(ip);
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, new MockFilterChain());
		return response;
	}

	@Test
	void apiBucketAllowsUpToLimitThenReturns429() throws Exception {
		assertEquals(200, perform("/items/titles", "10.0.0.1").getStatus());
		assertEquals(200, perform("/items/titles", "10.0.0.1").getStatus());
		assertEquals(200, perform("/items/titles", "10.0.0.1").getStatus());

		MockHttpServletResponse throttled = perform("/items/titles", "10.0.0.1");
		assertEquals(429, throttled.getStatus());
		assertEquals("application/json;charset=UTF-8", throttled.getContentType());
		assertTrue(throttled.getContentAsString().contains("Too many requests"));
		assertTrue(throttled.getContentAsString().contains("instance"));
		assertTrue(Long.parseLong(throttled.getHeader("Retry-After")) > 0);
	}

	@Test
	void loginAndApiBucketsAreIndependent() throws Exception {
		perform("/auth/login", "10.0.0.2");
		perform("/auth/login", "10.0.0.2");
		assertEquals(429, perform("/auth/login", "10.0.0.2").getStatus());

		assertEquals(200, perform("/items/titles", "10.0.0.2").getStatus());
	}

	@Test
	void bucketsAreKeyedPerClientIp() throws Exception {
		perform("/auth/login", "10.0.0.3");
		perform("/auth/login", "10.0.0.3");
		assertEquals(429, perform("/auth/login", "10.0.0.3").getStatus());

		assertEquals(200, perform("/auth/login", "10.0.0.4").getStatus());
	}

	@Test
	void xForwardedForHeaderWinsOverRemoteAddr() throws Exception {
		perform("/items/titles", "192.168.1.1");
		perform("/items/titles", "192.168.1.1");
		perform("/items/titles", "192.168.1.1");

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/items/titles");
		request.setRemoteAddr("192.168.1.1");
		request.addHeader("X-Forwarded-For", "203.0.113.9");
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, new MockFilterChain());

		assertEquals(200, response.getStatus());
	}
}
