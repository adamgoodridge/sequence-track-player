package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.ErrorMessages;

public class InvalidSessionIdError extends BadRequestError {
    public InvalidSessionIdError() {
        super(ErrorMessages.INVALID_SESSION_ID);
    }

}
