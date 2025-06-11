package net.adamgoodridge.sequencetrackplayer;
import net.adamgoodridge.sequencetrackplayer.filesystem.NasConnectorFileSystem;
import net.adamgoodridge.sequencetrackplayer.exceptions.GetFeedException;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioIOFileManager;
import net.adamgoodridge.sequencetrackplayer.feeder.FeedRequestType;
import net.adamgoodridge.sequencetrackplayer.settings.PreferredRandomSettings;
import net.adamgoodridge.sequencetrackplayer.settings.SettingArrayRepository;
import net.adamgoodridge.sequencetrackplayer.settings.SettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NasConnectorFileSystemTest {

    private NasConnectorFileSystem nasConnectorService;

    @Mock
    private SettingRepository settingRepository;

    @Mock
    private SettingArrayRepository settingArrayRepository;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nasConnectorService = new NasConnectorFileSystem();
    }

    @Test
    void getRanTrack_WithValidFeedName_ReturnsAudioIOFileManager() throws GetFeedException {
        // Given
        String feedName = "Test_Folder";
        PreferredRandomSettings preferredRandomSettings = new PreferredRandomSettings.Builder()
            .day("*")
            .time(-1)
            .regularlyTrackChange(false)
            .trackCurrentCount(1)
            .build();
        // When
        AudioIOFileManager result = nasConnectorService.getRanTrack(feedName, preferredRandomSettings);

        // Then
        assertNotNull(result);
        assertNotNull(result.getFile());
    }
    @Test
    void getRanTrack_WithValidFeedNamePreferredDay_ReturnsAudioIOFileManager() throws GetFeedException {
        // Given
        String feedName = "***REMOVED***";
        // When
        AudioIOFileManager result = nasConnectorService.getRanTrack(feedName, new PreferredRandomSettings.Builder()
            .day("Monday")
            .time(-1)
            .regularlyTrackChange(false)
            .trackCurrentCount(99)
            .build());

        // Then
        assertNotNull(result);
        //         System.out.println(result.getFile().getFullPath());
        //         System.out.println(result.getFile().getFullPath());
        assertTrue(result.getFile().getFullPath().contains("Monday"));
    }
    @Test
    void getTrack_WithValidAudioPath_ReturnsAudioIOFileManager() throws GetFeedException {
        // Given
        String path = "/ACT/2020/2020-09_September/2020-09-08_Tuesday/ACT_2020-09-08_Tuesday_23-27_TO_23-57_004.mp3";
        // When
        AudioIOFileManager result = nasConnectorService.getTrack(path, FeedRequestType.BOOKMARK);
        String actualPath = result.getFile().getFullPathLocalFileSystem().replace(ConstantTextFileSystem.getInstance().getSharePath(),"");
        // Then
        assertEquals(path.substring(1),actualPath);
    }
    @Test
    void getTrack_WithInValidAudioPathThrowError(){
        // Given
        String path = "/ACT/2020/2020-09_September/2020-09-08_Tuesday/no.mp3";
        // When
        assertThrows(GetFeedException.class, () -> {
            nasConnectorService.getTrack(path, FeedRequestType.BOOKMARK);
        });

    }
    @Test
    void getRanTrack_WithForwardSlashes_NormalizesPath() throws GetFeedException {
        // Given
        String feedName = "/***REMOVED***/2020";
        // When
        AudioIOFileManager result = nasConnectorService.getRanTrack(feedName, new PreferredRandomSettings.Builder().day("*")
                .time(1200).regularlyTrackChange(false).trackCurrentCount(1).build());

        // Then
        assertNotNull(result);
    }

    @Test
    void getRanTrack_WithEmptyDirectory_ThrowsGetFeedException() {
        // Given
        String feedName = "Empty_Folder";

        // When/Then
        assertThrows(GetFeedException.class, () ->
                nasConnectorService.getRanTrack(feedName, new PreferredRandomSettings.Builder()
                    .day("*")
                    .time(1200)
                    .regularlyTrackChange(true)
                    .trackCurrentCount(100)
                    .build()));
    }

    @Test
    void getRanTrack_WithInvalidPath_ThrowsNotFoundError() {
        // Given
        String feedName = "NonExistentFolder";
        // When/Then
        assertThrows(GetFeedException.class, () ->
            nasConnectorService.getRanTrack(feedName,  new PreferredRandomSettings.Builder()
                    .day("*")
                    .time(1200)
                    .regularlyTrackChange(true)
                    .trackCurrentCount(100)
                    .build()));

    }







}