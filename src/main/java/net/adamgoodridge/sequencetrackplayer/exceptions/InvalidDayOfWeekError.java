package net.adamgoodridge.sequencetrackplayer.exceptions;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;

public class InvalidDayOfWeekError extends ShufflePlayerError {

    public InvalidDayOfWeekError(String value) {
        super("Unknown day of week: " + value);
    }

}