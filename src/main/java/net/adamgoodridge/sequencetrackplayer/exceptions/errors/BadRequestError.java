package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class BadRequestError extends Error {
	public BadRequestError(ErrorMessages errorMessage) {
		super(errorMessage.getMessage());
	}
}
