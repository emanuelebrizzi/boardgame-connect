package boardgameconnect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import boardgameconnect.config.SecurityConfig;
import boardgameconnect.dto.association.AssociationSummary;
import boardgameconnect.exception.AssociationNotFoundException;
import boardgameconnect.exception.BoardgameInUseException;
import boardgameconnect.exception.BoardgameNotFoundException;
import boardgameconnect.model.Email;
import boardgameconnect.service.association.AssociationService;

@WebMvcTest(AssociationController.class)
@Import(SecurityConfig.class)
class AssociationControllerTest {

	private static final String BOARDGAME_ID = "test1";

	private static final Email ASSOCIATION_1_EMAIL = new Email("test@gmail.com");

	private static final String BASE_URI = "/api/v1/associations";

	private static final String ASSOCIATION_1_ID = "test";
	private static final String ASSOCIATION_1_NAME = "test_name";
	private static final String ASSOCIATION_1_ADDRESS = "test_address";

	private static final String ASSOCIATION_2_ID = "test";
	private static final String ASSOCIATION_2_NAME = "test2_name";
	private static final String ASSOCIATION_2_ADDRESS = "test2_address";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AssociationService associationService;

	@Test
	void testGetAssociationsReturnsAssociationSummeries() throws Exception {
		var association1 = new AssociationSummary(ASSOCIATION_1_ID, ASSOCIATION_1_NAME, ASSOCIATION_1_ADDRESS);
		var association2 = new AssociationSummary(ASSOCIATION_2_ID, ASSOCIATION_2_NAME, ASSOCIATION_2_ADDRESS);

		when(associationService.getAssociations()).thenReturn(List.of(association1, association2));

		mockMvc.perform(get(BASE_URI)
				.with(jwt().jwt(j -> j.claim("sub", "test@email,com"))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is(ASSOCIATION_1_ID)))
				.andExpect(jsonPath("$[1].id", is(ASSOCIATION_2_ID)));

		verify(associationService).getAssociations();
	}

	@Test
	void testGetAssociationsReturnsAssociationSummeriesWhenBoardgameIdIsValid() throws Exception {
		String boardgameId = "bg-123";
		var association1 = new AssociationSummary(ASSOCIATION_1_ID, ASSOCIATION_1_NAME, ASSOCIATION_1_ADDRESS);

		when(associationService.getAssociations(boardgameId)).thenReturn(List.of(association1));

		mockMvc.perform(get(BASE_URI).param("boardgameId", boardgameId)
				.with(jwt().jwt(j -> j.claim("sub", "test@email.com"))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(ASSOCIATION_1_ID)));

		verify(associationService).getAssociations(boardgameId);
	}

	@Test
	void testGetAssociationsReturnsNotFoundWhenBoardgameIdDoesNotExist() throws Exception {
		String invalidBoardgameId = "unknown-id";

		doThrow(new BoardgameNotFoundException("Boardgame not found with id: " + invalidBoardgameId))
				.when(associationService).getAssociations(invalidBoardgameId);

		mockMvc.perform(get(BASE_URI).param("boardgameId", invalidBoardgameId)
				.with(jwt().jwt(j -> j.claim("sub", "test@email.com"))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		verify(associationService).getAssociations(invalidBoardgameId);
	}

	@Test
	void testAddAssociationGamesReturnsOkWhenRequestIsValid() throws Exception {
		List<String> boardgameIds = List.of(BOARDGAME_ID, "test2");

		mockMvc.perform(post(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardgameIds)))
				.andExpect(status().isOk());

		verify(associationService).addBoardgamesToAssociation(boardgameIds, ASSOCIATION_1_EMAIL);
	}

	@Test
	void testAddAssociationGamesReturnsBadRequestWhenBodyIsInvalid() throws Exception {
		mockMvc.perform(post(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());

		verifyNoInteractions(associationService);
	}

	@Test
	void testAddAssociationGamesReturnsNotFoundWhenAssociationIsMissing() throws Exception {
		List<String> boardgameIds = List.of(BOARDGAME_ID, "test2");

		doThrow(new AssociationNotFoundException("Association not found with id: " + ASSOCIATION_1_ID))
				.when(associationService).addBoardgamesToAssociation(boardgameIds, ASSOCIATION_1_EMAIL);

		mockMvc.perform(post(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardgameIds)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("Association not found with id: " + ASSOCIATION_1_ID)));

		verify(associationService).addBoardgamesToAssociation(boardgameIds, ASSOCIATION_1_EMAIL);
	}

	@Test
	void testRemoveAssociationGamesReturnsOkWhenBodyIsValid() throws Exception {
		var boardgameIds = List.of(BOARDGAME_ID, "test2");

		mockMvc.perform(delete(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardgameIds)))
				.andExpect(status().isOk());

		verify(associationService).removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL);
	}

	@Test
	void testRemoveAssociationGamesReturnsNotFoundWhenAssociationIsMissing() throws Exception {
		var boardgameIds = List.of(BOARDGAME_ID);

		doThrow(new AssociationNotFoundException("Association not found")).when(associationService)
				.removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL);

		mockMvc.perform(delete(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardgameIds)))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.message", is("Association not found")));

		verify(associationService).removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL);
	}

	@Test
	void testRemoveAssociationGamesReturnsConflictWhenGameIsInUse() throws Exception {
		var boardgameIds = List.of(BOARDGAME_ID);
		String errorMessage = "Cannot remove boardgame because it has OPEN reservations.";

		doThrow(new BoardgameInUseException(errorMessage)).when(associationService)
				.removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL);

		mockMvc.perform(delete(BASE_URI + "/boardgames")
				.with(jwt().jwt(j -> j.claim("sub", ASSOCIATION_1_EMAIL))
						.authorities(new SimpleGrantedAuthority("ROLE_ASSOCIATION")))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardgameIds)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.message", is(errorMessage)));

		verify(associationService).removeBoardgamesFromAssociation(boardgameIds, ASSOCIATION_1_EMAIL);
	}

}
