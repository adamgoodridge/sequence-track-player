package net.adamgoodridge.sequencetrackplayer.thymeleaf;
//imporcom.adamgoodridge.sequencetrackplayer.bookmark.BookmarkedAudio;

import net.adamgoodridge.sequencetrackplayer.bookmark.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;

/*
TEMP class
 for passing a feed in thymeleaf
 */
public class FeedCurrentPlayingInfoDTO {
       private String trackName;
       private String trackPath;
       private final String feedName;
       private String trackUrl;
       private final long feedId;
       private final String bookmarkId;
       private int trackCurrentCount;
       private int currentPosition;

       public FeedCurrentPlayingInfoDTO(AudioFeeder audioFeeder, long feedId, BookmarkedAudio bookmarkedAudio) {
              this.feedName = audioFeeder.getFeedName();
              this.feedId = feedId;
              this.bookmarkId = (bookmarkedAudio != null) ? bookmarkedAudio.getBookmarkId() : "-1";
             initializeTrackInfo(audioFeeder);
       }

       private void initializeTrackInfo(AudioFeeder audioFeeder) {
              AudioIOFileManager audioFolder = audioFeeder.getAudioIOFileManager();
              if (audioFolder != null) {
                     trackPath = audioFolder.getFile().getFullPath();
                     trackName = audioFolder.getFile().getFileName();
                     trackUrl = audioFolder.getFile().getHref();
                     currentPosition = audioFolder.getCurrentPosition();
                     trackCurrentCount = audioFeeder.getCurrentTrackCount();
              }
       }

       public String getFeedName() {
              return feedName;
       }

       @SuppressWarnings("unused")
      public String getTrackName() {
                  return trackName;
      }
       @SuppressWarnings("unused")
      public String getTrackPath() {
                return trackPath;
       }
       @SuppressWarnings("unused")
       public String getTrackUrl() {
                    return trackUrl;
         }
       @SuppressWarnings("unused")
         public long getFeedId() {
                return feedId;
         }

       @SuppressWarnings("unused")
       public int getCurrentPosition() {
              return currentPosition;
       }
       @SuppressWarnings("unused")
       public String getBookmarkId() {
              return bookmarkId;
       }

       @SuppressWarnings("unused")
       public int getTrackCurrentCount() {
              return trackCurrentCount;
       }
}
