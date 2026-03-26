package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveTrackControlTests extends AbstractSpringBootTest {
	@Autowired
	private AudioFeederRepository repository;

	// minimal filesystem map used by these tests
	private java.util.HashMap<String, String[]> feedBMock() {
		java.util.HashMap<String, String[]> map = new java.util.HashMap<>();
		map.put("/mnt/path", new String[]{"FeedB"});
		map.put("/mnt/path/FeedB", new String[]{"2023"});
		map.put("/mnt/path/FeedB/2023", new String[]{"2023-07_July", "2023-06_June"});
		map.put("/mnt/path/FeedB/2023/2023-07_July", new String[]{"2023-07-19_Wednesday","2023-07-20_Thursday","2023-07-21_Friday","2023-07-03_Monday"});
		map.put("/mnt/path/FeedB/2023/2023-07_July/2023-07-19_Wednesday",
				new String[]{"FEEDB_AUDIOFILE_2023-07-19_Wednesday_09-15-00.mp3","FEEDB_AUDIOFILE_2023-07-19_Wednesday_12-22-00.mp3","FEEDB_AUDIOFILE_2023-07-19_Wednesday_17-45-00.mp3"});
		map.put("/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday",
				new String[]{"FEEDB_AUDIOFILE_2023-07-20_Thursday_10-30-00.mp3","FEEDB_AUDIOFILE_2023-07-20_Thursday_12-45-00.mp3","FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3"});
		map.put("/mnt/path/FeedB/2023/2023-07_July/2023-07-21_Friday",
				new String[]{"FEEDB_AUDIOFILE_2023-07-21_Friday_09-30-00.mp3","FEEDB_AUDIOFILE_2023-07-21_Friday_12-15-00.mp3","FEEDB_AUDIOFILE_2023-07-21_Friday_15-00-00.mp3"});
		map.put("/mnt/path/FeedB/2023/2023-07_July/2023-07-03_Monday",
				new String[]{"FEEDB_AUDIOFILE_2023-07-03_Monday_09-00-00.mp3"});
		map.put("/mnt/path/FeedB/2023/2023-06_June", new String[]{"2023-06-23_Friday"});
		map.put("/mnt/path/FeedB/2023/2023-06_June/2023-06-23_Friday",
				new String[]{"FEEDB_AUDIOFILE_2023-06-23_Friday_17-15-00.mp3"});
		return map;
	}

	@BeforeEach
	void setUp() throws IOException {
		// fixes classNotFoundException, before loading the MockedConstruction
		new AudioFeederRepositoryMock().fillWithMockData(repository);
		new Path("/mnt/pathB"); //pre-load Path class to avoid ClassNotFoundException during MockedConstruction
		FileListSubFileWrapper.wrap("/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday");
		FileUtils.removeTrailingSlash("/mnt/");
	}
	@Test
	void MoveTrackWhenNextTrackGoToPrevious() {
		AudioFeeder audioFeeder = repository.findById(249L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(PreferredRandomSettings.builder().regularlyTrackChange(false).build(), audioFeeder);
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());
			Assertions.assertEquals(2, audioFeeder.getAudioIOFileManager().getFileNo());
			String expectedStartPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
			String actualStartPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedStartPath, actualStartPath, "Initial path does not match expected value");

			// Move to next track
			moveTrackControl.decreaseFileNo();

			// Verify the track movement
			assertNotNull(audioFeeder.getAudioIOFileManager());
			assertNotNull(audioFeeder.getAudioIOFileManager().getFile().getFileName(),
					"FEEDB_AUDIOFILE_2023-07-20_Thursday_12-45-00.mp3");


		}
	}
	@Test
	void MoveTrackWhenNextTrackInNextDirectory() {
		AudioFeeder audioFeeder = repository.findById(249L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(PreferredRandomSettings.builder().regularlyTrackChange(false).build(), audioFeeder);
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());
			Assertions.assertEquals(2, audioFeeder.getAudioIOFileManager().getFileNo());
			String expectedStartPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
			String actualStartPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedStartPath, actualStartPath, "Initial path does not match expected value");

			// Move to next track
			moveTrackControl.increaseFileNo();

			// Verify the track movement
			assertNotNull(audioFeeder.getAudioIOFileManager());
			assertNotNull(audioFeeder.getAudioIOFileManager().getFile().getFileName(),
					"FEEDB_AUDIOFILE_2023-07-21_Friday_09-30-00.mp3");


		}
	}

	@Test
	void MoveTrackWhenNextTrackInNextDirectoryAndGoBack() throws GetFeedError {
		AudioFeeder audioFeeder = repository.findById(249L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(PreferredRandomSettings.builder().regularlyTrackChange(false).build(), audioFeeder);
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());
			Assertions.assertEquals(2, audioFeeder.getAudioIOFileManager().getFileNo());
			String expectedStartPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
			String actualStartPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedStartPath, actualStartPath, "Initial path does not match expected value");

			// Move to next track
			moveTrackControl.increaseFileNo();
			Assertions.assertEquals(0, audioFeeder.getAudioIOFileManager().getCurrentPosition(),
					"Current position should be reset to 0 after moving to next track");
			// Verify the track movement
			assertNotNull(audioFeeder.getAudioIOFileManager());
			assertNotNull(audioFeeder.getAudioIOFileManager().getFile().getFileName(),
					"FEEDB_AUDIOFILE_2023-07-21_Friday_09-30-00.mp3");
			audioFeeder.getAudioIOFileManager().setCurrentPosition(10);
			// Go back to previous track
			moveTrackControl.decreaseFileNo();

			Assertions.assertEquals(expectedStartPath, actualStartPath, "Initial path does not match expected value");

			Assertions.assertEquals(0, audioFeeder.getAudioIOFileManager().getCurrentPosition(),
					"Current position should be reset to 0 after moving to next track");


		}
	}

	@Test
	void MoveTrackToPreviousDirectoryFirstTrack() {
		AudioFeeder audioFeeder = repository.findById(250L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(PreferredRandomSettings.builder().regularlyTrackChange(false).build(), audioFeeder);
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());
			Assertions.assertEquals(0, audioFeeder.getAudioIOFileManager().getFileNo());
			String expectedStartPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-21_Friday/FEEDB_AUDIOFILE_2023-07-21_Friday_09-30-00.mp3";
			String actualStartPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();

			Assertions.assertEquals(expectedStartPath, actualStartPath);

			// Move to previous track
			moveTrackControl.decreaseFileNo();

			// Verify we moved to previous directory's last track
			assertNotNull(audioFeeder.getAudioIOFileManager());
			String expectedPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
			String actualPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedPath, actualPath, "Should move to previous directory's last track");
			Assertions.assertEquals(2, audioFeeder.getAudioIOFileManager().getFileNo());
		}
	}

	@Test
	void MoveTrackToNextDirectoryWhenAtLastTrackOfMonth() {
		AudioFeeder audioFeeder = repository.findById(251L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(PreferredRandomSettings.builder().regularlyTrackChange(false).build(), audioFeeder);
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());
			Assertions.assertEquals(3, audioFeeder.getAudioIOFileManager().getFileNo());
			String expectedStartPath = "/mnt/path/FeedB/2023/2023-06_June/2023-06-23_Friday/FEEDB_AUDIOFILE_2023-06-23_Friday_17-15-00.mp3";
			String actualStartPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedStartPath, actualStartPath);

			// Move to next track
			moveTrackControl.increaseFileNo();

			// Verify we moved to next month's first file
			assertNotNull(audioFeeder.getAudioIOFileManager());
			String expectedPath = "/mnt/path/FeedB/2023/2023-07_July/2023-07-03_Monday/FEEDB_AUDIOFILE_2023-07-03_Monday_09-00-00.mp3";
			String actualPath = audioFeeder.getAudioIOFileManager().getCurrentFullPath();
			Assertions.assertEquals(expectedPath, actualPath);
			Assertions.assertEquals(0, audioFeeder.getAudioIOFileManager().getFileNo());
		}
	}

	@Test
	void MoveTrackWhenRandomTrackChangeEnabled() {
		AudioFeeder audioFeeder = repository.findById(249L).orElseThrow();

		// Configure to change track after 1 track played
		MoveTrackControl moveTrackControl = new MoveTrackControl(
			PreferredRandomSettings.builder()
				.regularlyTrackChange(true)
				.trackCurrentCount(1)
				.build(),
			audioFeeder
		);

		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Verify initial state
			Assertions.assertEquals(1, audioFeeder.getCurrentTrackCount());

			// This should trigger a random track change
			moveTrackControl.increaseFileNo();

			// Track count should be reset since we got a new random track
			Assertions.assertEquals(1, audioFeeder.getCurrentTrackCount());
		}
	}

	@Test
	void MoveTrackWhenAtEndOfFileSystem() {
		AudioFeeder audioFeeder = repository.findById(252L).orElseThrow();
		MoveTrackControl moveTrackControl = new MoveTrackControl(
			PreferredRandomSettings.builder().regularlyTrackChange(false).build(),
			audioFeeder
		);

		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(feedBMock())) {
			// Initial state verification
			assertNotNull(audioFeeder.getAudioIOFileManager());

			// Should throw exception when we're at the end of files
			assertThrows(JsonReturnError.class, moveTrackControl::increaseFileNo);
		}
	}
}
