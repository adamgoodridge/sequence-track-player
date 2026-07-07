package net.adamgoodridge.sequencetrackplayer.utils;

import net.adamgoodridge.sequencetrackplayer.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeedNameUtilsTest extends AbstractSpringBootTest {

	@Test
	void getFeedName_returnsSegmentBeforeYear_simplePath() {
		assertEquals("FeedB", FeedNameUtils.getFeedName("/mnt/path/FeedB/2020/audio.mp3"));
	}

	@Test
	void getFeedName_returnsSegmentBeforeYear_nestedPath() {
		assertEquals("FeedB/e", FeedNameUtils.getFeedName("FeedB/e/2020/audio.mp3"));
	}
	@Test
	void getFeedName_returnsSegmentBeforeDate_nestedPath() {
		assertEquals("FeedB/e", FeedNameUtils.getFeedName("FeedB/e/2020-01-01/audio.mp3"));
	}

	@Test
	void getFeedName_returnsSegmentBeforeYear_deepNestedPath() {
		assertEquals("FeedB/e/b", FeedNameUtils.getFeedName("P:\\\\AUDIOFILE\\\\FULLY_PROCESSED\\\\FeedB/e/b/2005/audio.mp3"));
	}

	@Test
	void getFeedName_ignoresLeadingAndTrailingSlashes() {
		assertEquals("FeedB/e", FeedNameUtils.getFeedName("/FeedB/e/2020/audio.mp3/"));
	}

	@Test
	void getFeedName_returnsNormalizedInputWhenNoYearSegmentExists() {
		assertEquals("FeedB/e/audio.mp3", FeedNameUtils.getFeedName("/FeedB/e/audio.mp3/"));
	}

	@Test
	void getFeedName_returnsEmptyWhenYearIsFirstSegment() {
		assertEquals("", FeedNameUtils.getFeedName("2020/audio.mp3"));
	}

	@Test
	void getFeedName_returnsEmptyForNullOrBlankInput() {
		assertEquals("", FeedNameUtils.getFeedName(null));
		assertEquals("", FeedNameUtils.getFeedName("  "));
	}
}

