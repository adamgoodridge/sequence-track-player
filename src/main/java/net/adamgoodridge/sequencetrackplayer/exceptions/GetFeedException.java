package net.adamgoodridge.sequencetrackplayer.exceptions;

public class GetFeedException extends ShufflePlayerException {
    public GetFeedException(String message) {
        super(message);
    }

    public GetFeedException(Exception cause) {
        super(cause);
    }
}
