package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class InvalidStatisticsQueryParametersError  extends BadRequestError {
	public InvalidStatisticsQueryParametersError() {
		super(ErrorMessages.INVALID_STATISTICS_QUERY_PARAMETERS);

	}
}
