package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class InvalidStatisticsDateTooManyParametersError extends BadRequestError {
	public InvalidStatisticsDateTooManyParametersError() {
		super(ErrorMessages.INVALID_STATISTICS_DATE_QUERY_PARAMETERS);
	}
}
