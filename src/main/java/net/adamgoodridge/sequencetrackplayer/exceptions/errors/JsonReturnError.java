package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

//for exception needs to be in json
public class JsonReturnError extends ShufflePlayerError {
    private String feedName;
    public JsonReturnError(String message) {
        super(message);
    }

    public JsonReturnError(String message, String feedName) {
        super(message);
        this.feedName = feedName;
    }

    public String getFeedName() {
        return feedName;
    }
}
