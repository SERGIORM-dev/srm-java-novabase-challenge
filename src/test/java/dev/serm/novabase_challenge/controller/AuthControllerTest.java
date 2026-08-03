package dev.serm.novabase_challenge.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.serm.novabase_challenge.exception.GlobalExceptionHandler;
import dev.serm.novabase_challenge.security.JwtService;
import dev.serm.novabase_challenge.security.SecurityConfig;
import dev.serm.novabase_challenge.security.UserDetailsServiceImpl;

@WebMvcTest(AuthController.class)
@Import({ GlobalExceptionHandler.class, SecurityConfig.class, dev.serm.novabase_challenge.security.RestAuthenticationEntryPoint.class })
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private UserDetailsServiceImpl userDetailsService;

	@Test
	void login_returnsTokenForValidCredentials() throws Exception {
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "password123")))
				.thenReturn(new UsernamePasswordAuthenticationToken("alice", "password123"));
		when(jwtService.generateToken("alice")).thenReturn("fake.jwt.token");

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"username":"alice","password":"password123"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("fake.jwt.token"));
	}

	@Test
	void login_returns401ForInvalidCredentials() throws Exception {
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "wrong-password")))
				.thenThrow(new BadCredentialsException("Bad credentials"));

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"username":"alice","password":"wrong-password"}
						"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.title").value("Authentication failed"));
	}

	@Test
	void login_returns400WhenUsernameIsBlank() throws Exception {
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"username":"","password":"password123"}
						"""))
				.andExpect(status().isBadRequest());
	}

}
