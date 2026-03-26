package net.adamgoodridge.sequencetrackplayer.filesystem;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.GetFeedError;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.NotFoundError;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManager;
import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMockConstruction;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.DateForCalendarView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NasConnectorFileSystemTest extends AbstractSpringBootTest {
    @Mock
    private File file;
    private NasConnectorFileSystem nasConnector;

    private ListAppender<ILoggingEvent> logAppender;
    private ch.qos.logback.classic.Logger nasConnectorLogger;

    @BeforeEach
    void setUp() {
        LoadClassDef.initializeComponents();
        nasConnector = new NasConnectorFileSystem();

        nasConnectorLogger = (ch.qos.logback.classic.Logger)
                LoggerFactory.getLogger(NasConnectorFileSystem.class.getName());
        logAppender = new ListAppender<>();
        logAppender.start();
        nasConnectorLogger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        nasConnectorLogger.detachAppender(logAppender);
    }
    private String[] listSubFilesWithFileMock(String path, Map<String, String[]> fileStructure) {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				(HashMap<String, String[]>) fileStructure
		)) {
            return nasConnector.listSubFiles(path);
        }
    }
    @Test
    void listSubFiles_ShouldReturnCorrectFiles() {
        // given
        Map<String, String[]> fileStructure = new HashMap<>() {{
            put("/mnt/path/test/2023/2023-02_February",
                    new String[]{"2023-02-01_Wednesday",
                            "2023-02-02_Thursday",
                            "2023-02-03_Friday"});
        }};
        // then
        String[] result = listSubFilesWithFileMock("/mnt/path/test/2023/2023-02_February", fileStructure);

            assertArrayEquals(new String[]{
                    "2023-02-01_Wednesday",
                    "2023-02-02_Thursday",
                    "2023-02-03_Friday"}, result);
        }

    @Test
    void listSubFeeds_ShouldThrowNotFoundError_WhenDirectoryEmpty() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path", new String[]{});
                }})) {
            assertEquals(0, nasConnector.listSubFeeds("/mnt/path").length);
        }
    }

    @Test
    void listSubFeeds_ShouldReturnFeeds() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/FeedA", new String[]{"FeedA-A","FeedA-B","FeedA-C"});
                }})) {
            String[] result = nasConnector.listSubFeeds("/mnt/path/FeedA");
            assertArrayEquals(new String[]{
                    "/mnt/path/FeedA/FeedA-A",
                    "/mnt/path/FeedA/FeedA-B",
                    "/mnt/path/FeedA/FeedA-C"
            }, result);
        }
    }

    @Test
    void getFiles_ShouldReturnDataItems() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/test/2023/2023-11_November/2023-11-19_Sunday",
                            new String[]{"TEST_AUDIOFILE_2023-11-19_Sunday_12-44-14.mp3","TEST_AUDIOFILE_2023-11-19_Sunday_13-45-00.m4a","TEST_AUDIOFILE_2023-11-19_Sunday_14-44-14.mp3"});
                }})) {
            String path = "/mnt/path/test/2023/2023-11_November/2023-11-19_Sunday";
            List<DataItem> result = nasConnector.getFiles(path);
            assertEquals(3, result.size());
            String expectedFirstFileName = "TEST_AUDIOFILE_2023-11-19_Sunday_12-44-14.mp3";
            assertEquals(expectedFirstFileName, result.get(0).getFileName());
        }
    }


    @Test
    void getBookmarkedTrack_ShouldReturnCorrectTrack() throws GetFeedError {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path",
                            new String[]{"test"});
                    put("/mnt/path/test",
                            new String[]{"2023"});
                    put("/mnt/path/test/2023",
                            new String[]{"2023-02_February"});
                    put("/mnt/path/test/2023/2023-02_February",
                            new String[]{"2023-02-01_Wednesday"});
                    put("/mnt/path/test/2023/2023-02_February/2023-02-01_Wednesday",
                            new String[]{"TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3"});
                }})) {
            AudioIOFileManager result = nasConnector.getBookmarkedTrack("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3");
            assertNotNull(result);
            assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
        }
    }
    @Test
    void getTrack_ShouldThrowException_WhenFileNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path", new String[]{});
                }})) {
            assertThrows(GetFeedError.class,
                    () -> nasConnector.getTrack("nonexistent/path", FeedRequestType.BOOKMARK));
        }
    }

    @Test
    void logoPath_ShouldReturnCorrectPath() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/exclude/logos", new String[]{"FeedB.png"});
                }})) {
            String result = nasConnector.logoPath("FeedB");
            assertEquals("FeedB", result);
        }
    }
    @Test
    void logoPath_ShouldReturnNullWhenNotExist() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/exclude/logos", new String[]{"FeedB.png"});
                }})) {
            String result = nasConnector.logoPath("FeedC");
            assertEquals("", result);
        }
    }
    @Test
    void listDaysInYears_ShouldReturnAllDays() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/test/2023",
                            new String[]{"2023-02_February"});
                    put("/mnt/path/test/2023/2023-02_February", new String[]{"2023-02-01_Wednesday"});
                }})) {
            List<DateForCalendarView> result = nasConnector.listDaysInYears("/mnt/path/test/2023");
            assertFalse(result.isEmpty());
            assertTrue(result.stream().anyMatch(d -> d.date().equals("2023-02-01")));
        }
    }

    @Test
    void logoPath_ShouldReturnEmptyString_WhenLogoNotFound() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/NonExistentFeed", new String[]{});
                }})) {
            String result = nasConnector.logoPath("NonExistentFeed");
            assertEquals("", result);
        }
    }

    @Test
    void checkIfDirEmpty_ShouldThrowNotFoundError_AndLogError_WhenSubFilesNull() {
        // FileSystemMockConstruction returns null subFiles when the path is not in the map
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(new HashMap<>())) {
            NotFoundError thrown = assertThrows(NotFoundError.class,
                    () -> nasConnector.getFiles("/mnt/path/missing-dir"));

            assertEquals("No feeds cannot be found.", thrown.getMessage());

            List<ILoggingEvent> logs = logAppender.list;
            assertEquals(1, logs.size());
            assertEquals(Level.ERROR, logs.get(0).getLevel());
            assertEquals("no feeds cannot be found when trying to listing their names.", logs.get(0).getMessage());
        }
    }

    @Test
    void checkIfDirEmpty_ShouldNotLog_WhenSubFilesPresent() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
                new HashMap<>() {{
                    put("/mnt/path/FeedA/2023", new String[]{"track.mp3"});
                }})) {
            List<DataItem> result = nasConnector.getFiles("/mnt/path/FeedA/2023");

            assertFalse(result.isEmpty());
            assertEquals("track.mp3", result.get(0).getFileName());
            assertTrue(logAppender.list.isEmpty(), "No error should be logged when directory is not empty");
        }
    }

}