package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.ErrorMessages;

public class TodayStatisticNotFoundError extends JsonNotFoundError {
	public TodayStatisticNotFoundError() {
		super(ErrorMessages.TODAY_STATISTICS_NOT_FOUND.getMessage());
	}

	public TodayStatisticNotFoundError(String message) {
		super(message);
	}
}
