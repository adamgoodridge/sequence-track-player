package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.GetFeedError;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequest;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
public class CompletableFutureTest {
    @Autowired
    private FeedService feedService;
    @Mock
    private AudioFeederRepository audioFeederRepository;

    private static CompletableFuture<String> futureMethod(boolean throwError) {
        return CompletableFuture.supplyAsync(() -> {
                if (throwError)
                    throw new RuntimeException("Error occurred");
                //                 System.out.println("inside thread started");
                await().atMost(5, TimeUnit.SECONDS);
            return "hi";
        }).orTimeout(40, TimeUnit.SECONDS);
    }
    @Test
    void testLoadingFeed() throws GetFeedError {

        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            FeedRequest feedRequest = new FeedRequest.Builder()
                    .name("FeedA")
                    .feedRequestType(FeedRequestType.RANDOM)
                    .build();
            long id = feedService.populateFeed(feedRequest);
            AudioFeeder audioFeeder = new AudioFeeder(feedRequest.getName());
            audioFeeder.setId(id);
            while (feedService.checkAndUpdateStatus(id).getAudioIOFileManager() == null) {
                await().atMost(1, TimeUnit.SECONDS);
            }
            audioFeeder = feedService.getAudioFeeder(id).get();
            assert audioFeeder.getAudioIOFileManager() != null;
            assert audioFeeder.getAudioIOFileManager().getFile() != null;
        }
    }
}
