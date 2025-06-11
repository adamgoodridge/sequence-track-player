package net.adamgoodridge.sequencetrackplayer.feeder;

public enum FeedRequestType {
    BOOKMARK("Bookmark"),RANDOM("value not needed"), OVERWRITE("replace feed id"), NO_OVERWRITE("replace feed id");

    public final String label;

    FeedRequestType(String label) {
        this.label = label;
    }
}
