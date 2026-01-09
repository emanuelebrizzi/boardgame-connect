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

	private static final String INITIAL_CSV_PATH = "/boardgames.csv";

	@Bean
	CommandLineRunner initDatabase(BoardgameImportService importService) {
		return args -> {
			logger.info("Initializing application data...");
			try {
				importService.importInitialData(INITIAL_CSV_PATH);
			} catch (Exception e) {
				logger.error("Failed to initialize database: ", e);
			}
		};
	}
}