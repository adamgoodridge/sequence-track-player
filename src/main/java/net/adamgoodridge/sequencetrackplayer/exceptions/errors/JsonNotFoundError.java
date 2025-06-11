package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

//for exception needs to be in json
public class JsonNotFoundError extends ShufflePlayerError {
    public JsonNotFoundError(String message) {
        super(message);
    }
}
