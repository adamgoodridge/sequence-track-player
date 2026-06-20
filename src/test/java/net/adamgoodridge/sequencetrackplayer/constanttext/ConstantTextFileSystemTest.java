package net.adamgoodridge.sequencetrackplayer.constanttext;

import net.adamgoodridge.sequencetrackplayer.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantTextFileSystemTest extends AbstractSpringBootTest {
	@Test
	public void testTrackUrl() {
		ConstantTextFileSystem constantTextFileSystem = ConstantTextFileSystem.getInstance();
		assertEquals("https://testplayer.adamgoodridge.net", constantTextFileSystem.getTrackPlayerUrl());
	}
}
