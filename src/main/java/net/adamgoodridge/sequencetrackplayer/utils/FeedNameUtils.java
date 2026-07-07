package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import org.jspecify.annotations.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class FeedNameUtils {
	private final static ConstantTextFileSystem constantTextFileSystem = ConstantTextFileSystem.getInstance();
	public static String getFeedName(String feedName) {
		if (feedName == null || feedName.isBlank())
			return "";
		feedName = replaceStartPath(feedName, constantTextFileSystem.getSharePath());
		feedName = replaceStartPath(feedName, constantTextFileSystem.getWindowsSharePath());
		String normalized = feedName.trim();
		String[] rawSegments = normalized.split("/");
		List<String> segments = Arrays.stream(rawSegments).filter(
				s -> !s.isBlank()
		).toList();

		for (int i = 0; i < segments.size(); i++) {
			if (isYearOrDateSegment(segments.get(i)))
				return String.join("/", segments.subList(0, i));
		}

		return String.join("/", segments);
	}

	private static @NonNull String replaceStartPath(String feedName, String startPath) {
		startPath = startPath.replace("\\","\\\\");
		if(feedName.startsWith(startPath))
			feedName = feedName.substring(startPath.length());
		return feedName;
	}

	private static boolean isYearOrDateSegment(String segment) {
		return segment.matches("\\d{4}") || segment.matches("\\d{4}-\\d{2}-\\d{2}");
	}
}
