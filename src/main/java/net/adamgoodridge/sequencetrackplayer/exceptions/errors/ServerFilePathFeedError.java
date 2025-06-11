package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

public class ServerFilePathFeedError extends ShufflePlayerError {
    private final long feedId;
    private final String feedName;
    public ServerFilePathFeedError(String message, long feedId, String feedName)
    {
        super(message);
        this.feedId = feedId;
        this.feedName = feedName;
    }
    
    public long getFeedId() {
        return feedId;
    }
    
    public String getFeedName() {
        return feedName;
    }
}
