package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.exception.DataImportException;
import boardgameconnect.exception.FileNotFoundException;
import boardgameconnect.model.Boardgame;

@ExtendWith(MockitoExtension.class)
class BoardgameImportServiceTest {

	@Mock
	private BoardgameRepository repository;

	@InjectMocks
	private BoardgameImportService importService;

	private String csvContent;

	private String delimiter;

	@BeforeEach
	void setUp() {
		csvContent = "name;minPlayer;maxPlayer;minTime;timePerPlayer;imagePath\n"
				+ "Catan;3;4;60;15;/images/catan.png\n" + "Dixit;3;6;30;5;/images/dixit.png";
		delimiter = ";";
	}

	@Test
	void importFromStreamShouldSaveBoardgameWhenNameIsNotRegistered() {
		InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

		when(repository.existsByNameIgnoreCase("Catan")).thenReturn(false);
		when(repository.existsByNameIgnoreCase("Dixit")).thenReturn(true);

		importService.importFromStream(inputStream, delimiter);

		verify(repository, times(1)).save(any(Boardgame.class));

		verify(repository, times(1)).existsByNameIgnoreCase("Catan");
		verify(repository, times(1)).existsByNameIgnoreCase("Dixit");
	}

	@Test
	void importFromStreamShouldSkipRowWhenDelimiterIsWrong() {
		String wrongDelimiter = ",";

		InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

		importService.importFromStream(is, wrongDelimiter);

		verify(repository, never()).save(any(Boardgame.class));
	}

	@Test
	void importShouldThrowFileNotFoundExceptionWhenResourceFileNotFound() {
		String nonExistingPath = "/file-che-non-esiste.csv";

		FileNotFoundException exception = assertThrows(FileNotFoundException.class,
				() -> importService.importInitialData(nonExistingPath, delimiter));

		assertTrue(exception.getMessage().contains("Resource file not found at " + nonExistingPath));

		verifyNoInteractions(repository);
	}

	@Test
	void importFromStreamShouldHandleSpacesAndNotSaveWhenNameMatchesAfterTrim() {
		String gameWithSpaces = "  Catan  ";
		String cleanName = "Catan";
		String csvContent = "name;minPlayer;maxPlayer;minTime;timePerPlayer;imagePath\n" + gameWithSpaces
				+ ";3;4;60;15;/images/catan.png";
		InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

		when(repository.existsByNameIgnoreCase(cleanName)).thenReturn(true);

		importService.importFromStream(is, delimiter);

		verify(repository, never()).save(any(Boardgame.class));
		verify(repository).existsByNameIgnoreCase(cleanName);
	}

	@Test
	void importFromStreamShouldSkipRowWhenDataIsMalformed() {
		String malformedCsv = "name;minPlayer;maxPlayer;minTime;timePerPlayer;imagePath\n"
				+ "BrokenGame;ABC;4;60;15;/images/error.png";
		InputStream is = new ByteArrayInputStream(malformedCsv.getBytes(StandardCharsets.UTF_8));

		when(repository.existsByNameIgnoreCase("BrokenGame")).thenReturn(false);

		importService.importFromStream(is, delimiter);

		verify(repository, never()).save(any(Boardgame.class));
	}

	@Test
	void importFromStreamShouldThrowDataImportExceptionWhenStreamFails() throws IOException {
		InputStream mockInputStream = mock(InputStream.class);
		when(mockInputStream.read(any(byte[].class), anyInt(), anyInt()))
				.thenThrow(new java.io.IOException("Simulated IO error"));

		DataImportException exception = assertThrows(DataImportException.class,
				() -> importService.importFromStream(mockInputStream, delimiter));

		assertTrue(exception.getMessage().contains("Fatal error: Failed to parse boardgame CSV stream"));

		verifyNoInteractions(repository);
	}
}