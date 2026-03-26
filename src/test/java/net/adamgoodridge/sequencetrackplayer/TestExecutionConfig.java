package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManagerService;
import net.adamgoodridge.sequencetrackplayer.utils.RandomNumberGenerator;
import net.adamgoodridge.sequencetrackplayer.settings.SettingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class to override async execution in test environments.
 * Uses a same-thread executor so MockedStatic is visible on the worker thread.
 */
@Configuration
public class TestExecutionConfig {

	/**
	 * Provides AudioIOFileManagerService with a same-thread (synchronous) executor.
	 * This ensures MockedStatic mocks are visible when CompletableFuture tasks run,
	 * since Mockito's MockedStatic is thread-local.
	 */
	@Bean
	@Primary
	public AudioIOFileManagerService audioIOFileManagerService(SettingService settingService, RandomNumberGenerator randomNumberGenerator) {
		// Runnable::run executes tasks on the calling thread (synchronously),
		// making MockedStatic mocks visible to the CompletableFuture task.
		return new AudioIOFileManagerService(settingService, randomNumberGenerator, Runnable::run);
	}
}

