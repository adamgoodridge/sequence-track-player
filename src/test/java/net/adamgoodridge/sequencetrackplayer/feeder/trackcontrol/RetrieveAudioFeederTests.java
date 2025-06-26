package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
class RetrieveAudioFeederTests extends AbstractSpringBootTest {

	@BeforeEach
	void setUp() {
		 // Use helper methods to initialize components
		initializeComponents();
	}

	private void initializeComponents() {
		new GetIndexByRandomStrategy(PreferredRandomSettings.builder().build());
		new PreferredRandomSettings.Builder().build();
		new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test", new GetIndexByRandomStrategy(PreferredRandomSettings.builder().build()));
		new GetIndexByPathStrategy();
		new Path("test");
	}

	@Test
	void RandomStrategy_WithTimePreference_ShouldReturnTrackAtSpecifiedTime() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.time(12)
					.build();
			AudioIOFileManager result = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test", new GetIndexByRandomStrategy(settings)).compute();
			assertNotNull(result);
			System.out.println("File: " + result.getFile().getFullPath());
			assertTrue(result.getFile().getFileName().contains("12-"));
		}
	}

	@Test
	void RandomStrategy_ShouldThrowException_WhenNoFilesFound() {
		assertThrows(GetFeedException.class, () -> {
			try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
				PreferredRandomSettings settings = new PreferredRandomSettings.Builder().build();
				new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("nonexistent", new GetIndexByRandomStrategy(settings)).compute();
			}
		});
	}

	@Test
	void RandomStrategy_ShouldThrowException_WhenSubFilesFound() {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder().build();
			GetFeedException exception = assertThrows(GetFeedException.class, () -> new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test/nonexistent", new GetIndexByRandomStrategy(settings)).compute());
			assertEquals("Cannot find random track for /mnt/path/test/nonexistent, Reason: Cannot find next folder: /mnt/path/test/nonexistent", exception.getMessage());
		}
	}

	@Test
	void RandomStrategy_WithDayPreference() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.day("Monday")
					.build();
			AudioIOFileManager result = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("FeedB", new GetIndexByRandomStrategy(settings)).compute();
			assertNotNull(result);
			assertTrue(result.getFile().getFileName().contains("Monday"));
		}
	}
	@Test
	void RandomStrategy_WithDayPreference_ShouldRWhenTheExactTimeFound() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.day("Monday")
					.time(12)
					.build();
			AudioIOFileManager result = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("FeedB/2023/2023-06_June/", new GetIndexByRandomStrategy(settings)).compute();
			System.out.println("File: " + result.getFile().getFullPath());
			assertTrue(result.getFile().getFileName().contains("Monday"));
			assertTrue(result.getFile().getFileName().contains("12-"));
		}
	}
	@Test
	void RandomStrategy_WithDayPreference_ShouldReturnTrackOnSpecifiedDayAndTimeWhenTheExactTimeNotFound() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.time(15)
					.build();
			AudioIOFileManager result = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("FeedB/2023/2023-06_June/", new GetIndexByRandomStrategy(settings)).compute();
			System.out.println("File: " + result.getFile().getFullPath());
			assertTrue(result.getFile().getFileName().contains("17-"));
		}
	}

	@Test
	void WithDayPreference_ShouldReturnTrackOnSpecifiedDayAndTimeWhenNoTimeNotFound() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.time(15)
					.build();
			AudioIOFileManager result = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test/2019/", new GetIndexByRandomStrategy(settings)).compute();
			System.out.println("File: " + result.getFile().getFullPath());
			assertNotNull(result.getFile());
		}
	}

	@Test
	void PathStrategy_ShouldReturnCorrectTrack_WhenPathExists() throws GetFeedException {
		try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
			net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder retrieveAudioFeeder = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", new GetIndexByPathStrategy());
			AudioIOFileManager result = retrieveAudioFeeder.compute();
			assertNotNull(result);
			assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
		}
	}

	@Test
	void PathStrategy_ShouldThrowException_WhenPathDoesNotExist() {
		assertThrows(GetFeedException.class, () -> {
			try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
				net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder retrieveAudioFeeder = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test/2023/nonexistent", new GetIndexByPathStrategy());
				retrieveAudioFeeder.compute();
			}
		});
	}

	@Test
	void PathStrategy_ShouldThrowException_WhenFileNotFoundInFolder() {
		assertThrows(GetFeedException.class, () -> {
			try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
				net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder retrieveAudioFeeder = new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test/2023/2023-02_February/2023-02-01_Wednesday/nonexistent.mp3", new GetIndexByPathStrategy());
				retrieveAudioFeeder.compute();
			}
		});
	}
}
