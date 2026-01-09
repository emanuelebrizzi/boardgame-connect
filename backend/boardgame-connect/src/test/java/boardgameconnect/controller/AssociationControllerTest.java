package boardgameconnect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import boardgameconnect.config.SecurityConfig;
import boardgameconnect.dto.AssociationSummary;
import boardgameconnect.service.association.AssociationService;

@WebMvcTest(AssociationController.class)
@Import(SecurityConfig.class)
class AssociationControllerTest {

	private static final String BASE_URI = "/api/v1/associations";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AssociationService associationService;

//	@Autowired
//	private ObjectMapper objectMapper;

	@Test
	void testGetAssociationsReturnsTheListOfAssociations() throws Exception {
		var association1 = new AssociationSummary("test_id_1", "test_name_1", "test_address_1");
		var association2 = new AssociationSummary("test_id_2", "test_name_2", "test_address_3");

		when(associationService.getAllAssociations()).thenReturn(List.of(association1, association2));

		mockMvc.perform(get(BASE_URI)
				.with(jwt().jwt(j -> j.claim("sub", "test@email,com"))
						.authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is("test_id_1")))
				.andExpect(jsonPath("$[1].id", is("test_id_2")));

		verify(associationService).getAllAssociations();
	}

}
