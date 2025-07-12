package net.adamgoodridge.sequencetrackplayer;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.CompletableFuture;

@Configuration
public class GsonConfig {

	/**
	 * Creates a Gson instance that excludes CompletableFuture fields to avoid serialization issues
	 * with the internal 'result' field.
	 */
	@Bean
	@Primary
	public Gson gson() {
		return new GsonBuilder()
				.setExclusionStrategies(new ExclusionStrategy() {
					@Override
					public boolean shouldSkipField(FieldAttributes fieldAttributes) {
						return CompletableFuture.class.isAssignableFrom(fieldAttributes.getDeclaredClass());
					}

					@Override
					public boolean shouldSkipClass(Class<?> clazz) {
						return CompletableFuture.class.isAssignableFrom(clazz);
					}
				})
				.create();
	}
}

