package net.adamgoodridge.sequencetrackplayer.filesystem.directory;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;

import java.util.*;


public interface NasConnectorService {

     String[] listSubFiles(String path);
     List<DataItem> getFiles(String url);
    //https://www.codeproject.com/Articles/5261921/Working-with-Shared-Files-in-Remote-Server-using-S
    //use for feedf and list files
    public String[] listSubFeeds(String path);

    List<DateForCalendarView> listDaysInYears(String path);

    AudioIOFileManager getTrack(String path, FeedRequestType feedRequestType) throws GetFeedError;
    AudioIOFileManager getBookmarkedTrack(String path) throws GetFeedError;

    String logoPath(String feedName);
}
