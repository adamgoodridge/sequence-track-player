package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class ServerError extends ShufflePlayerError {
    public ServerError(String message) {
        super(message);
    }
    public ServerError(ErrorMessages errorMessage) {
        super(errorMessage.getMessage());
    }
}
