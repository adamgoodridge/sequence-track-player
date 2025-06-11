package net.adamgoodridge.sequencetrackplayer.thymeleaf;


import java.util.*;

@SuppressWarnings("unused")
public class AudioFeederItemSorter implements Comparator<AudioFeederItemDTO> {
    
    
    @Override
    public int compare(AudioFeederItemDTO a, AudioFeederItemDTO b) {
        return a.getFeedName().compareTo(b.getFeedName());
    }
}
