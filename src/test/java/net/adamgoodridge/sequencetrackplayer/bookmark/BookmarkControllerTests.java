package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMockConstruction;
import net.adamgoodridge.sequencetrackplayer.mock.respository.AudioFeederRepositoryMock;
import net.adamgoodridge.sequencetrackplayer.mock.respository.BookmarkRepositoryMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class BookmarkControllerTests extends AbstractSpringBootTest {
    @Autowired
    private BookmarkController bookmarkController;

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
        LoadClassDef.initializeComponents();
    }

    @Test
    void testGetBookmark_Success() throws DataBaseError {
        String bookmarkID = "63c3d88ced2a9155d57e9252";

        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<String, String[]>() {{
                    put("/mnt/path/test/2023/2023-02_February/2023-02-01_Wednesday",
                            new String[]{"TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3"});
                }})) {
            String response = bookmarkController.getBookmark(bookmarkID);
            assertEquals("redirect:/feed/get/1", response);
        }
    }

    @Test
    public void testListBookmarks() {
        String response = bookmarkController.list(false, model);
        assertEquals("list-bookmark-feeds", response);
        verify(model).addAttribute(eq("isDeleted"), eq(false));
        verify(model).addAttribute(eq("bookmarks"), anyList());
    }
    @Test
    public void testListBookmarksDeleteTrue() {
        String response = bookmarkController.list(true, model);
        assertEquals("list-bookmark-feeds", response);
        verify(model).addAttribute(eq("isDeleted"), eq(true));
        verify(model).addAttribute(eq("bookmarks"), anyList());
    }
}
