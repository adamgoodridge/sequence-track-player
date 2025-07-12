package net.adamgoodridge.sequencetrackplayer;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Configuration class to disable async execution in test environments
 */
@Configuration
public class TestExecutionConfig {

	@PostConstruct
	public void setupTestExecution() {
		// Disable parallel execution for tests
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");

		// Force CompletableFuture to use the common ForkJoinPool with limited parallelism
		System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", "java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory");
	}

	@PreDestroy
	public void cleanupTestExecution() {
		// Clean up system properties when tests are done
		System.clearProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
		System.clearProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
	}
}

