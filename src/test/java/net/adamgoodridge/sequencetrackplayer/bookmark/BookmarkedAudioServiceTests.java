package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.ui.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkedAudioServiceTests extends AbstractSpringBootTest {
    @Autowired
    private BookmarkedAudioService bookmarkedAudioService;

    @Autowired
    private BookmarkedAudioRepository bookmarkedAudioRepository;
    @Autowired
    private AudioFeederRepository audioFeederRepository;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        new BookmarkRepositoryMock().fillWithMockData(bookmarkedAudioRepository);
        new AudioFeederRepositoryMock().fillWithMockData(audioFeederRepository);
    
    }

    @Test
    void testGetAll() {
        List<BookmarkedAudio> bookmarkedAudios = bookmarkedAudioService.getAll();
        assertNotNull(bookmarkedAudios);
        assertEquals(3, bookmarkedAudios.size()); // Verify size matches JSON data
    }

    @Test
    void testGetById() {
        String id = "63c3d88ced2a9155d57e9252";
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getById(id);
        assertNotNull(bookmarkedAudio);
        assertEquals(id, bookmarkedAudio.getBookmarkId());
    }
    @Test
    void testGetByIdDoesntExist() {
        String id = "63c3d88ced2a9155d57e9253"; // ID that doesn't exist
        JsonNotFoundError exception = assertThrows(JsonNotFoundError.class, () -> {
            bookmarkedAudioService.getById(id);
        });
        String expectedMessage = "Bookmark not found with id: " + id;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void testAdd() {
        // Given
        long feedTrackIndex = 248;
        // When
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.add(feedTrackIndex);

        // Then
        assertNotNull(bookmarkedAudio, "BookmarkedAudio should not be null after adding");
        String actualPath = bookmarkedAudio.getPath();
        String expectedPath = "https://testplayer.adamgoodridge.net/FeedB/2023/2023-07_July/2023-07-20_Thursday/FEEDB_AUDIOFILE_2023-07-20_Thursday_14-15-00.mp3";
        assertEquals(expectedPath, actualPath, "The path of the bookmarked audio does not match the expected value");
    }

    @Test
    void testAdd_FeedExistsButNoAudioIOFileManager() {
        // Given: save a feeder with no audioInfo (AudioIOFileManager is null)
        AudioFeeder feeder = new AudioFeeder();
        feeder.setId(999L);
        feeder.setFeedName("testFeed");
        audioFeederRepository.save(feeder);

        // When / Then: add() should throw because AudioIOFileManager is null
        JsonNotFoundError exception = assertThrows(JsonNotFoundError.class,
                () -> bookmarkedAudioService.add(999L));
        assertEquals("There's no feed found loaded on the server currently", exception.getMessage());
    }

    @Test
    void testGetBookedMarked() {
        // Create complex DataItem objects
        DataItem file1 = new DataItem("/FEEDE/2019/2019-03/2019-03-06_Wednesday/AUDIOFILE_FEEDE_2019-03-06_Wednesday_11-34_TO_12-04_727393754008.mp3");
        DataItem file2 = new DataItem("/FEEDE/2019/2019-03/2019-03-06_Wednesday/AUDIOFILE_FEEDE_2019-03-06_Wednesday_11-34_TO_12-04_727393754009.mp3");
        List<DataItem> files = List.of(file1, file2);

        // Create AudioIOFileManager object with nested structure
        AudioIOFileManager audioInfo = new AudioIOFileManager(files, 0);

        // Test the service method
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getBookedMarked(audioInfo);
        assertNotNull(bookmarkedAudio);
    }
}
