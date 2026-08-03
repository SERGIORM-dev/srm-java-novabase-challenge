package dev.serm.novabase_challenge.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.serm.novabase_challenge.exception.GlobalExceptionHandler;
import dev.serm.novabase_challenge.security.JwtService;
import dev.serm.novabase_challenge.security.SecurityConfig;
import dev.serm.novabase_challenge.security.UserDetailsServiceImpl;
import dev.serm.novabase_challenge.service.ItemService;

@WebMvcTest(ItemController.class)
@Import({ GlobalExceptionHandler.class, SecurityConfig.class, dev.serm.novabase_challenge.security.RestAuthenticationEntryPoint.class })
class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ItemService itemService;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private UserDetailsServiceImpl userDetailsService;

	@Test
	void getTitles_returnsTitlesAsUtf8Json() throws Exception {
		when(itemService.getTitles(3.0)).thenReturn(List.of("Cashback Plus Card", "Student Starter Card"));

		mockMvc.perform(get("/items/titles").param("rating", "3.0").with(user("alice")))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$[0]").value("Cashback Plus Card"))
				.andExpect(jsonPath("$[1]").value("Student Starter Card"));
	}

	@Test
	void getTitles_returnsEmptyArrayWhenNoMatches() throws Exception {
		when(itemService.getTitles(0.0)).thenReturn(List.of());

		mockMvc.perform(get("/items/titles").param("rating", "0.0").with(user("alice")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void getTitles_returns400WhenRatingParameterIsMissing() throws Exception {
		mockMvc.perform(get("/items/titles").with(user("alice")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Missing request parameter"));
	}

	@Test
	void getTitles_returns400WhenRatingIsNegative() throws Exception {
		mockMvc.perform(get("/items/titles").param("rating", "-1").with(user("alice")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Validation failed"));
	}

	@Test
	void getTitles_returns400WhenRatingIsNotANumber() throws Exception {
		mockMvc.perform(get("/items/titles").param("rating", "abc").with(user("alice")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Type mismatch"));
	}

	@Test
	void getTitles_returns401WhenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/items/titles").param("rating", "3.0"))
				.andExpect(status().isUnauthorized());
	}

}
