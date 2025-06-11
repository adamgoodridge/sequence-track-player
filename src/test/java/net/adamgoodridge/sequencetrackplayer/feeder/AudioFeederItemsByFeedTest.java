package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.thymeleaf.AudioFeederItemDTO;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AudioFeederItemsByFeedTest {

    @Test
    void sort_shouldGroupByFeedName_whenMultipleFeedsPresent() {
        // Arrange
        AudioFeeder audioFeeder1 = new AudioFeeder("Feed1");
        audioFeeder1.setId(1);
        AudioFeeder audioFeeder2 = new AudioFeeder("Feed1");
        audioFeeder2.setId(2);
        AudioFeeder audioFeeder3 = new AudioFeeder("Feed2");
        audioFeeder3.setId(3);

        AudioFeederItemDTO item1 = new AudioFeederItemDTO(audioFeeder1);
        AudioFeederItemDTO item2 = new AudioFeederItemDTO(audioFeeder2);
        AudioFeederItemDTO item3 = new AudioFeederItemDTO(audioFeeder3);
        List<AudioFeederItemDTO> unsortedItems = Arrays.asList(item2, item3, item1);

        // Act
        List<AudioFeederItemsByFeed> result = AudioFeederItemsByFeed.sort(unsortedItems);

        // Assert
        assertEquals(2, result.size(), "Should create 2 groups for 2 different feeds");
        
        // Check first group (Feed1)
        AudioFeederItemsByFeed feed1Group = result.get(0);
        assertEquals("Feed1 (2)", feed1Group.headerName(), "First group should be Feed1 with 2 items");
        assertEquals(2, feed1Group.getAudioFeederItems().size(), "Feed1 should have 2 items");
        List<AudioFeederItemDTO> feed1Items = feed1Group.getAudioFeederItems();
        assertEquals(1, feed1Items.get(0).getId(), "First item in Feed1 should have ID 1");
        assertEquals(2, feed1Items.get(1).getId(), "Second item in Feed1 should have ID 2");
        
        // Check second group (Feed2)
        AudioFeederItemsByFeed feed2Group = result.get(1);
        assertEquals("Feed2 (1)", feed2Group.headerName(), "Second group should be Feed2 with 1 item");
        assertEquals(1, feed2Group.getAudioFeederItems().size(), "Feed2 should have 1 item");
        assertEquals(3, feed2Group.getAudioFeederItems().get(0).getId(), "Feed2 item should have ID 3");
    }

    @Test
    void headerName_shouldReturnCorrectFormat() {
        // Arrange
        AudioFeeder audioFeeder1 = new AudioFeeder("TestFeed");
        audioFeeder1.setId(1);
        AudioFeeder audioFeeder2 = new AudioFeeder("TestFeed");
        audioFeeder2.setId(2);

        AudioFeederItemsByFeed feed = new AudioFeederItemsByFeed("TestFeed");
        feed.add(new AudioFeederItemDTO(audioFeeder1));
        feed.add(new AudioFeederItemDTO(audioFeeder2));

        // Act
        String result = feed.headerName();

        // Assert
        assertEquals("TestFeed (2)", result, "Header should show feed name and item count");
    }
}
