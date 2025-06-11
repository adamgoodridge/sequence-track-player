package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.GetFeedException;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManager;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMock;
import net.adamgoodridge.sequencetrackplayer.settings.PreferredRandomSettings;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class RandomTrackTest extends AbstractSpringBootTest  {

    @Test
    void compute_ShouldReturnRandomTrack_WhenNoPreferences() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            RandomTrack randomTrack = new RandomTrack("test", PreferredRandomSettings.builder().build());
            AudioIOFileManager result = randomTrack.compute();
            
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().endsWith(".mp3") 
                    || result.getFile().getFileName().endsWith(".m4a"));
        }
    }

    @Test
    void compute_ShouldReturnTrackAtSpecificTime_WhenTimePreferenceSet() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = PreferredRandomSettings.builder()
                    .time(1200)
                    .build();
            RandomTrack randomTrack = new RandomTrack("test", settings);
            
            AudioIOFileManager result = randomTrack.compute();
            
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("12-"));
        }
    }

    @Test
    void compute_ShouldReturnTrackOnSpecificDay_WhenDayPreferenceSet() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = PreferredRandomSettings.builder()
                    .day("Monday")
                    .build();
            RandomTrack randomTrack = new RandomTrack("FeedB", settings);
            
            AudioIOFileManager result = randomTrack.compute();
            
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("Monday"));
        }
    }

    @Test
    void compute_ShouldThrowException_WhenFeedNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            RandomTrack randomTrack = new RandomTrack("nonexistent", PreferredRandomSettings.builder().build());
            
            assertThrows(GetFeedException.class, randomTrack::compute);
        }
    }

    @Test
    void compute_ShouldReturnTrackForSpecificDayAndTime() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = PreferredRandomSettings.builder()
                    .day("Monday")
                    .time(1200)
                    .build();
            RandomTrack randomTrack = new RandomTrack("FeedB", settings);
            
            AudioIOFileManager result = randomTrack.compute();
            
            assertNotNull(result);
            String fileName = result.getFile().getFileName();
            assertTrue(fileName.contains("Monday"));
            assertTrue(fileName.contains("12-"));
        }
    }

    @Test
    void compute_ShouldHandlePathWithLeadingSlash() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            RandomTrack randomTrack = new RandomTrack("/test", PreferredRandomSettings.builder().build());
            
            AudioIOFileManager result = randomTrack.compute();
            
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("TEST_AUDIOFILE"));
        }
    }
}
