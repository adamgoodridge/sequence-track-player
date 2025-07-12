package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.filesystem.NasConnectorFileSystem;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.GetFeedError;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.NotFoundError;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManager;
import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMockConstruction;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.DateForCalendarView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NasConnectorFileSystemTest extends AbstractSpringBootTest {
    @Mock
    private File file;
    private NasConnectorFileSystem nasConnector;

    @BeforeEach
    void setUp() {
        LoadClassDef.initializeComponents();
        nasConnector = new NasConnectorFileSystem();
    }
    private String[] listSubFilesWithFileMock(String path) {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            return nasConnector.listSubFiles(path);
        }
    }
    @Test
    void listSubFiles_ShouldReturnCorrectFiles() {
        // then
        String[] result = listSubFilesWithFileMock("/mnt/path/test/2023/2023-02_February");

            assertArrayEquals(new String[]{
                    "2023-02-01_Wednesday",
                    "2023-02-02_Thursday",
                    "2023-02-03_Friday"}, result);
        }

    @Test
    void listSubFeeds_ShouldThrowNotFoundError_WhenDirectoryEmpty() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            assertThrows(NotFoundError.class, () -> nasConnector.listSubFeeds("nonexistent/path"));
        }
    }

    @Test
    void listSubFeeds_ShouldReturnFeeds() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            String[] result = nasConnector.listSubFeeds("/");
            assertArrayEquals(new String[]{
                    "emptyDir","exclude","FeedA", "FeedB", "FeedC", "test", "testDirUnsorted"
            }, result);
        }
    }

    @Test
    void getFiles_ShouldReturnDataItems() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            String path = "/mnt/path/test/2023/2023-11_November/2023-11-19_Sunday";
            List<DataItem> result = nasConnector.getFiles(path);
            assertEquals(3, result.size());
            String expectedFirstFileName = "TEST_AUDIOFILE_2023-11-19_Sunday_12-44-14.mp3";
            assertEquals(expectedFirstFileName, result.get(0).getFileName());
        }
    }


    @Test
    void getBookmarkedTrack_ShouldReturnCorrectTrack() throws GetFeedError {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            AudioIOFileManager result = nasConnector.getBookmarkedTrack("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3");
            assertNotNull(result);
            assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
        }
    }

    @Test
    void getTrack_ShouldThrowException_WhenFileNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            assertThrows(GetFeedError.class,
                    () -> nasConnector.getTrack("nonexistent/path", FeedRequestType.BOOKMARK));
        }
    }

    @Test
    void logoPath_ShouldReturnCorrectPath() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            String result = nasConnector.logoPath("FeedB");
            assertEquals("FeedB", result);
        }
    }
    @Test
    void logoPath_ShouldReturnNullWhenNotExist() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            String result = nasConnector.logoPath("FeedC");
            assertEquals("", result);
        }
    }
    @Test
    void listDaysInYears_ShouldReturnAllDays() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            List<DateForCalendarView> result = nasConnector.listDaysInYears("/mnt/path/test/2023");
            assertFalse(result.isEmpty());
            assertTrue(result.stream().anyMatch(d -> d.date().equals("2023-02-01")));
        }
    }

    @Test
    void getFiles_ShouldThrowNotFoundError_WhenDirectoryEmpty() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            assertThrows(NotFoundError.class, () -> nasConnector.getFiles("server/nonexistent/path"));
        }
    }

    @Test
    void logoPath_ShouldReturnEmptyString_WhenLogoNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            String result = nasConnector.logoPath("NonExistentFeed");
            assertEquals("", result);
        }
    }

}