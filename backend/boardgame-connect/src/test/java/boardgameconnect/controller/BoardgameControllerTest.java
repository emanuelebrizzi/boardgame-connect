package boardgameconnect.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import boardgameconnect.dto.BoardgameDto;
import boardgameconnect.service.BoardgameService;

@WebMvcTest(BoardgameController.class)
class BoardgameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BoardgameService boardgameService;

	@Test
	@WithMockUser
	void getBoardgames_ShouldReturnOkAndAllFields() throws Exception {
		BoardgameDto catan = new BoardgameDto("1", "Catan", 3, 4, 60, 15, "catan.png");
		BoardgameDto root = new BoardgameDto("2", "Root", 2, 4, 60, 20, "root.png");

		when(boardgameService.getAllBoardgames()).thenReturn(List.of(catan, root));

		mockMvc.perform(get("/boardgames").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].name").value("Catan")).andExpect(jsonPath("$[0].minPlayers").value(3))
				.andExpect(jsonPath("$[0].maxPlayers").value(4)).andExpect(jsonPath("$[0].timeMin").value(60))
				.andExpect(jsonPath("$[0].timePerPlayer").value(15))
				.andExpect(jsonPath("$[0].imagePath").value("catan.png")).andExpect(jsonPath("$[1].id").value("2"))
				.andExpect(jsonPath("$[1].name").value("Root")).andExpect(jsonPath("$[1].minPlayers").value(2))
				.andExpect(jsonPath("$[1].maxPlayers").value(4)).andExpect(jsonPath("$[1].timeMin").value(60))
				.andExpect(jsonPath("$[1].timePerPlayer").value(20))
				.andExpect(jsonPath("$[1].imagePath").value("root.png"));
	}
}