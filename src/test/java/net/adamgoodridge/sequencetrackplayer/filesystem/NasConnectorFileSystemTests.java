package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.exceptions.GetFeedException;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.NotFoundError;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManager;
import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMock;
import net.adamgoodridge.sequencetrackplayer.settings.PreferredRandomSettings;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.DateForCalendarView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NasConnectorFileSystemTests extends AbstractSpringBootTest {
    private NasConnectorFileSystem nasConnector;

    @BeforeEach
    void setUp() {
        nasConnector = new NasConnectorFileSystem();
    }

    @Test
    void listSubFiles_ShouldReturnCorrectFiles() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            String[] result = nasConnector.listSubFiles("/mnt/path/test/2023/2023-02_February");
            assertArrayEquals(new String[]{
                    "2023-02-01_Wednesday",
                    "2023-02-02_Thursday",
                    "2023-02-03_Friday"}, result);
        }
    }

    @Test
    void listSubFeeds_ShouldThrowNotFoundError_WhenDirectoryEmpty() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            assertThrows(NotFoundError.class, () -> nasConnector.listSubFeeds("nonexistent/path"));
        }
    }

    @Test
    void listSubFeeds_ShouldReturnFeeds() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            String[] result = nasConnector.listSubFeeds("/mnt/path/");
            assertArrayEquals(new String[]{
                    "/mnt/path/FeedA",
                    "/mnt/path/FeedB",
                    "/mnt/path/FeedC",
                    "/mnt/path/test"}, result);
        }
    }

    @Test
    void getFiles_ShouldReturnDataItems() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            List<DataItem> result = nasConnector.getFiles("server/test/2023/2023-02_February/2023-02-01_Wednesday");
            assertFalse(result.isEmpty());
            assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.get(0).getFileName());
        }
    }
    @Test
    void getBookmarkedTrack_ShouldReturnCorrectTrack() throws GetFeedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            AudioIOFileManager result = nasConnector.getBookmarkedTrack("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3");
            assertNotNull(result);
            assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
        }
    }

    @Test
    void getTrack_ShouldThrowException_WhenFileNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            assertThrows(GetFeedException.class,
                    () -> nasConnector.getTrack("nonexistent/path", FeedRequestType.BOOKMARK));
        }
    }

    @Test
    void logoPath_ShouldReturnCorrectPath() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            String result = nasConnector.logoPath("FeedB");
            assertEquals("FeedB", result);
        }
    }

    @Test
    void listDaysInYears_ShouldReturnAllDays() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            List<DateForCalendarView> result = nasConnector.listDaysInYears("/mnt/path/test/2023");
            assertFalse(result.isEmpty());
            assertTrue(result.stream().anyMatch(d -> d.date().equals("2023-02-01")));
            assertEquals(7, result.size()); // Assuming there are 7 days in 2023 in the mock data
        }
    }
    @Test
    void getFiles_ShouldThrowNotFoundError_WhenDirectoryEmpty() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            assertThrows(NotFoundError.class, () -> nasConnector.getFiles("server/nonexistent/path"));
        }
    }

    @Test
    void logoPath_ShouldReturnEmptyString_WhenLogoNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            String result = nasConnector.logoPath("NonExistentFeed");
            assertEquals("", result);
        }
    }

}
