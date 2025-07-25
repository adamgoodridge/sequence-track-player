package net.adamgoodridge.sequencetrackplayer.filesystem.directory;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;

import java.util.*;

public interface IDirectoryService {
    public List<DataItem> getSubFiles(String name) throws GetFeedError;
}
