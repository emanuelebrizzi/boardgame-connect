package boardgameconnect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import boardgameconnect.service.BoardgameImportService;

@Configuration
public class DataInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

	private static final String INITIAL_BOARDGAME_CSV_PATH = "/data/boardgame.csv";
	private static final String DELIMITER = ";";

	@Bean
	CommandLineRunner initDatabase(BoardgameImportService importService) {
		return args -> {
			logger.info("Initializing application data...");
			try {
				importService.importInitialData(INITIAL_BOARDGAME_CSV_PATH, DELIMITER);
			} catch (Exception e) {
				logger.error("Failed to initialize database: ", e);
			}
		};
	}
}