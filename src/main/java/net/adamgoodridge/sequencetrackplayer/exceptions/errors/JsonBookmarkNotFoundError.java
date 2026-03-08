package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

//for exception needs to be in json
public class JsonBookmarkNotFoundError extends JsonNotFoundError {
    public JsonBookmarkNotFoundError(String id) {
        super("Bookmark not found with id: " + id);
    }
}
