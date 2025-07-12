package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
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
        String feedName = getPathWithoutMount().split("/")[0];
        //todo: get subfeed
        if(feedName.equals("FEEDD")) {
            feedName += "-" + getPathWithoutMount().split("/")[2];
        }
        return feedName;
    }

    public FeedRequest toRequest() {
        return new FeedRequest.Builder()
                .name(getFeedName())
                .path(getPathWithoutMount())
                .feedRequestType(FeedRequestType.BOOKMARK)
                .build();
    }
    @Deprecated
    //db migration needed for deprecated method
    public String getPathWithoutMount() {
        String mountPath = ConstantTextFileSystem.getInstance().getSharePath();
        if (path.startsWith(mountPath)) {
            return path.substring(mountPath.length());
        }
        return path;
    }
    /**
     * Returns the name of the file without its extension.
     * For example, if the path is "path/to/file.mp3", it will return "file".
     *
     * @return The name of the file without its extension.
     */
    public String nameWithoutExtension() {
        String name = path.split("/")[path.split("/").length - 1];
        return name.substring(0, name.lastIndexOf('.'));
    }
}
