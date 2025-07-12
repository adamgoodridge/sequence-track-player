package net.adamgoodridge.sequencetrackplayer;
/*
import net.adamgoodridge.sequencetrackplayer.browser.BreadCrumb;
import net.adamgoodridge.sequencetrackplayer.browser.BrowserUtils;
import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.A
import net.adamgoodridge.sequencetrackplayer.feeder.FeedService;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.respository.AudioFeederRepositoryMock;
import net.adamgoodridge.sequencetrackplayer.mock.respository.settingRepositoryMock;
import net.adamgoodridge.sequencetrackplayer.settings.SettingRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FileBrowserTests {`
    private FeedService feedService;

    @BeforeEach
    void setUp() throws IOException {
        AudioFeederRepository audioFeederRepository = AudioFeederRepositoryMock.get();
        SettingRepository settingRepository = AudioFeederRepositoryMock.get();
        feedService = new FeedService();
        // Setup mock data
        AudioFeeder mockAudioFeeder = new AudioFeeder();
        // Set required properties on mockAudioFeeder

        when(audioFeederRepository.findById(6L))
                .thenReturn(Optional.of(mockAudioFeeder));
    }


    @Test
    public void getChildFolderName() {
        AudioFeeder audioFeeder = feedService.getAudioFeeder(6).get();
        String currentChild = audioFeeder.fileInFolder("GIPPSLANDCFA/2021/2021-03_March/");
        assertEquals("2021-03-25_Thursday", currentChild);
    }

    @Test
    public void getChildFolderNameUtils() {
        AudioFeeder audioFeeder = feedService.getAudioFeeder(6).get();
        List<BreadCrumb> browserFileItems = BrowserUtils.generateFileItems("GIPPSLANDCFA/2021/2021-03_March/r");
        String currentChild = BrowserUtils.fileInFolder(browserFileItems, "GIPPSLANDCFA/2021/2021-03_March/r/");
        assertEquals("d", currentChild);
    }
}*/