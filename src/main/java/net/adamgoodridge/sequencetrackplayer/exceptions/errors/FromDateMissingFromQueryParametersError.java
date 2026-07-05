package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

public class FromDateMissingFromQueryParametersError extends  BadRequestError {
	public FromDateMissingFromQueryParametersError() {
		super(ErrorMessages.FROM_DATE_MISSING_FROM_QUERY_PARAMETERS);
	}
}
