package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import net.adamgoodridge.sequencetrackplayer.utils.GenerateMp3Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class GenerateMp3IndexTest extends AbstractSpringBootTest {

    private List<DataItem> children;

    @BeforeEach
    void setUp() {
        children = new ArrayList<>();
        // Setup test data
        children.add(new DataItem("/test/share/10_00-30.mp3"));
        children.add(new DataItem("/test/share/11_15-45.mp3"));
        children.add(new DataItem("/test/share/12_30-15.mp3"));
        children.add(new DataItem("/test/share/13_45-20.mp3"));
    }

    @Test
    void testRandomSelectionWhenTimeNotSpecified() {
        GenerateMp3Index generator = new GenerateMp3Index(children, -1);
        int index = generator.compute();
        assertTrue(index >= 0 && index < children.size());
    }

    @Test
    void testTimeBasedSelection() {
        GenerateMp3Index generator = new GenerateMp3Index(children, 11);
        int index = generator.compute();
        assertEquals(1, index); // Should find the 11_15-45.mp3 file
    }

    @Test
    void testRolloverToNextHourWhenTimeNotFound() {
        GenerateMp3Index generator = new GenerateMp3Index(children, 14);
        int index = generator.compute();
        assertTrue(index >= 0 && index < children.size());
    }

}