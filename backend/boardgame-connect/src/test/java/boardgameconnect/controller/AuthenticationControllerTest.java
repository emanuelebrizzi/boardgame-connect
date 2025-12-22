package boardgameconnect.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.dto.authorization.LoginRequest;
import boardgameconnect.dto.authorization.LoginResponse;
import boardgameconnect.exception.InvalidCredentialsException;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserRole;
import boardgameconnect.service.AssociationAuthService;
import boardgameconnect.service.PlayerAuthService;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlayerAuthService playerService;

    @MockitoBean
    private AssociationAuthService associationService;

    @Test
    void loginPlayerShouldReturnTokenAndPlayerProfile() throws Exception {
	var email = new Email("player@example.com");
	var request = new LoginRequest(email, "password");
	PlayerDto playerDto = new PlayerDto("uuid-123", "player@example.com", "MarioRossi", UserRole.PLAYER);
	LoginResponse<PlayerDto> response = new LoginResponse<>("valid_token", playerDto);
	when(playerService.login(request)).thenReturn(response);

	mockMvc.perform(post("/api/v1/auth/login/player").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken", is("valid_token")))
		.andExpect(jsonPath("$.profile.name", is("MarioRossi")))
		.andExpect(jsonPath("$.profile.role", is("PLAYER")));

	verifyNoInteractions(associationService);
    }

    @Test
    void loginPlayerInvalidCredentials() throws Exception {
	var email = new Email("player@example.com");
	LoginRequest request = new LoginRequest(email, "wrongpass");

	when(playerService.login(request)).thenThrow(new InvalidCredentialsException("Invalid email or password"));

	mockMvc.perform(post("/api/v1/auth/login/player").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.error").value("Unauthorized"))
		.andExpect(jsonPath("$.message").value("Invalid email or password"))
		.andExpect(jsonPath("$.path").value("/api/v1/auth/login/player"));
    }

    @Test
    void loginPlayerServerError() throws Exception {
	var email = new Email("player@example.com");
	LoginRequest request = new LoginRequest(email, "password");

	when(playerService.login(request)).thenThrow(new RuntimeException("Database connection failed"));

	mockMvc.perform(post("/api/v1/auth/login/player").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.status").value(500))
		.andExpect(jsonPath("$.error").value("Internal Server Error"))
		.andExpect(jsonPath("$.message").value("An internal error occurred"));
    }

    @Test
    void loginAssociationShouldReturnTokenAndAssociationProfile() throws Exception {
	var email = new Email("assoc@example.com");
	LoginRequest request = new LoginRequest(email, "password");
	AssociationDto associationDto = new AssociationDto("uuid-456", "assoc@example.com", "BoardGames Inc", "TAX123",
		"Via Roma 1", UserRole.ASSOCIATION);
	LoginResponse<AssociationDto> response = new LoginResponse<>("valid_token", associationDto);

	when(associationService.login(request)).thenReturn(response);

	mockMvc.perform(post("/api/v1/auth/login/association").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken", is("valid_token")))
		.andExpect(jsonPath("$.profile.name", is("BoardGames Inc")))
		.andExpect(jsonPath("$.profile.taxCode", is("TAX123")))
		.andExpect(jsonPath("$.profile.address", is("Via Roma 1")));

	verifyNoInteractions(playerService);
    }

    @Test
    void loginAssociationInvalidCredentials() throws Exception {
	var email = new Email("assoc@example.com");
	LoginRequest request = new LoginRequest(email, "badpass");

	when(associationService.login(request)).thenThrow(new InvalidCredentialsException("Invalid email or password"));

	mockMvc.perform(post("/api/v1/auth/login/association").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.status").value(401))
		.andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginAssociationServerError() throws Exception {
	var email = new Email("assoc@example.com");
	LoginRequest request = new LoginRequest(email, "password");

	when(associationService.login(request)).thenThrow(new NullPointerException("Something went wrong internally"));

	mockMvc.perform(post("/api/v1/auth/login/association").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.status").value(500))
		.andExpect(jsonPath("$.message").value("An internal error occurred"));
    }

}