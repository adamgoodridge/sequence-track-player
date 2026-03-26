package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeederService;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class FeedServiceTests extends AbstractSpringBootTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private AudioFeederService audioFeederService;
    @Autowired
    private AudioFeederRepository audioFeederRepository;
    @BeforeEach
    void setUp() throws IOException {
        LoadClassDef.initializeComponents();
        new AudioFeederRepositoryMock().fillWithMockData(audioFeederRepository);
    }
    @Test
    public void testSessionIdInsert() {
        AudioFeeder audioFeeder = new AudioFeeder();
        String sessionId = feedService.generateSessionId();
        audioFeeder.setSessionId(sessionId);
        feedService.updateAudioFeeder(audioFeeder);

        Optional<AudioFeeder> optionalAudioFeeder = feedService.getAudioFeeder(audioFeeder.getId());
        assertEquals(sessionId,optionalAudioFeeder.get().getSessionId());
    }
    @Test
    void testAllFeed() {
        try (MockedStatic<FileListSubFileWrapper> mockedStatic = Mockito.mockStatic(FileListSubFileWrapper.class)) {
            mockedStatic.when(() -> FileListSubFileWrapper.wrap("/mnt/path"))
                    .thenReturn(new String[]{"FeedA","FeedB","FeedC","test","testDirUnsorted","emptyDir"});
            String[] feedNames = feedService.feedNames();
            String[] expectedFeedNames = {"emptyDir", "FeedA", "FeedB", "FeedC", "test", "testDirUnsorted"};
            assertArrayEquals(expectedFeedNames, feedNames);
        }
    }
    }
