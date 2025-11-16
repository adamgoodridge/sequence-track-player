
package net.adamgoodridge.sequencetrackplayer.constanttext;

import jakarta.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

@Component
public class ConstantTextProperties {
	@Value("${other.commit.time}")
	private String compileTime;
	private ConstantTextProperties() {
		// private constructor to prevent instantiation
	}
	private static ConstantTextProperties instance;
	@PostConstruct
	@SuppressWarnings("unused")
	public void init() {
		instance = this;
	}
	public static ConstantTextProperties getInstance() {
		return instance;
	}
	public String getCompileTime() {
		return compileTime;
	}
	
}