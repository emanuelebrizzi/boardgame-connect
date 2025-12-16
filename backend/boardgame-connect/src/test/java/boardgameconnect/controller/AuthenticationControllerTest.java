package boardgameconnect.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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

import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.service.AuthenticationService;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void loginShouldReturnToken() throws Exception {
	var email = new Email("test@example.com");
	LoginRequest request = new LoginRequest(email, "password");

	Player user = new Player(email, "enc_pass", "Test");
	LoginResponse response = new LoginResponse("valid_token", user);

	when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

	mockMvc.perform(post("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken", is("valid_token")))
		.andExpect(jsonPath("$.user.username", is("Test")));
    }

}
