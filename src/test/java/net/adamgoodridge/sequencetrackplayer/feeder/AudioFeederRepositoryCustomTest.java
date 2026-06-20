package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AudioFeederRepositoryCustomTest extends AbstractSpringBootTest {
	@Autowired
	private AudioFeederRepository audioFeederRepository;
	@Test
	void getFeedNameWithinHolidayPeriod_shouldReturnCorrectFeedNameAndFileName() {
		// Arrange
		AudioFeeder nonHolidayaudioFeeder = new AudioFeeder("FeedA");
		nonHolidayaudioFeeder.setId(1);
		AudioIOFileManager nonHolidayfileManager = new AudioIOFileManager(List.of(
				new DataItem("Test_2023-12-19_Test.mp3")
		), 0);
		nonHolidayaudioFeeder.setAudioIOFileManager(nonHolidayfileManager);
		audioFeederRepository.save(nonHolidayaudioFeeder);
		AudioFeeder audioFeeder = new AudioFeeder("FeedA");
		audioFeeder.setId(1);
		AudioIOFileManager fileManager = new AudioIOFileManager(List.of(
				new DataItem("Test_2023-12-21_Test.mp3")
		), 0);
		audioFeeder.setAudioIOFileManager(fileManager);
		audioFeederRepository.save(audioFeeder);

		// Act
		AudioFeeder feed = audioFeederRepository.getRandomByFeedNameInHolidayPeriod("FeedA");


		// Assert
		assertNotNull(feed, "Feed should not be null");
		assertEquals("Feed name should match","FeedA", feed.getFeedName());
		assertEquals("File name should match","/mnt/path/Test_2023-12-21_Test.mp3", feed.getAudioIOFileManager().getFiles().get(0).getFullPath());
	}

	@Test
	void getFeedNameWithNoHolidayPeriod_shouldReturnNull() {
		// Arrange
		AudioFeeder nonHolidayaudioFeeder = new AudioFeeder("FeedA");
		nonHolidayaudioFeeder.setId(1);
		AudioIOFileManager nonHolidayfileManager = new AudioIOFileManager(List.of(
				new DataItem("Test_2023-12-19_Test.mp3")
		), 0);
		nonHolidayaudioFeeder.setAudioIOFileManager(nonHolidayfileManager);
		audioFeederRepository.save(nonHolidayaudioFeeder);

		// Act
		AudioFeeder feed = audioFeederRepository.getRandomByFeedNameInHolidayPeriod("FeedA");


		// Assert
		assertNull(feed, "Feed should be null");
	}
}
