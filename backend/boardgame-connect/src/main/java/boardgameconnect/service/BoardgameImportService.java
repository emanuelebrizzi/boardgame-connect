package boardgameconnect.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import boardgameconnect.dao.BoardgameRepository;
import boardgameconnect.exception.DataImportException;
import boardgameconnect.exception.FileNotFoundException;
import boardgameconnect.model.Boardgame;

@Service
public class BoardgameImportService {

	private static final Logger logger = LoggerFactory.getLogger(BoardgameImportService.class);

	@Autowired
	private BoardgameRepository repository;

	public void importInitialData(String filePath, String delimiter) {
		InputStream is = getClass().getResourceAsStream(filePath);

		if (is != null) {
			logger.info("Starting import from path: {}", filePath);
			importFromStream(is, delimiter);
		} else {
			throw new FileNotFoundException("Resource file not found at " + filePath);
		}
	}

	@Transactional
	public void importFromStream(InputStream inputStream, String delimiter) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

			br.readLine();

			String line;
			int addedCount = 0;
			int skippedCount = 0;

			while ((line = br.readLine()) != null) {
				if (line.isBlank())
					continue;
				String[] values = line.split(delimiter, -1);
				String name = values[0].trim();

				if (!repository.existsByNameIgnoreCase(name)) {
					try {
						Boardgame game = mapToEntity(values);
						repository.save(game);
						addedCount++;
					} catch (Exception e) {
						logger.warn("Error parsing line: {}. Details: {}", line, e.getMessage());
					}
				} else {
					skippedCount++;
				}
			}
			logger.info("Import completed. Added: {}, Skipped (duplicates): {}", addedCount, skippedCount);

		} catch (Exception e) {
			throw new DataImportException("Fatal error: Failed to parse boardgame CSV stream", e);
		}
	}

	private Boardgame mapToEntity(String[] values) {
		// CSV Order: name, minPlayer, maxPlayer, minTime, timePerPlayer, imagePath
		return new Boardgame(values[0].trim(), // name
				Integer.parseInt(values[1].trim()), // minPlayer
				Integer.parseInt(values[2].trim()), // maxPlayer
				Integer.parseInt(values[3].trim()), // minTimeInMin
				Integer.parseInt(values[4].trim()), // timeInMinPerPlayer
				values[5].trim() // imagePath
		);
	}
}