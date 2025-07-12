package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.respository.AudioFeederRepositoryMock;
import net.adamgoodridge.sequencetrackplayer.mock.respository.BookmarkRepositoryMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class BookmarkControllerJsonTests extends AbstractSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookmarkedAudioRepository bookmarkedAudioRepository;
    
    @Autowired
    private AudioFeederRepository audioFeederRepository;



    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        new BookmarkRepositoryMock().fillWithMockData(bookmarkedAudioRepository);
        new AudioFeederRepositoryMock().fillWithMockData(audioFeederRepository);
    }

    @Test
    void testAddBookmark() throws Exception {
        int feedTrackIndex = 248;
        String expectedPath = "/TEST/FEEDC/2023/2023-11_November/2023-11-19_Sunday/FEEDC_AUDIOFILE_2023-11-19_Sunday_13-45-21.mp3";
        
        mockMvc.perform(delete("/bookmark/json/add/" + feedTrackIndex))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.bookmarkId").exists())
                .andExpect(jsonPath("$.feedTrackIndex").value(feedTrackIndex))
               .andExpect(jsonPath("$.path").value(expectedPath));
    }

    @Test
    void testDeleteBookmark_Success() throws Exception {
        String bookmarkId = "63c3d88ced2a9155d57e9252";
        
        mockMvc.perform(delete("/bookmark/json/delete/" + bookmarkId))
               .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    @Test
    void testDeleteBookmark_NotFound() throws Exception {
        String bookmarkId = "123";
        
        mockMvc.perform(delete("/bookmark/json/delete/" + bookmarkId))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value("Bookmark id (123) cannot find in the database."));
    }

    @Test
    void testAddBookmark_NoFeedLoaded() throws Exception {
        int feedTrackIndex = 0;
        
        mockMvc.perform(delete("/bookmark/json/add/" + feedTrackIndex))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value("There's no feed found loaded on the server currently"));
    }

    @Test
    void testAddBookmark_NoAudioFeederFound() throws Exception {
        int feedTrackIndex = 1;
        
        mockMvc.perform(delete("/bookmark/json/add/" + feedTrackIndex))
               .andExpect(status().is(404))
               .andExpect(jsonPath("$.error").value("There's no feed found loaded on the server currently"));
    }
}
