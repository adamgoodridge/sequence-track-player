package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.filesystem.NasConnectorFileSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class NasConnectorServiceWindowsTests {
    @Autowired
    private NasConnectorFileSystem nasConnectorServiceWindows;
    @Test
    public void feedBPathExist() {
        String feedName = "FEEDE";
        String expected = feedName + ".png";
        String actual = nasConnectorServiceWindows.logoPath(feedName);
        assertEquals(expected, actual);
    }
    @Test
    public void pathNotExist() {
        String feedName = "FEEDG";
        String expected = "";
        String actual = nasConnectorServiceWindows.logoPath(feedName);
        assertEquals(expected, actual);
    }
    @Test
    public void pathExistWithThreeForwardSlashes() {
        String feedName = "FEEDD/SUBFEEDA/10325_SUBFEEDA_North-North";
        String expected = "FEEDD-SUBFEEDA.png";
        String actual = nasConnectorServiceWindows.logoPath(feedName);
        assertEquals(expected, actual);
    }
    @Test
    public void pathExistWithTwoForwardSlashes() {
        String feedName = "FEEDD/SUBFEEDA";
        String expected = "FEEDD-SUBFEEDA.png";
        String actual = nasConnectorServiceWindows.logoPath(feedName);
        assertEquals(expected, actual);
    }
}
