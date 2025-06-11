package net.adamgoodridge.sequencetrackplayer.exceptions;

public class ShufflePlayerException extends Exception {
    public ShufflePlayerException() {
    }


    public ShufflePlayerException(String message) {
        super(message);
    }
    public ShufflePlayerException(Exception e) {
        super(e);
    }
}
