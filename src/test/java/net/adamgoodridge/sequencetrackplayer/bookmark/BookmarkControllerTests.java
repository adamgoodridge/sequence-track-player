package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.DataBaseError;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.ServerError;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMock;
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
    }
//todo: when nas is static
    @Test
    void testGetBookmark_Success() throws DataBaseError {
        String bookmarkID = "63c3d88ced2a9155d57e9252";

        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            String response = bookmarkController.getBookmark(bookmarkID);
            assertEquals("redirect:/feed/get/1", response);
        }
    }

    @Test
    void testGetBookmark_NotFound() {
        String bookmarkID = "123";
        ServerError thrown = assertThrows(ServerError.class, () -> bookmarkController.getBookmark(bookmarkID));
        assertEquals("Bookmark ID doesn't exists in database.", thrown.getMessage());
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
