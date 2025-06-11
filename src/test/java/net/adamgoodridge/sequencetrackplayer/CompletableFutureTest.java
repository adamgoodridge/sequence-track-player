package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.exceptions.GetFeedException;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequest;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
public class CompletableFutureTest {
    @Autowired
    private FeedService feedService;
    @Mock
    private AudioFeederRepository audioFeederRepository;
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        CompletableFuture<String> completableFuture = futureMethod(true);
        //         System.out.println("main");
        while (!completableFuture.isDone()) {
            //             System.out.println("Loading..."); // Ensure this prints while CompletableFuture is running
            await().atMost(1, TimeUnit.SECONDS);
        }
    }

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
    void testLoadingFeed() throws GetFeedException {
        FeedRequest feedRequest = new FeedRequest.Builder()
                .name("FEEDD")
                .feedRequestType(FeedRequestType.RANDOM)
                .build();
        long id = feedService.populateFeed(feedRequest);
        AudioFeeder audioFeeder = new AudioFeeder(feedRequest.getName());
        audioFeeder.setId(id);
        while (feedService.checkAndUpdateStatus(id).getAudioIOFileManager() == null) {
            //             System.out.println("Loading...");
            await().atMost(1, TimeUnit.SECONDS);
        }
        //         System.out.println("Loading complete");

    }
}
