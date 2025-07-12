package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class FileUtils {
	private static final String SLASH = ConstantTextFileSystem.getInstance().getSlash();
	private FileUtils() {
		// Private constructor to prevent instantiation
	}
	public static String removeLeadingSlashIfExist(String path) {
		if (path.startsWith(SLASH)) {
			return path.substring(1); // Remove leading slash if present
		}
		return path;
	}
	public static String removeTrailingSlash(String path) {
		if (path.endsWith(SLASH)) {
			return path.substring(0, path.length() - 1); // Remove trailing slash if present
		}
		return path;
	}
}
