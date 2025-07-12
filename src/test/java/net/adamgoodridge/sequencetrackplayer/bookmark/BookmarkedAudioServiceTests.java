package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.*;
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
        Optional<BookmarkedAudio> bookmarkedAudio = bookmarkedAudioService.getById(id);
        assertTrue(bookmarkedAudio.isPresent());
        assertEquals(id, bookmarkedAudio.get().getBookmarkId());
    }
    @Test
    void testGetByIdDoesntExist() {
        String id = "63c3d88ced2a9155d57e9253"; // ID that doesn't exist
        Optional<BookmarkedAudio> bookmarkedAudio = bookmarkedAudioService.getById(id);
        assertTrue(bookmarkedAudio.isEmpty());
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
        assertEquals("/FEEDE/2019/2019-03/2019-03-06_Wednesday/AUDIOFILE_FEEDE_2019-03-06_Wednesday_11-34_TO_12-04_727393754008.mp3", bookmarkedAudio.getPath());
    }

    @Test
    void testDelete() {
        // Given
        String id = "63c3d88ced2a9155d57e9252"; // ID to delete
        String name  = "/mnt/path/test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3";

        // When
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found with id: " + id));
        bookmarkedAudio.setBookmarkId(id);
        bookmarkedAudioService.delete(bookmarkedAudio);
        Optional<BookmarkedAudio> deletedBookmark = bookmarkedAudioService.getById(id);

        // Then
        assertEquals(name, bookmarkedAudio.getPath());
        assertTrue(deletedBookmark.isEmpty(), "Bookmark should be deleted");
    }

}
