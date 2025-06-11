package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
public class FeedServiceTests extends AbstractSpringBootTest {
    @Autowired
    private FeedService feedService;
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
    public void testAllFeed() throws IOException {
        boolean isEmpty = feedService.feedNames().length == 0;
         assertFalse(isEmpty);
    }
    }
