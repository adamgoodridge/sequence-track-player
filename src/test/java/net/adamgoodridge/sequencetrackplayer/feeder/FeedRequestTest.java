package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.constanttext.ConstantTextFileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedRequestTest {

    @Mock
    private ConstantTextFileSystem fileSystem;
    private FeedRequest feedRequest;

    @BeforeEach
    void setUp() {
        feedRequest = new FeedRequest.Builder()
                .name("TestFeed")
                .path("/test/path")
                .feedId(1L)
                .feedRequestType(FeedRequestType.BOOKMARK)
                .build();
    }

    @Test
    void getName_ShouldReturnCorrectName() {
        assertEquals("TestFeed", feedRequest.getName());
    }

    @Test
    void getPath_ShouldReturnCorrectPath() {
        assertEquals("/test/path", feedRequest.getPath());
    }

}
