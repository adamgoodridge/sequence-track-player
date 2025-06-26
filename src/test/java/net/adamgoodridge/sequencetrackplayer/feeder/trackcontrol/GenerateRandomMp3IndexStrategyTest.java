package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

class GetMp3IndexByRandomStrategyTest extends AbstractSpringBootTest {

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



}