package net.adamgoodridge.sequencetrackplayer.constanttext;

import jakarta.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class ConstantTextFileSystem {
	private static final String WINDOWS_SLASH = "\\";
	private static final String FORWARD_SLASH = "/";
	@Value("${file.slash}")
	@SuppressWarnings("unused")
	private String slash;

	@Value("${file.share.windows.root}")
	@SuppressWarnings("unused")
	private String windowsSharePath;

	@Value("${file.share.root}")
	@SuppressWarnings("unused")
	private String sharePath;
	private ConstantTextFileSystem() {
		// private constructor to prevent instantiation
	}
	private static ConstantTextFileSystem instance;
	@PostConstruct
	@SuppressWarnings("unused")
	public void init() {
		instance = this;
	}

	public static ConstantTextFileSystem getInstance() {
		return instance;
	}
	public String getSharePath() {
		return sharePath;
	}


	public String getWindowsSharePath() {
		return windowsSharePath;
	}


	public String getSlash() {
		return slash;
	}
	public String getForwardSlash() {
		return FORWARD_SLASH;
	}
	public String getWindowsSlash() {
		return WINDOWS_SLASH;
	}
}