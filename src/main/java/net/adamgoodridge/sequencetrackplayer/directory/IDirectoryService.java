package net.adamgoodridge.sequencetrackplayer.directory;

import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;

import java.util.*;

public interface IDirectoryService {
    public List<DataItem> getSubFiles(String name) throws GetFeedException;
}
