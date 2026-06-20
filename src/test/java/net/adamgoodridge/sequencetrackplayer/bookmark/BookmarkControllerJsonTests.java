package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class BookmarkControllerJsonTests extends AbstractSpringBootTest {

    private static final String BASE_URL = "/bookmark/json";
    @Autowired
    private MockMvc mockMvc;

    // keep test fixtures minimal and explicit
    @Autowired
    private BookmarkedAudioRepository bookmarkedAudioRepository;

    @Autowired
    private AudioFeederRepository audioFeederRepository;

    @Autowired
    private FeedService feedService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Populate bookmarks from JSON fixture (includes id 63c3d88ced2a9155d57e9252)
        new BookmarkRepositoryMock().fillWithMockData(bookmarkedAudioRepository);

        // Populate audio feeders from JSON fixture (includes id 248 with AudioIOFileManager)
        new AudioFeederRepositoryMock().fillWithMockData(audioFeederRepository);
    }

    @Test
    void testAddBookmark() throws Exception {
        int feedTrackIndex = 248;
        String expectedUrl = "//FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
        mockMvc.perform(post(BASE_URL + "/add/" + feedTrackIndex))
               .andExpect(status().is(201))
               .andExpect(jsonPath("$.bookmarkId").exists())
               .andExpect(jsonPath("$.path").value(expectedUrl));
    }

    @Test
    void testDeleteBookmark_Success() throws Exception {
        String bookmarkId = "63c3d88ced2a9155d57e9252";

        mockMvc.perform(delete(BASE_URL + "/delete/" + bookmarkId))
               .andExpect(status().is(204))
               .andExpect(content().string(""));
    }

    @Test
    void testDeleteBookmark_NotFound() throws Exception {
        String bookmarkId = "123";
        String expectedMessage = "Bookmark not found with id: 123";

        mockMvc.perform(delete(BASE_URL + "/delete/" + bookmarkId))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value(expectedMessage));
    }

    @Test
    void testAddBookmark_NoFeedLoaded() throws Exception {
        int feedTrackIndex = 0;
        String expectedMessage = "There's no feed found loaded on the server currently";

        mockMvc.perform(post(BASE_URL + "/add/" + feedTrackIndex))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value(expectedMessage));
    }

    @Test
    void testAddBookmark_NoAudioFeederFound() throws Exception {
        int feedTrackIndex = 1;
        
        mockMvc.perform(post(BASE_URL + "/add/" + feedTrackIndex))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value("There's no feed found loaded on the server currently"));
    }
}