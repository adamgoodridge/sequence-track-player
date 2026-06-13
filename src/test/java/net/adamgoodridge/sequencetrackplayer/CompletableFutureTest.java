package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.GetFeedError;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequest;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class CompletableFutureTest extends AbstractSpringBootTest {
    @Autowired
    private FeedService feedService;
    @Mock
    private AudioFeederRepository audioFeederRepository;

    @BeforeEach
    void setUp() {
        LoadClassDef.initializeComponents();
        MockitoAnnotations.openMocks(this);
    }

    private static CompletableFuture<String> futureMethod(boolean throwError) {
        return CompletableFuture.supplyAsync(() -> {
            if (throwError)
                throw new RuntimeException("Error occurred");
            await().atMost(5, TimeUnit.SECONDS);
            return "hi";
        }).orTimeout(40, TimeUnit.SECONDS);
    }

    @Test
    void testLoadingFeed() throws GetFeedError {
        try (MockedStatic<FileListSubFileWrapper> mockedStatic = Mockito.mockStatic(FileListSubFileWrapper.class)) {
            mockedStatic.when(() -> FileListSubFileWrapper.wrap("/mnt/path/FeedA"))
                    .thenReturn(new String[]{"2022"});
            mockedStatic.when(() -> FileListSubFileWrapper.wrap("/mnt/path/FeedA/2022"))
                    .thenReturn(new String[]{"2022-01_January"});
            mockedStatic.when(() -> FileListSubFileWrapper.wrap("/mnt/path/FeedA/2022/2022-01_January"))
                    .thenReturn(new String[]{"FEEDA_AUDIOFILE_2022-01-01_Saturday_09-00-00.mp3"});

            FeedRequest feedRequest = new FeedRequest.Builder()
                    .name("FeedA")
                    .feedRequestType(FeedRequestType.RANDOM)
                    .build();
            long id = feedService.populateFeed(feedRequest);
            AudioFeeder audioFeeder = feedService.checkAndUpdateStatus(id);
            assert audioFeeder.getAudioIOFileManager() != null;
            assert audioFeeder.getAudioIOFileManager().getFile() != null;
        }
    }
}
