package boardgameconnect.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
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

import boardgameconnect.dto.AssociationProfile;
import boardgameconnect.dto.PlayerProfile;
import boardgameconnect.dto.auth.login.LoginRequest;
import boardgameconnect.dto.auth.login.LoginResponse;
import boardgameconnect.dto.auth.register.AssociationDetails;
import boardgameconnect.dto.auth.register.RegistrationRequest;
import boardgameconnect.exception.EmailAlreadyInUseException;
import boardgameconnect.exception.InvalidCredentialsException;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserRole;
import boardgameconnect.service.auth.login.AssociationLoginService;
import boardgameconnect.service.auth.login.PlayerLoginService;
import boardgameconnect.service.auth.register.AssociationRegistrationService;
import boardgameconnect.service.auth.register.PlayerRegistrationService;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    private static final String ASSOCIATION_LOGIN_URI = "/api/v1/auth/login/association";
    private static final String PLAYER_LOGIN_URI = "/api/v1/auth/login/player";
    private static final String PLAYER_REGISTER_URI = "/api/v1/auth/register/player";
    private static final String ASSOCIATION_REGISTER_URI = "/api/v1/auth/register/association";

    private static final String PLAYER_EMAIL_STRING = "player@example.com";
    private static final String PLAYER_NAME = "Mario Rossi";

    private static final String ASSOCIATION_EMAIL_STRING = "assoc@example.com";
    private static final String ASSOCIATION_NAME = "BoardGames Inc";
    private static final String ASSOCIATION_ADDRESS = "Via Roma 1";
    private static final String ASSOCIATION_TAXCODE = "TAX123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlayerLoginService playerLoginService;

    @MockitoBean
    private AssociationLoginService associationLoginService;

    @MockitoBean
    private PlayerRegistrationService playerRegistrationService;

    @MockitoBean
    private AssociationRegistrationService associationRegistrationService;

    @Test
    void loginPlayerShouldReturnTokenAndPlayerProfile() throws Exception {
	var email = new Email(PLAYER_EMAIL_STRING);
	var request = new LoginRequest(email, "password");
	var playerProfile = new PlayerProfile("uuid-123", PLAYER_EMAIL_STRING, PLAYER_NAME, UserRole.PLAYER);
	var response = new LoginResponse<PlayerProfile>("valid_token", playerProfile);
	when(playerLoginService.login(request)).thenReturn(response);

	mockMvc.perform(post(PLAYER_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken", is("valid_token")))
		.andExpect(jsonPath("$.profile.name", is("Mario Rossi")))
		.andExpect(jsonPath("$.profile.role", is("PLAYER")));

	verifyNoInteractions(associationLoginService);
    }

    @Test
    void loginPlayerInvalidCredentials() throws Exception {
	var email = new Email(PLAYER_EMAIL_STRING);
	var request = new LoginRequest(email, "wrongpass");

	when(playerLoginService.login(request)).thenThrow(new InvalidCredentialsException("Invalid email or password"));

	mockMvc.perform(post(PLAYER_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.error").value("Unauthorized"))
		.andExpect(jsonPath("$.message").value("Invalid email or password"))
		.andExpect(jsonPath("$.path").value(PLAYER_LOGIN_URI));
    }

    @Test
    void loginPlayerServerError() throws Exception {
	var email = new Email(PLAYER_EMAIL_STRING);
	var request = new LoginRequest(email, "password");

	when(playerLoginService.login(request)).thenThrow(new RuntimeException("Database connection failed"));

	mockMvc.perform(post(PLAYER_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.status").value(500))
		.andExpect(jsonPath("$.error").value("Internal Server Error"))
		.andExpect(jsonPath("$.message").value("An internal error occurred"));
    }

    @Test
    void loginAssociationShouldReturnTokenAndAssociationProfile() throws Exception {
	var email = new Email(ASSOCIATION_EMAIL_STRING);
	var request = new LoginRequest(email, "password");
	var associationDto = new AssociationProfile("uuid-456", ASSOCIATION_EMAIL_STRING, ASSOCIATION_NAME,
		ASSOCIATION_TAXCODE, ASSOCIATION_ADDRESS, UserRole.ASSOCIATION);
	LoginResponse<AssociationProfile> response = new LoginResponse<>("valid_token", associationDto);

	when(associationLoginService.login(request)).thenReturn(response);

	mockMvc.perform(post(ASSOCIATION_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken", is("valid_token")))
		.andExpect(jsonPath("$.profile.name", is(ASSOCIATION_NAME)))
		.andExpect(jsonPath("$.profile.taxCode", is(ASSOCIATION_TAXCODE)))
		.andExpect(jsonPath("$.profile.address", is(ASSOCIATION_ADDRESS)));

	verifyNoInteractions(playerLoginService);
    }

    @Test
    void loginAssociationInvalidCredentials() throws Exception {
	var email = new Email(ASSOCIATION_EMAIL_STRING);
	LoginRequest request = new LoginRequest(email, "badpass");

	when(associationLoginService.login(request))
		.thenThrow(new InvalidCredentialsException("Invalid email or password"));

	mockMvc.perform(post(ASSOCIATION_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.status").value(401))
		.andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginAssociationServerError() throws Exception {
	var email = new Email(ASSOCIATION_EMAIL_STRING);
	LoginRequest request = new LoginRequest(email, "password");

	when(associationLoginService.login(request))
		.thenThrow(new NullPointerException("Something went wrong internally"));

	mockMvc.perform(post(ASSOCIATION_LOGIN_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.status").value(500))
		.andExpect(jsonPath("$.message").value("An internal error occurred"));
    }

    @Test
    void registerPlayerWhenRequestIsValidShouldReturn201() throws Exception {
	var email = new Email(PLAYER_EMAIL_STRING);
	var password = "testPassword";
	String name = PLAYER_NAME;
	var request = new RegistrationRequest<Void>(email, password, name, null);

	mockMvc.perform(post(PLAYER_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    void registerPlayerWhenEmailIsAlreadyInUseShouldReturn409() throws Exception {
	var email = new Email(PLAYER_EMAIL_STRING);
	var password = "testPassword";
	String name = PLAYER_NAME;
	RegistrationRequest<Void> request = new RegistrationRequest<>(email, password, name, null);

	doThrow(new EmailAlreadyInUseException("Email already registered")).when(playerRegistrationService)
		.register(request);

	mockMvc.perform(post(PLAYER_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
		.andExpect(jsonPath("$.message").value("Email already registered"))
		.andExpect(jsonPath("$.path").value(PLAYER_REGISTER_URI));
    }

    @Test
    void registerPlayerWithInvalidDataShouldReturn400() throws Exception {
	var request = new RegistrationRequest<>(null, "123", "", null);

	mockMvc.perform(post(PLAYER_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
		.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Invalid data"))
		.andExpect(jsonPath("$.path").value(PLAYER_REGISTER_URI));

	verifyNoInteractions(playerRegistrationService);
    }

    @Test
    void registerAssociationWhenRequestIsValidShouldReturn201() throws Exception {
	var email = new Email(ASSOCIATION_EMAIL_STRING);
	var password = "testPassword";
	String name = ASSOCIATION_NAME;
	var request = new RegistrationRequest<AssociationDetails>(email, password, name,
		new AssociationDetails(ASSOCIATION_TAXCODE, ASSOCIATION_ADDRESS));

	mockMvc.perform(post(ASSOCIATION_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    void registerAssociationWhenEmailIsAlreadyInUseShouldReturn409() throws Exception {
	var email = new Email(ASSOCIATION_EMAIL_STRING);
	var password = "testPassword";
	String name = ASSOCIATION_NAME;
	RegistrationRequest<AssociationDetails> request = new RegistrationRequest<>(email, password, name,
		new AssociationDetails(ASSOCIATION_TAXCODE, ASSOCIATION_ADDRESS));

	doThrow(new EmailAlreadyInUseException("Email already registered")).when(associationRegistrationService)
		.register(request);

	mockMvc.perform(post(ASSOCIATION_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
		.andExpect(jsonPath("$.message").value("Email already registered"))
		.andExpect(jsonPath("$.path").value(ASSOCIATION_REGISTER_URI));
    }

    @Test
    void registerAssociationWithInvalidDataShouldReturn400() throws Exception {
	var request = new RegistrationRequest<AssociationDetails>(null, "123", "", null);

	mockMvc.perform(post(ASSOCIATION_REGISTER_URI).contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
		.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Invalid data"))
		.andExpect(jsonPath("$.path").value(ASSOCIATION_REGISTER_URI));

	verifyNoInteractions(associationRegistrationService);
    }
}