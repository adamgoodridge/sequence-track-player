package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;



@Document(collection = "bookmarkedAudio")
@SuppressWarnings("unused")
public class BookmarkedAudio {
    @Id
    private String bookmarkId;

    @Field(name = "path")
    private String path;

    public BookmarkedAudio() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BookmarkedAudio(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getBookmarkId() {

        return bookmarkId;
    }

    public void setBookmarkId(String id) {
        this.bookmarkId = id;
    }

    public String getFeedName() {
        String feedName = path.split("/")[1];
        if(feedName.equals("FEEDD")) {
            feedName += "-" + path.split("/")[2];
        }
        return feedName;
    }

    public FeedRequest toRequest() {
        return new FeedRequest.Builder()
                .name(getFeedName())
                .path(path)
                .feedRequestType(FeedRequestType.BOOKMARK)
                .build();
    }
    public String nameWithoutExtension() {
        String name = path.split("/")[path.split("/").length - 1];
        return name.substring(0, name.lastIndexOf('.'));
    }
}
